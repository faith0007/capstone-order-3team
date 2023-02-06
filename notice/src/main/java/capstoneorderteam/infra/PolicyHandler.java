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
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderCanceled'")
    public void wheneverOrderCanceled_KakaoNotice(@Payload OrderCanceled orderCanceled){

        OrderCanceled event = orderCanceled;
        System.out.println("\n\n##### listener KakaoNotice : " + orderCanceled + "\n\n");


        

        // Sample Logic //

        

    }
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='Ordered'")
    public void wheneverOrdered_KakaoNotice(@Payload Ordered ordered){

        Ordered event = ordered;
        System.out.println("\n\n##### listener KakaoNotice : " + ordered + "\n\n");


        

        // Sample Logic //

        

    }
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='DeliveryPrepared'")
    public void wheneverDeliveryPrepared_KakaoNotice(@Payload DeliveryPrepared deliveryPrepared){

        DeliveryPrepared event = deliveryPrepared;
        System.out.println("\n\n##### listener KakaoNotice : " + deliveryPrepared + "\n\n");


        

        // Sample Logic //

        

    }
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='DeliveryCompleted'")
    public void wheneverDeliveryCompleted_KakaoNotice(@Payload DeliveryCompleted deliveryCompleted){

        DeliveryCompleted event = deliveryCompleted;
        System.out.println("\n\n##### listener KakaoNotice : " + deliveryCompleted + "\n\n");


        

        // Sample Logic //

        

    }
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='DeliveryCanceled'")
    public void wheneverDeliveryCanceled_KakaoNotice(@Payload DeliveryCanceled deliveryCanceled){

        DeliveryCanceled event = deliveryCanceled;
        System.out.println("\n\n##### listener KakaoNotice : " + deliveryCanceled + "\n\n");


        

        // Sample Logic //

        

    }

}


