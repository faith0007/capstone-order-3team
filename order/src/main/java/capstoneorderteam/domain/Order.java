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

        OrderCanceled orderCanceled = new OrderCanceled(this);
        orderCanceled.publishAfterCommit();

    }

    @PreRemove
    public void onPreRemove(){
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

        /** Example 2:  finding and process
        
        repository().findById(deliveryPrepared.get???()).ifPresent(order->{
            
            order // do something
            repository().save(order);


         });
        */

        
    }


}
