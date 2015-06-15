package org.reluxa.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Configuration
@EnableTransactionManagement
public class TxConfig {

  @Bean(initMethod = "init", destroyMethod = "close")
  @DependsOn({
      "dataSource",
      "inputSenderConnectionFactory",
      "inputListenerConnectionFactory",
      "squareSenderConnectionFactory",
      "squareListenerConnectionFactory",
      "resultSenderConnectionFactory",
      "resultListenerConnectionFactory"
  })
  public TransactionManager transactionManager() throws SystemException {
    UserTransactionManager userTransactionManager = new UserTransactionManager();
    userTransactionManager.setTransactionTimeout(60);
    return userTransactionManager;
  }

  @Bean
  public UserTransaction userTransaction() {
    UserTransactionImp userTransactionImp = new UserTransactionImp();
    return userTransactionImp;
  }

  @Bean
  public JtaTransactionManager jtaTransactionManager() throws SystemException {
    JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
    jtaTransactionManager.setTransactionManager(transactionManager());
    jtaTransactionManager.setUserTransaction(userTransaction());
    return jtaTransactionManager;
  }



}
