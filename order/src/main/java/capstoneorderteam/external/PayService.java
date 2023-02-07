package capstoneorderteam.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "pay", url = "${api.url.pay}")
public interface PayService {
    @RequestMapping(method= RequestMethod.POST, path="/pays")
    public void approvePayment(@RequestBody Pay pay);
}
