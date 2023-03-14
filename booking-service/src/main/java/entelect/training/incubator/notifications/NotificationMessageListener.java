package entelect.training.incubator.notifications;

import entelect.training.incubator.spring.notification.sms.client.impl.MoloCellSmsClient;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class NotificationMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageListener.class);

    @Autowired
    private MoloCellSmsClient smsService;

    @Override
    public void onMessage(Message message) {
        try {
            ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
            SmsMessage smsMessage = (SmsMessage) objectMessage.getObject();
            smsService.sendSms(smsMessage.getPhoneNumber(), smsMessage.getMessage());
            LOGGER.info("Message sent to " + smsMessage.getPhoneNumber());
        } catch (JMSException e) {
            LOGGER.error("Error processing message: " + e.getMessage(), e);
        }
    }
}