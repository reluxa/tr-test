package org.reluxa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

import static java.lang.String.format;

public class SquareQueueMessageListener implements MessageListener{

  private Logger logger = LoggerFactory.getLogger(SquareQueueMessageListener.class);
  private final JmsTemplate resultQueue;

  public SquareQueueMessageListener(JmsTemplate resultQueue) {
    this.resultQueue = resultQueue;
  }

  public void onMessage(Message message) {
    try {
      int number = Integer.parseInt(((TextMessage) message).getText());
      logger.info("Message was received: {}", number);
      int square = calculateSquare(number);
      sendResult(number, square);
      logger.info("Result was sent: {}", number);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private void sendResult(final int original, final int square) {
    resultQueue.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(format("%d,%d", original, square));
      }
    });
  }

  private int calculateSquare(int number) {
    return number * number;
  }

}
