# [SDS] CAPSTONE pjt. team 3.


# 주제 - 상품주문시스템


# Table of contents

- [상품주문시스템](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [Gateway / Ingress](## Gateway / Ingress)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

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




## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/487999/79684772-eba9ab00-826e-11ea-9405-17e2bf39ec76.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현



## Saga (Pub-Sub)  





## CQRS




## Compensation & Correlation




# 운영



## Gateway / Ingress





## Deploy / Pipeline



## Autoscale (HPA)



## Zero-downtime deploy (Readiness probe)



## Persistence Volume/ConfigMap/Secret 



## Self-healing (liveness probe) 



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


## Loggregation / Monitoring 









