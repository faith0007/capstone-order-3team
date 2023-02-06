package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class DeliveryCompleted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private Long customerId;
    private String productName;
    private Integer qtyp;
    private Integer price;
    private String address;
    private String status;
}


