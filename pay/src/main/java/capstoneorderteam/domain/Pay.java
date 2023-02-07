package capstoneorderteam.domain;

import capstoneorderteam.domain.PayApproved;
import capstoneorderteam.domain.PayCanceled;
import capstoneorderteam.PayApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Pay_table")
@Data

public class Pay  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private Long price;
    
    
    
    
    
    private String status;
    
    
    
    
    
    private Integer itemcd;
    
    
    
    
    
    private Integer orderQty;
    
    
    
    
    
    private Long orderId;


    @PostPersist
    public void onPostPersist(){


        PayApproved payApproved = new PayApproved(this);
        payApproved.publishAfterCommit();



        //PayCanceled payCanceled = new PayCanceled(this);
        //payCanceled.publishAfterCommit();

    }

    public static PayRepository repository(){
        PayRepository payRepository = PayApplication.applicationContext.getBean(PayRepository.class);
        return payRepository;
    }

    public void approvePayment(Pay){

    }




    public static void cancelPayment(OrderCanceled orderCanceled){

        /** Example 1:  new item 
        Pay pay = new Pay();
        repository().save(pay);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderCanceled.get???()).ifPresent(pay->{
            
            pay // do something
            repository().save(pay);


         });
        */

        
    }


}
