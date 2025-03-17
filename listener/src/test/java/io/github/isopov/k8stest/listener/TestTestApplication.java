package io.github.isopov.k8stest.listener;

import org.springframework.boot.SpringApplication;
import io.github.isopov.k8stest.tests.TestContainersConfiguration;

public class TestTestApplication {

  public static void main(String[] args) {
    SpringApplication.from(TestApplication::main).with(TestContainersConfiguration.class).run(args);
  }

}
