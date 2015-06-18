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
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;
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

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn("brokerService")
  public AtomikosConnectionFactoryBean inputSenderConnectionFactory() {
    return atomikosConnectionFactoryBean(INPUT_QUEUE + ".sender");
  }

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn("brokerService")
  public AtomikosConnectionFactoryBean inputListenerConnectionFactory() {
    return atomikosConnectionFactoryBean(INPUT_QUEUE + ".listener");
  }

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn("brokerService")
  public AtomikosConnectionFactoryBean squareSenderConnectionFactory() {
    return atomikosConnectionFactoryBean(SQUARE_QUEUE + ".sender");
  }

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn("brokerService")
  public AtomikosConnectionFactoryBean squareListenerConnectionFactory() {
    return atomikosConnectionFactoryBean(SQUARE_QUEUE + ".listener");
  }

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn("brokerService")
  public AtomikosConnectionFactoryBean resultSenderConnectionFactory() {
    return atomikosConnectionFactoryBean(RESULT_QUEUE + ".sender");
  }

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn("brokerService")
  public AtomikosConnectionFactoryBean resultListenerConnectionFactory() {
    return atomikosConnectionFactoryBean(RESULT_QUEUE + ".listener");
  }


  private AtomikosConnectionFactoryBean atomikosConnectionFactoryBean(String resourceName) {
    ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
    activeMQXAConnectionFactory.setBrokerURL("tcp://localhost:61616");
    AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
    atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
    atomikosConnectionFactoryBean.setMinPoolSize(1);
    atomikosConnectionFactoryBean.setMaxPoolSize(15);
    atomikosConnectionFactoryBean.setUniqueResourceName(resourceName);
    return atomikosConnectionFactoryBean;
  }

  private Queue queue(String name) {
    ActiveMQQueue queue = new ActiveMQQueue(name);
    return queue;
  }

  @Bean
  public JmsTemplate inputQueueJmsTemplate() {
    return jmsTemplate(INPUT_QUEUE, inputSenderConnectionFactory());
  }

  @Bean
  public JmsTemplate squareQueueJmsTemplate() {
    return jmsTemplate(SQUARE_QUEUE, squareSenderConnectionFactory());
  }

  @Bean
  public JmsTemplate resultQueueJmsTemplate() {
    return jmsTemplate(RESULT_QUEUE, resultSenderConnectionFactory());
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
  @DependsOn("jtaTransactionManager")
  public DefaultMessageListenerContainer inputQueueDMLC() throws SystemException {
    return defaultMessageListenerContainer(INPUT_QUEUE, inputQueueMessageListener(), inputListenerConnectionFactory());
  }

  @Bean
  @DependsOn("jtaTransactionManager")
  public DefaultMessageListenerContainer squareQueueDMLC() throws SystemException {
    return defaultMessageListenerContainer(SQUARE_QUEUE, squareQueueMessageListener(), squareListenerConnectionFactory());
  }

  @Bean
  @DependsOn("jtaTransactionManager")
  public DefaultMessageListenerContainer resultQueueDMLC() throws SystemException {
    return defaultMessageListenerContainer(RESULT_QUEUE, resultQueueMessageListener(), squareListenerConnectionFactory());
  }

  private DefaultMessageListenerContainer defaultMessageListenerContainer(String queueName, MessageListener messageListener,
              ConnectionFactory connectionFactory) throws SystemException {
    DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
    listenerContainer.setConnectionFactory(connectionFactory);
    listenerContainer.setTransactionManager(jtaTransactionManager);
    listenerContainer.setDestination(queue(queueName));
    listenerContainer.setSessionTransacted(false);
    listenerContainer.setReceiveTimeout(10000);
    listenerContainer.setMessageListener(messageListener);
    listenerContainer.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
    listenerContainer.setConcurrency("1-15");
    listenerContainer.setAutoStartup(true);
    return listenerContainer;
  }

  private JmsTemplate jmsTemplate(String queueName, ConnectionFactory connectionFactory) {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(connectionFactory);
    jmsTemplate.setDefaultDestination(queue(queueName));
    return jmsTemplate;
  }
}
