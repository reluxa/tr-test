package org.reluxa.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.spring.ActiveMQXAConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQConfig {

  @Bean
  public BrokerService brokerService() throws Exception {
    BrokerService broker = new BrokerService();
    broker.addConnector("tcp://localhost:61616");
    broker.start();
    return broker;
  }



}
