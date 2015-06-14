package org.reluxa.config;

import org.reluxa.service.DBStore;
import org.reluxa.service.InputSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

import javax.sql.DataSource;

@Configuration
@Import({
    ActiveMQConfig.class,
    TxConfig.class,
    JmsConfig.class,
    DbConfig.class
})
public class AppConfig {

  @Autowired
  JmsTemplate inputQueueJmsTemplate;

  @Autowired
  DataSource dataSource;

  @Bean
  public DBStore dbStore() {
    return new DBStore(dataSource);
  }


  @Bean
  public InputSender inputGenerator() {
    return new InputSender(inputQueueJmsTemplate);
  }


}
