package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class DeliveryStarted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private Long customerId;
    private String productName;
    private Integer qtyp;
    private Integer price;
    private String address;
    private String status;

    public DeliveryStarted(Delivery aggregate){
        super(aggregate);
    }
    public DeliveryStarted(){
        super();
    }
}
