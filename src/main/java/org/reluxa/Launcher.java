package org.reluxa;

import org.reluxa.config.AppConfig;
import org.reluxa.service.DBStore;
import org.reluxa.service.InputSender;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Launcher {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    InputSender inputSender = ctx.getBean(InputSender.class);
    DBStore dbStore = ctx.getBean(DBStore.class);
    dbStore.deleteAll();
    for (int i=0;i<10;i++) {
      inputSender.send(i);
    }
  }

}
