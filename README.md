# [SDS] CAPSTONE pjt. team 3.


# 주제 - 상품주문시스템


# Table of contents

- [상품주문시스템](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [분석/설계](#분석설계)
  - [구현:](#구현)
    - [구현 개요](#구현-개요)
    - [Application 테스트](#application-테스트)
    - [Saga (Pub-Sub)](#saga-pub-sub)
    - [CQRS]
    - [Compensation & Correlation](#compensation--correlation)
  - [운영](#운영)
    - [Gateway / Ingress]
    - [Deploy / Pipeline]
    - [Autoscale (HPA)](#autoscale-hpa)
    - [Zero-downtime deploy (Readiness probe)](#zerodowntime-deploy-readiness-probe)
    - [Persistence volume / ConfigMap / Secret](#persistence-volumeconfigmapsecret)
    - [Self-healing (liveness probe)](#self-healing-liveness-probe)
    - [Apply Service Mesh](#apply-service-Messh)
    - [Log aggregation / Monitoring](#log-aggregation--monitoring)


# 서비스 시나리오

< 기능 요구사항 >

- 고객
  - 고객 (Customer) 이 상품을 선택하여 주문한다 (Place an Order)
  - 고객이 주문을 취소할 수 있다 (Customer can cancel order)
  - 고객은 언제든지 주문한 내역과 배송 상태를 조회한다 (CQRS - View)

- 상품
  - 주문정보가 들어오면 상품이 준비된다
  - 주문이 취소되면 상품준비를 취소한다

- 배송
  - 주문이 완료되면 배송을 시작한다
  - 주문이 취소되면 배송이 취소된다

- 결재
  - 고객이 결재 한다.
  - 고객이 결재 취소한다

- 알림
  - 주문/배송상태가 바뀌면 고객에게 알림을 보낸다



< 비기능 요구사항 >

 - 결제가 되지 않은 주문건은 거래가 성립되지 않아야 한다
 - 고객이 자주 상점관리에서 확인할 수 있는 배달상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다



# 분석/설계

### 완성된 Event Storming 모형

![image](https://user-images.githubusercontent.com/119907154/217114338-1a0eb084-d6f6-4365-bb1e-87f5b1d7fe61.png)





### 요구사항을 커버리지 검증




![image](https://user-images.githubusercontent.com/487999/79684167-3ecd2f00-826a-11ea-806a-957362d197e3.png)

    - 고객이 메뉴를 선택하여 주문한다 (ok)
    - 고객이 결제한다 (ok)
    - 주문이 되면 주문 내역이 입점상점주인에게 전달된다 (ok)
    - 상점주인이 확인하여 요리해서 배달 출발한다 (ok)

![image](https://user-images.githubusercontent.com/487999/79684170-47256a00-826a-11ea-9777-e16fafff519a.png)
    - 고객이 주문을 취소할 수 있다 (ok)
    - 주문이 취소되면 배달이 취소된다 (ok)
    - 고객이 주문상태를 중간중간 조회한다 (View-green sticker 의 추가로 ok) 
    - 주문상태가 바뀔 때 마다 카톡으로 알림을 보낸다 (?)


### 모델 수정

![image](https://user-images.githubusercontent.com/487999/79684176-4e4c7800-826a-11ea-8deb-b7b053e5d7c6.png)
    
    - 수정된 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증

![image](https://user-images.githubusercontent.com/487999/79684184-5c9a9400-826a-11ea-8d87-2ed1e44f4562.png)

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - 고객 주문시 결제처리:  결제가 완료되지 않은 주문은 절대 받지 않는다는 경영자의 오랜 신념(?) 에 따라, ACID 트랜잭션 적용. 주문와료시 결제처리에 대해서는 Request-Response 방식 처리
        - 결제 완료시 점주연결 및 배송처리:  App(front) 에서 Store 마이크로서비스로 주문요청이 전달되는 과정에 있어서 Store 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
        - 나머지 모든 inter-microservice 트랜잭션: 주문상태, 배달상태 등 모든 이벤트에 대해 카톡을 처리하는 등, 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.







# 구현

## 구현 개요


 - 분석/설계 단계에서 도출된 아키텍처에 따라, 각 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd order
mvn spring-boot:run

cd product
mvn spring-boot:run 

cd pay
mvn spring-boot:run  

cd delivery
mvn spring-boot:run 

cd notice
mvn spring-boot:run 

cd customercenter
mvn spring-boot:run 
```

- 각 서비스내에 도출된 핵심 Aggregate Root 객체 Entity 들을 아래와 같이 생성함( 예시 : order)
- order, pay, product, delivery, notice  등 동일하게 생성

```
package capstoneorderteam.domain;

import capstoneorderteam.domain.Ordered;
import capstoneorderteam.domain.OrderCanceled;
import capstoneorderteam.OrderApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Order_table")
@Data

public class Order  {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Long id;
    private String item;
    private Integer orderQty;
    private String status;
    private Long price;
    private Integer itemcd;

    @PostPersist
    public void onPostPersist(){

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.


        capstoneorderteam.external.Pay pay = new capstoneorderteam.external.Pay();
        
        pay.setPrice(price);
        pay.setStatus(status);
        pay.setItemcd(itemcd);
        pay.setOrderQty(orderQty);
        pay.setOrderId(id);

        // mappings goes here
        OrderApplication.applicationContext.getBean(capstoneorderteam.external.PayService.class)
            .approvePayment(pay);

        Ordered ordered = new Ordered(this);
        ordered.publishAfterCommit();

    }

    @PostUpdate
    public void onPostUpdate(){

        // OrderCanceled orderCanceled = new OrderCanceled(this);
        // orderCanceled.publishAfterCommit();

    }

    @PreRemove
    public void onPreRemove(){
        OrderCanceled orderCanceled = new OrderCanceled(this);
        orderCanceled.publishAfterCommit();
    }

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }




    public static void orderStatusModify(DeliveryPrepared deliveryPrepared){

        /** Example 1:  new item 
        Order order = new Order();
        repository().save(order);

        */

              
        repository().findById(deliveryPrepared.getOrderId()).ifPresent(order->{
            
            order.setStatus("deliveryPrepared");
            repository().save(order);


         });
      

        
    }


}



```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="orders", path="orders")
public interface OrderRepository extends PagingAndSortingRepository<Order, Long>{

}
```

## Application 테스트


- Application별 REST API 사용법

```
[상품관리자 - 상품 추가하기]
http POST localhost:8082/products itemcd="2" totalQuantity="100"
```
![image](https://user-images.githubusercontent.com/120017873/217167782-83ee2ff4-7a72-487b-bab8-f9f1d8d7e975.png)

```
[상품관리자 - 상품 수량 확인]
http GET localhost:8082/products/2
```
![image](https://user-images.githubusercontent.com/120017873/217167863-29c0265b-8a1f-4a4a-871b-f1b5747579fd.png)
```
[고객 - 주문하기]
http POST localhost:8081/orders item="milk" orderQty="1" status="ordered" price="2000" itemcd="2" 
```
![image](https://user-images.githubusercontent.com/120017873/217167910-32e9c96b-92da-48ea-a772-79c290b1c63a.png)
```
[고객 - 주문내역확인]
http GET localhost:8081/orders/3
```
![image](https://user-images.githubusercontent.com/120017873/217167986-0b2c50f5-ce20-4fec-a607-c090bdcb934d.png)
```

[고객 - 결제하기]
http POST localhost:8083/pays orderId="3" item="milk" orderQty="1" status="ordered" price="2000" itemcd="2" 
```
![image](https://user-images.githubusercontent.com/120017873/217168043-1a4185c6-9b13-4a61-ad1c-f3eecd9e66e0.png)
```
[고객 주문취소하기]
http DELETE localhost:8081/orders/3
```
![image](https://user-images.githubusercontent.com/120017873/217168257-fa12cf06-7a55-48aa-98ec-68fa5da6bb6f.png)
```

[배송업체 - 배송상태 변경하기(배송시작, 배송완료)]
http POST localhost:8086/deliveries/1/deliverystarted test=1
```
![image](https://user-images.githubusercontent.com/120017873/217168132-4cf574fd-81b9-483f-aa9d-1c08f53b76bf.png)
```

http POST localhost:8086/deliveries/1/deliverycompleted test=1
```
![image](https://user-images.githubusercontent.com/120017873/217168197-08e5e894-745e-4f04-8679-c534d8609384.png)


## Saga (Pub-Sub)  


- Saga (Pub-Sub) 구조 구현 예시 (product 서비스) 
  - 결제정보 이벤트를 수신하면 product의 수량정보를 수정한후 배송시작 이벤트를 발생한다
```
  @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='PayApproved'")
    public void wheneverPayApproved_OrderInfoReceived(@Payload PayApproved payApproved){

        PayApproved event = payApproved;
        System.out.println("\n\n##### listener OrderInfoReceived : " + payApproved + "\n\n");

        // Sample Logic //
        Product.orderInfoReceived(event);     

    }

public static void orderInfoReceived(PayApproved payApproved){

        /** Example 1:  new item 
        Product product = new Product();
        repository().save(product);

        */

       
        
        repository().findById(payApproved.getItemcd()).ifPresent(product->{
            
            product.setTotalQuantity(product.getTotalQuantity() - payApproved.getOrderQty()); // do something
            repository().save(product);

         DeliveryPrepared deliveryPrepared = new DeliveryPrepared();
         deliveryPrepared.setOrderId(payApproved.getOrderId());
         deliveryPrepared.setId(payApproved.getId());
         deliveryPrepared.setAddress(payApproved.getAddress());
         deliveryPrepared.setItemcd(payApproved.getItemcd());
         deliveryPrepared.setOrderQty(payApproved.getOrderQty());
         deliveryPrepared.publishAfterCommit();

         });
        
    }
```


## CQRS




## Compensation & Correlation


- 결제취소(주문취소)이벤트 발생시 product 수량정보를 보정하도록 아래와 같이 구현

![image](https://user-images.githubusercontent.com/120017873/217170390-14c20c19-e3ef-4962-aac2-0a28b2311587.png)



# 운영



## Gateway / Ingress





## Deploy / Pipeline



## Autoscale (HPA)

- 앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다.

- 주문서비스에 대한 replica를 동적으로 늘려주도록 HPA를 설정한다. 
- 설정은 CPU 사용량이 20프로를 넘어서면 replic를 10개까지 늘려준다.

```
kubectl autoscale deploy order -cpu-percent=20 --min=1 --max=10
```

- 워크로드를 60초 동안 걸어준다.

```
siege -c50 -t60S -v http://order:8080/orders
```

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어준다:

```
kubectl get deploy order -w
```



- 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다

```
NAME    READY   UP-TO-DATE   AVAILABLE   AGE
------------------------------------------------
order   1/4     1            1           2m3s
order   1/4     1            1           2m3s
order   1/4     3            1           2m3s
order   1/8     3            1           2m17s
order   1/8     3            1           2m17s
order   1/8     3            1           2m17s
order   1/8     3            1           2m17s
order   1/8     6            1           2m17s
order   1/10    6            1           2m32s
```

- siege의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다.

```
Transactions:                    498 hits
Availability:                 100.00 %
Elapsed time:                  61.33 secs
Data transferred:               0.15 MB
Response time:                  2.66 secs
Transaction rate:               8.12 trans/sec
Throughput:                     0.00 MB/sec
Concurrency:                   21.62
```


## Zero-downtime deploy (Readiness probe)

- 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler이나 CB 설정을 제거함
- seige로 배포작업 직전에 워크로드를 모니터링 함

```
root@siege:/# siege -c1 -t60S -v http://order:8080/orders --delay=1S
** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 200     0.03 secs:     291 bytes ==> GET  /orders
HTTP/1.1 200     0.03 secs:     291 bytes ==> GET  /orders
HTTP/1.1 200     0.03 secs:     291 bytes ==> GET  /orders
HTTP/1.1 200     0.01 secs:     291 bytes ==> GET  /orders
```

- 새 버전으로의 배포 시작

```
kubectl get deployment.yaml
```

- seige의 화면으로 넘어가서 Availability가 100% 미만으로 떨어졌는지 확인

```
Transactions:                     81 hits
Availability:                  72.32 %
Elapsed time:                  59.32 secs
Data transferred:               0.03 MB
Response time:                  0.08 secs
Transaction rate:               1.37 trans/sec
Throughput:                     0.00 MB/sec
Concurrency:                    0.11
```

- 배포기간 중 Availablity가 평소 100%에서 70%대로 떨어지는 것을 확인. 
- 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 거시기 때문이며, 이를 막기위해 Readiness Probe를 설정함

```
readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 30
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10![image](https://user-images.githubusercontent.com/119907154/217401149-2b6fdf24-eedf-44f6-8109-cf1cb56d6c85.png)
```

- 동일한 시나리오로 재배포 한 후 Availability 확인


```
Transactions:                    111 hits
Availability:                 100.00 %
Elapsed time:                  59.52 secs
Data transferred:               0.03 MB
Response time:                  0.04 secs
Transaction rate:               1.86 trans/sec
Throughput:                     0.00 MB/sec
Concurrency:                    0.07
```

=>  배포기간 동안 Availabilty가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨


## Persistence Volume/ConfigMap/Secret 

- EFS(Elastic File System) 사용한 Persistent Volume 설정

### 1. EFS 생성

- EFS를 사용할 EKS VPC 선택하여 생성

![image](https://user-images.githubusercontent.com/119908993/217175091-983fabb4-1a80-4e47-9ffc-4c507079c5cb.png)
![image](https://user-images.githubusercontent.com/119908993/217175159-e810adce-ebc3-44ce-8796-776e6ac1f4e6.png) 

### 2. EFS 계정 생성 및 Role 바인딩

- ServerAccount 생성 (efs-sa.yaml)


```
 $ kubectl apply -f efs-sa.yaml

apiVersion: v1
kind: ServiceAccount
metadata:
  name: efs-provisioner
```

 
![image](https://user-images.githubusercontent.com/119908993/217180034-08028f5d-42a1-4df1-abe5-48d0b1885a72.png)


- ServerAccount(efs-provisioner) 권한 설정

```
 $ kubectl apply -f efs-rbac.yaml
 

kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: efs-provisioner-runner
rules:
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-efs-provisioner
subjects:
  - kind: ServiceAccount
    name: efs-provisioner
    namespace: default
roleRef:
  kind: ClusterRole
  name: efs-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-efs-provisioner
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-efs-provisioner
subjects:
  - kind: ServiceAccount
    name: efs-provisioner
roleRef:
  kind: Role
  name: leader-locking-efs-provisioner
  apiGroup: rbac.authorization.k8s.io
```

### 3. EFS Provisioner 배포

```
 $ kubectl apply -f efs-provisioner-deploy.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: efs-provisioner
  namespace: mall
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: efs-provisioner
  template:
    metadata:
      labels:
        app: efs-provisioner
    spec:
      serviceAccount: efs-provisioner
      containers:
        - name: efs-provisioner
          image: quay.io/external_storage/efs-provisioner:latest
          env:
            - name: FILE_SYSTEM_ID
              value: fs-0aaccb328f84992df
            - name: AWS_REGION
              value: eu-west-3
            - name: PROVISIONER_NAME
              value: my-aws.com/aws-efs
          volumeMounts:
            - name: pv-volume
              mountPath: /persistentvolumes
      volumes:
        - name: pv-volume
          nfs:
            server: fs-0aaccb328f84992df.efs.eu-west-3.amazonaws.com
            path: /
```
![image](https://user-images.githubusercontent.com/119908993/217183126-181e8d31-8386-446c-9291-4f664369a96e.png)

### 4. StorageClass 생성

```
 $ kubectl apply -f efs-provisioner-deploy.yaml

kind: StorageClass
apiVersion: storage.k8s.io/v1
metadata:
  name: aws-efs
provisioner: my-aws.com/aws-efs
```
![image](https://user-images.githubusercontent.com/119908993/217183745-0c62c4fb-d322-48a0-b886-a0e5909bb4cb.png)

###5. PVC(PersistentVolumeClaim) 생성

```
 $ kubectl apply -f volume-PVC.yaml

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: aws-efs
  labels:
    app: test-pvc
spec:
  accessModes:
  - ReadWriteMany
  resources:
    requests:
      storage: 1Mi
  storageClassName: aws-efs
```
![image](https://user-images.githubusercontent.com/119908993/217190198-3fd67fc2-f292-48f0-9351-f090681f0e31.png)

###6. order pod 에 pvc 적용

```
 $ kubectl apply -f deployment.yaml

          volumeMounts:
          - mountPath: "/mnt/aws"
            name: volume
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: aws-efs
```
![image](https://user-images.githubusercontent.com/119908993/217205590-6c97aeb5-adbc-4c67-8819-53a01edebe30.png)

![image](https://user-images.githubusercontent.com/119908993/217205504-86533f87-4130-479a-9db8-6ed70eebcf10.png)



## Self-healing (liveness probe) 


- order pod 실행 후 /tmp 디렉토리 healthy 체크하여 처리

```
 /order/kubernetes/deployment.yaml

          livenessProbe:
            exec:
              command:
              - cat
              - /tmp/healthy
            initialDelaySeconds: 30
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
```

- order pod 생성

![image](https://user-images.githubusercontent.com/119908993/217396439-405777d3-3371-4cbf-aa53-1422bf4b41aa.png)

- order pod /tmp/health 삭제

![image](https://user-images.githubusercontent.com/119908993/217396679-0b541379-aa88-45b7-a7fb-b9847ee6dfac.png)

- /tmp/health 삭제 후 order pod restart 확인

![image](https://user-images.githubusercontent.com/119908993/217396803-1dc7cb86-d1ca-4e43-adf2-4337b59c61f8.png)








## Apply Service Messh

  - Istio를 구성하여 Service Mesh 구현
  - Istio는 sidecar 패턴을 이용하여 pod내에 별도의 역할을 하는 container 주입(injection)
  - kiali를 통해 Service Messh 구성 확인

```
< 구성절차 >
1) curl -L https://istio.io/downloadIstio | ISTIO_VERSION=$ISTIO_VERSION TARGET_ARCH=x86_64 sh –   // download Istio
2) istioctl install --set profile=demo --set hub=gcr.io/istio-release                              // istioctl을 통하여 설치
3) kubectl label namespace default istio-injection=enabled                                         // injection 대상 namespace 설정 (본 pjt에서는 default NS 이용)
4) kubectl apply -f samples/addons                                                                 // kiali / prometheus / grafana 를 위한 애드온 설치
5) kubectl patch svc kiali -n istio-system -p '{"spec": {"type": "LoadBalancer"}}'                 // kiali 접근을 위한 IP 외부노출


```

  - injection용 Namespace 설정후 확인
  
```
$ kubectl get ns -L istio-injection
```

![image](https://user-images.githubusercontent.com/119907154/217383342-e54e607a-a7fe-4be1-8a04-5c2268bd5836.png)


  - kiali 접근 URL 확인 (로드밸런서 타입으로 외부노출 후)

```
$ kubectl get service -n istio-system

```

![image](https://user-images.githubusercontent.com/119907154/217384012-fb6da3dc-27c9-4396-917d-35b20231dc7f.png)


  - kiali를 실행하여  구성 확인
    - default NS 에 6개 Application및 health 확인

![image](https://user-images.githubusercontent.com/119907154/217384306-099f7bad-ad25-4cf8-9ccd-5316a6593cf5.png)


## Log Aggregation / Monitoring

  - prometheus를 이용하여 메트릭 수집
  - grafana를 이용하여 시각화


```
< 구성절차 >
1) kubectl apply -f samples/addons                                                                 // add-on 설치
2) kubectl patch service/prometheus -n istio-system -p '{"spec": {"type": "LoadBalancer"}}'        // prometheus IP 노출
3) kubectl patch service/grafana -n istio-system -p '{"spec": {"type": "LoadBalancer"}}’           // grafana IP 노출

```


  - kiali 접근 URL 확인 (로드밸런서 타입으로 외부노출 후)

```
$ kubectl get service -n istio-system

```

![image](https://user-images.githubusercontent.com/119907154/217389261-256cbec9-4b80-402d-b4d7-c188bf28a6f6.png)


  - prometheus 메트릭 수집 (istio_request_total)

![image](https://user-images.githubusercontent.com/119907154/217389336-7fccf088-c760-48bb-bb75-1fe0be226aae.png)


  - grafana 연동 (metric from prometheus)

![image](https://user-images.githubusercontent.com/119907154/217389760-1cb57ac0-e7f6-4d14-b7b7-6b7543da4027.png)




