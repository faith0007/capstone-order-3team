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
    @Autowired DeliveryRepository deliveryRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='DeliveryPrepared'")
    public void wheneverDeliveryPrepared_RequestDelivery(@Payload DeliveryPrepared deliveryPrepared){

        DeliveryPrepared event = deliveryPrepared;
        System.out.println("\n\n##### listener RequestDelivery : " + deliveryPrepared + "\n\n");


        

        // Sample Logic //
        Delivery.requestDelivery(event);
        

        

    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='PayCanceled'")
    public void wheneverPayCanceled_CancelDelivery(@Payload PayCanceled payCanceled){

        PayCanceled event = payCanceled;
        System.out.println("\n\n##### listener CancelDelivery : " + payCanceled + "\n\n");


        

        // Sample Logic //
        Delivery.cancelDelivery(event);
        

        

    }
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderCanceled'")
    public void wheneverOrderCanceled_CancelDelivery(@Payload OrderCanceled orderCanceled){

        OrderCanceled event = orderCanceled;
        System.out.println("\n\n##### listener CancelDelivery : " + orderCanceled + "\n\n");


        

        // Sample Logic //
        Delivery.cancelDelivery(event);
        

        

    }

}


