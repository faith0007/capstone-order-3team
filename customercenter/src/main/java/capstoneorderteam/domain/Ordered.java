package capstoneorderteam.domain;

import capstoneorderteam.infra.AbstractEvent;
import lombok.Data;
import java.util.*;


@Data
public class Ordered extends AbstractEvent {

    private Long id;
    private String item;
    private String status;
    private Long price;
    private Integer orderQty;
    private Integer itemcd;
}
