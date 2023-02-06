package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import capstoneorderteam.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class Ordered extends AbstractEvent {

    private Long id;
    private String item;
    private String status;
    private Long price;
    private Integer orderQty;
    private Integer itemcd;
}


