package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class PayApproved extends AbstractEvent {

    private Long id;
    private Long price;
    private String status;
    private Integer itemcd;
    private Integer orderQty;
    private Long orderId;

    public PayApproved(Pay aggregate){
        super(aggregate);
    }
    public PayApproved(){
        super();
    }
}
