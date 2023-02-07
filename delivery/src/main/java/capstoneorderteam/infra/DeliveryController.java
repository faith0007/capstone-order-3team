package capstoneorderteam.infra;
import capstoneorderteam.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;


@RestController
// @RequestMapping(value="/deliveries")
@Transactional
public class DeliveryController {
    @Autowired
    DeliveryRepository deliveryRepository;


    @RequestMapping(value = "deliveries/{id}/deliverystarted", method = RequestMethod.POST)
    public Delivery deliveryStarted(@PathVariable Long id, @RequestBody Delivery delivery, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("##### deliverystarted #####");
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        optionalDelivery.orElseThrow(() -> new Exception("No Entity Found"));
        Delivery d = optionalDelivery.get();
        d.setStatus("deliverystarted");

        deliveryRepository.save(d);

        

        DeliveryStarted deliveryStarted = new DeliveryStarted();
        deliveryStarted.setId(d.getId());
        deliveryStarted.publishAfterCommit();

        return d;
    }

    @RequestMapping(value = "deliveries/{id}/deliverycompleted", method = RequestMethod.POST)
    public Delivery deliveryCompleted(@PathVariable Long id, @RequestBody Delivery delivery, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("##### deliverycompleted #####");
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        optionalDelivery.orElseThrow(() -> new Exception("No Entity Found"));
        Delivery d = optionalDelivery.get();
        d.setStatus("deliverycompleted");

        deliveryRepository.save(d);

        
        DeliveryCompleted deliveryCompleted = new DeliveryCompleted();
        deliveryCompleted.setId(d.getId());
        deliveryCompleted.publishAfterCommit();

        
        return d;
    }


}
