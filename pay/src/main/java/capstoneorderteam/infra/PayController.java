package capstoneorderteam.infra;
import capstoneorderteam.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@RestController
// @RequestMapping(value="/pays")
@Transactional
public class PayController {
    @Autowired
    PayRepository payRepository;

    @RequestMapping(value = "pays", method = RequestMethod.POST)
    public void approvePayment(@RequestBody Pay pay) {
        System.out.println("##### /pay/approvePayment  called #####");
        payRepository.save(pay);
    }

}
