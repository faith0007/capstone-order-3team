package capstoneorderteam.external;

import lombok.Data;
import java.util.Date;
@Data
public class Pay {

    private Long id;
    private Long price;
    private String status;
    private Integer itemcd;
    private Integer orderQty;
    private Long orderId;
}


