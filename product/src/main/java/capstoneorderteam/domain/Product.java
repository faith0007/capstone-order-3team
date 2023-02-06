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
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Integer itemcd;
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String address;
    
    
    
    
    
    private Integer orderQty;
    
    
    
    
    
    private Long totalQuantity;
    
    
    
    
    
    private Long orderId;

    @PostPersist
    public void onPostPersist(){


        DeliveryPrepared deliveryPrepared = new DeliveryPrepared(this);
        deliveryPrepared.publishAfterCommit();

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

        /** Example 2:  finding and process
        
        repository().findById(payApproved.get???()).ifPresent(product->{
            
            product // do something
            repository().save(product);


         });
        */

        
    }
    public static void orderCancelProcess(PayCanceled payCanceled){

        /** Example 1:  new item 
        Product product = new Product();
        repository().save(product);

        */

        /** Example 2:  finding and process
        
        repository().findById(payCanceled.get???()).ifPresent(product->{
            
            product // do something
            repository().save(product);


         });
        */

        
    }
    public static void orderCancelProcess(OrderCanceled orderCanceled){

        /** Example 1:  new item 
        Product product = new Product();
        repository().save(product);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderCanceled.get???()).ifPresent(product->{
            
            product // do something
            repository().save(product);


         });
        */

        
    }


}
