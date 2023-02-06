package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class OrderCanceled extends AbstractEvent {

    private Long id;
    private String item;
    private Integer orderQty;
    private String status;
    private Long price;
    private Integer itemcd;
}


