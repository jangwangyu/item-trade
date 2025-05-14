package org.example.itemtrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ItemTradeApplication {

  public static void main(String[] args) {
    SpringApplication.run(ItemTradeApplication.class, args);
  }

}
