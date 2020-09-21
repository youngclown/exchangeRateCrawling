package com.bymin.crawling;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@Log4j2
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class CrawlingApplication {


  public static void main(String[] args) {
    SpringApplication.run(CrawlingApplication.class, args);
  }


}
