package com.zalude.spac.fusion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = {SpacFusionApplication.class, Jsr310JpaConverters.class})
public class SpacFusionApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpacFusionApplication.class, args);
  }
}
