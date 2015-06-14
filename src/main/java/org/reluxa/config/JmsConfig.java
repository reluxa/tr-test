package org.reluxa.config;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.spring.ActiveMQXAConnectionFactory;
import org.reluxa.service.DBStore;
import org.reluxa.service.InputQueueMessageListener;
import org.reluxa.service.ResultQueueMessageListener;
import org.reluxa.service.SquareQueueMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.sql.DataSource;
import javax.transaction.SystemException;

@Configuration
public class JmsConfig {

  public static final String INPUT_QUEUE = "input.queue";
  public static final String SQUARE_QUEUE = "square.queue";
  public static final String RESULT_QUEUE = "result.queue";

  @Autowired
  JtaTransactionManager jtaTransactionManager;

  @Autowired
  DataSource dataSource;

  private ActiveMQXAConnectionFactory activeMQXAConnectionFactory() {
    ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
    activeMQXAConnectionFactory.setBrokerURL("tcp://localhost:61616");
    return activeMQXAConnectionFactory;
  }

  private AtomikosConnectionFactoryBean connectionFactory(String name) {
    AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
    atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory());
    atomikosConnectionFactoryBean.setMinPoolSize(1);
    atomikosConnectionFactoryBean.setMaxPoolSize(15);
    atomikosConnectionFactoryBean.setUniqueResourceName(name);
    return atomikosConnectionFactoryBean;
  }

  private Queue queue(String name) {
    ActiveMQQueue queue = new ActiveMQQueue(name);
    return queue;
  }

  @Bean
  public JmsTemplate inputQueueJmsTemplate() {
    return jmsTemplate(INPUT_QUEUE);
  }

  @Bean
  public JmsTemplate squareQueueJmsTemplate() {
    return jmsTemplate(SQUARE_QUEUE);
  }

  @Bean
  public JmsTemplate resultQueueJmsTemplate() {
    return jmsTemplate(RESULT_QUEUE);
  }

  @Bean
  public InputQueueMessageListener inputQueueMessageListener() {
    return new InputQueueMessageListener(squareQueueJmsTemplate(), new DBStore(dataSource));
  }

  @Bean
  public SquareQueueMessageListener squareQueueMessageListener() {
    return new SquareQueueMessageListener(resultQueueJmsTemplate());
  }

  @Bean
  public ResultQueueMessageListener resultQueueMessageListener() {
    return new ResultQueueMessageListener(new DBStore(dataSource));
  }

  @Bean
  public DefaultMessageListenerContainer inputQueueDMLC() throws SystemException {
    return defaultMessageListenerContainer(INPUT_QUEUE, inputQueueMessageListener());
  }

  @Bean
  public DefaultMessageListenerContainer squareQueueDMLC() throws SystemException {
    return defaultMessageListenerContainer(SQUARE_QUEUE, squareQueueMessageListener());
  }

  @Bean
  public DefaultMessageListenerContainer resultQueueDMLC() throws SystemException {
    return defaultMessageListenerContainer(RESULT_QUEUE, resultQueueMessageListener());
  }

  private DefaultMessageListenerContainer defaultMessageListenerContainer(String queueName, MessageListener messageListener) throws SystemException {
    DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
    listenerContainer.setConnectionFactory(connectionFactory(queueName+".listener"));
    listenerContainer.setTransactionManager(jtaTransactionManager);
    listenerContainer.setDestination(queue(queueName));
    listenerContainer.setSessionTransacted(false);
    listenerContainer.setReceiveTimeout(10000);
    listenerContainer.setMessageListener(messageListener);
    listenerContainer.setAutoStartup(true);
    listenerContainer.setConcurrency("1-15");
    return listenerContainer;
  }

  private JmsTemplate jmsTemplate(String queueName) {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(connectionFactory(queueName+".sender"));
    jmsTemplate.setDefaultDestination(queue(queueName));
    return jmsTemplate;
  }
}
