package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class DeliveryPrepared extends AbstractEvent {

    private Long id;
    private String address;
    private Integer itemcd;
    private Integer orderQty;
    private Long totalQuantity;
    private Long orderId;

    public DeliveryPrepared(Product aggregate){
        super(aggregate);
    }
    public DeliveryPrepared(){
        super();
    }
}
