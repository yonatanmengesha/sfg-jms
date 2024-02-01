package guru.springframework.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    @Scheduled(fixedRate = 2000)
    public void sendMessage(){

    //    System.out.println("I Am Sending A Message");

        HelloWorldMessage message =HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();
        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE,message);
    //    System.out.println("Message Sent!");
    }
    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {



        HelloWorldMessage message =HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();
       Message receiveMsg =  jmsTemplate.sendAndReceive(JmsConfig.MY_SND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try{
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_Type","guru.springframework.sfgjms.model.HelloWorldMessage");
                    System.out.println("Sending Hello");
                    return helloMessage;
                }catch (JsonProcessingException e){
                    throw new JMSException("jms error message");

                }

            }
        });
        System.out.println(receiveMsg.getBody(String.class));
    }
}
