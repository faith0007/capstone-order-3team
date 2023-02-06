package capstoneorderteam.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import capstoneorderteam.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import capstoneorderteam.domain.*;

@Service
@Transactional
public class PolicyHandler{
    @Autowired OrderRepository orderRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='DeliveryPrepared'")
    public void wheneverDeliveryPrepared_OrderStatusModify(@Payload DeliveryPrepared deliveryPrepared){

        DeliveryPrepared event = deliveryPrepared;
        System.out.println("\n\n##### listener OrderStatusModify : " + deliveryPrepared + "\n\n");


        

        // Sample Logic //
        Order.orderStatusModify(event);
        

        

    }

}


