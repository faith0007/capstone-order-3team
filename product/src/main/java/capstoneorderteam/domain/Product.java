package capstoneorderteam.domain;

import capstoneorderteam.domain.DeliveryPrepared;
import capstoneorderteam.ProductApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Product_table")
@Data

public class Product  {


    
    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Integer itemcd;
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String address;
    
    
    
    
    
    private Integer orderQty;
    
    
    
    
    
    private Long totalQuantity;
    
    
    
    
    
    private Long orderId;

    @PostPersist
    public void onPostPersist(){


        // DeliveryPrepared deliveryPrepared = new DeliveryPrepared(this);
        // deliveryPrepared.publishAfterCommit();

    }

    

    public static ProductRepository repository(){
        ProductRepository productRepository = ProductApplication.applicationContext.getBean(ProductRepository.class);
        return productRepository;
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
         deliveryPrepared.setAddress(payApproved.getAddress());
         deliveryPrepared.setItemcd(payApproved.getItemcd());
         deliveryPrepared.setOrderQty(payApproved.getOrderQty());
         deliveryPrepared.publishAfterCommit();

         });
        

        
    }
    public static void orderCancelProcess(PayCanceled payCanceled){

        /** Example 1:  new item 
        Product product = new Product();
        repository().save(product);

        */

        
        
        repository().findById(payCanceled.getItemcd()).ifPresent(product->{
            
            // product // do something
            product.setTotalQuantity(product.getTotalQuantity() + payCanceled.getOrderQty()); // do something
            repository().save(product);


         });
        

        
    }
    public static void orderCancelProcess(OrderCanceled orderCanceled){

        /** Example 1:  new item 
        Product product = new Product();
        repository().save(product);

        */

        
        
        repository().findById(orderCanceled.getItemcd()).ifPresent(product->{
            
            product.setTotalQuantity(product.getTotalQuantity() + orderCanceled.getOrderQty()); // do something
            repository().save(product);


         });
        

        
    }


}
