package capstoneorderteam.domain;

import capstoneorderteam.domain.DeliveryCanceled;
import capstoneorderteam.domain.DeliveryCompleted;
import capstoneorderteam.domain.DeliveryStarted;
import capstoneorderteam.domain.DeliveryRequested;
import capstoneorderteam.DeliveryApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Delivery_table")
@Data

public class Delivery  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private Long orderId;
    
    
    
    
    
    private Long customerId;
    
    
    
    
    
    private String productName;
    
    
    
    
    
    private Integer qtyp;
    
    
    
    
    
    private Integer price;
    
    
    
    
    
    private String address;
    
    
    
    
    
    private String status;

    @PostPersist
    public void onPostPersist(){


        





    }

    public static DeliveryRepository repository(){
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(DeliveryRepository.class);
        return deliveryRepository;
    }




    public static void requestDelivery(DeliveryPrepared deliveryPrepared){

        
        Delivery delivery = new Delivery();
        delivery.setStatus("deliveryRequested");
        delivery.setAddress(deliveryPrepared.getAddress());
        delivery.setOrderId(deliveryPrepared.getOrderId());
        delivery.setQtyp(deliveryPrepared.getOrderQty());
        repository().save(delivery);

        DeliveryRequested deliveryRequested = new DeliveryRequested();

        deliveryRequested.setStatus("deliveryRequested");
        deliveryRequested.setAddress(deliveryPrepared.getAddress());
        deliveryRequested.setOrderId(deliveryPrepared.getOrderId());
        deliveryRequested.setQtyp(deliveryPrepared.getOrderQty());

        deliveryRequested.publishAfterCommit();

        /** Example 2:  finding and process
        
        repository().findById(deliveryPrepared.get???()).ifPresent(delivery->{
            
            delivery // do something
            repository().save(delivery);


         });
        */

        
    }
    public static void cancelDelivery(PayCanceled payCanceled){

        
        Delivery delivery = new Delivery();
        delivery.setStatus("deliveryCanceled");
        //delivery.setAddress(payCanceled.getAddress());
        delivery.setOrderId(payCanceled.getOrderId());
        delivery.setQtyp(payCanceled.getOrderQty());
        repository().save(delivery);

        

        DeliveryCanceled deliveryCanceled = new DeliveryCanceled();
        deliveryCanceled.setStatus("deliveryCanceled");
        //deliveryCanceled.setAddress(payCanceled.getAddress());
        deliveryCanceled.setOrderId(payCanceled.getOrderId());
        deliveryCanceled.setQtyp(payCanceled.getOrderQty());
        deliveryCanceled.publishAfterCommit();

        /** Example 2:  finding and process
        
        repository().findById(payCanceled.get???()).ifPresent(delivery->{
            
            delivery // do something
            repository().save(delivery);


         });
        */

        
    }
    public static void cancelDelivery(OrderCanceled orderCanceled){

        
        Delivery delivery = new Delivery();
        delivery.setStatus("deliveryCanceled");
        //delivery.setAddress(payCanceled.getAddress());
        delivery.setOrderId(orderCanceled.getId());
        delivery.setQtyp(orderCanceled.getOrderQty());
        repository().save(delivery);

        DeliveryCanceled deliveryCanceled = new DeliveryCanceled();
        deliveryCanceled.setStatus("deliveryCanceled");
        //deliveryCanceled.setAddress(payCanceled.getAddress());
        deliveryCanceled.setOrderId(orderCanceled.getId());
        deliveryCanceled.setQtyp(orderCanceled.getOrderQty());
        deliveryCanceled.publishAfterCommit();


        /** Example 2:  finding and process
        
        repository().findById(orderCanceled.get???()).ifPresent(delivery->{
            
            delivery // do something
            repository().save(delivery);


         });
        */

        
    }


}
