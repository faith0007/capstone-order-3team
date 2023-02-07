package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class PayApproved extends AbstractEvent {

    private Long id;
    private Long price;
    private String status;
    private Integer itemcd;
    private Integer orderQty;
    private Long orderId;
    private String address;
}


