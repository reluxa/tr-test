package org.reluxa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class InputSender {

  private Logger logger = LoggerFactory.getLogger(InputSender.class);
  private final JmsTemplate jmsTemplate;

  public InputSender(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  @Transactional
  public void send(final Integer number) {
    logger.info("Generator was called: {}", number);
    jmsTemplate.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(number.toString());
      }
    });
  }
}
