package org.reluxa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ResultQueueMessageListener implements MessageListener{

  private Logger logger = LoggerFactory.getLogger(ResultQueueMessageListener.class);
  private final DBStore dbStore;

  public ResultQueueMessageListener(DBStore dbStore) {
    this.dbStore = dbStore;
  }

  public void onMessage(Message message) {
    try {
      String[] split = ((TextMessage) message).getText().split(",");
      int original = Integer.parseInt(split[0]);
      int square = Integer.parseInt(split[1]);
      dbStore.addResult(original, square);
      logger.info("Result was received: {}", square);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

}
