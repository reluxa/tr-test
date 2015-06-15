package org.reluxa;

import org.reluxa.config.AppConfig;
import org.reluxa.service.DBStore;
import org.reluxa.service.InputSender;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Launcher {

  public static void main(String[] args) throws InterruptedException, IOException {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    InputSender inputSender = ctx.getBean(InputSender.class);
    DBStore dbStore = ctx.getBean(DBStore.class);
    dbStore.deleteAll();
    Thread.sleep(5000);
    for (int i=0;i<10000;i++) {
      inputSender.send(i);
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    br.readLine();
    ctx.close();
  }

}
