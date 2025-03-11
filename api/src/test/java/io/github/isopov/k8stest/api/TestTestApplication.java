package io.github.isopov.k8stest.api;

import org.springframework.boot.SpringApplication;

public class TestTestApplication {

  public static void main(String[] args) {
    SpringApplication.from(TestApplication::main).with(TestContainersConfiguration.class).run(args);
  }

}
