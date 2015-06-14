package org.reluxa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

public class InputQueueMessageListener implements MessageListener{

  private Logger logger = LoggerFactory.getLogger(InputQueueMessageListener.class);
  private final JmsTemplate squareDistributor;
  private final DBStore dbStore;

  public InputQueueMessageListener(JmsTemplate squareDistributor, DBStore dbStore) {
    this.squareDistributor = squareDistributor;
    this.dbStore = dbStore;
  }

  public void onMessage(Message message) {
    try {
      int number = Integer.parseInt(((TextMessage) message).getText());
      logger.info("Input was received: {}", number);
      dbStore.insert(number);
      sendToSquareDistributor(number);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private void sendToSquareDistributor(final int number) {
    squareDistributor.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(Integer.toString(number));
      }
    });
  }

}
