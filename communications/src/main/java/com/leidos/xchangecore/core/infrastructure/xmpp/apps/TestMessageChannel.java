package com.leidos.xchangecore.core.infrastructure.xmpp.apps;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.integration.channel.SubscribableChannel;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.message.MessageHandler;

public class TestMessageChannel {

    private MessageChannel interestGroupStateNotificationChannel;

    private EventDrivenConsumer consumer;

//    private Properties xmppProperties;

    public void setInterestGroupStateNotificationChannel(MessageChannel c) {
        interestGroupStateNotificationChannel = c;
    }

//    public void setXmppProperties(Properties p) {
//        xmppProperties = p;
//    }

    private Logger logger = Logger.getLogger(this.getClass());

    private class InterestGroupMessageHandler implements MessageHandler {

        @Override
        public void handleMessage(Message<?> arg0) {
            logger.info(" got interest groupStateNotificationChannel");
        }

    }

    @PostConstruct
    protected void init() {
        consumer = new EventDrivenConsumer(
                (SubscribableChannel) interestGroupStateNotificationChannel,
                new InterestGroupMessageHandler());
        consumer.start();
        if (consumer != null) {
            logger.info(" event consumer created on channel "
                    + interestGroupStateNotificationChannel.getName());
        } else {
            logger.error("event consumer not created");
        }
    }

}
