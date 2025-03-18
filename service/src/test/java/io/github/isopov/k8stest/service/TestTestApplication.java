package io.github.isopov.k8stest.service;

import org.springframework.boot.SpringApplication;

//locally can be tested with
//grpcurl -plaintext -proto grpccore/src/main/proto/factorials.proto -d '{"value":5}' localhost:8080 Factorials.Compute

public class TestTestApplication {

    public static void main(String[] args) {
        SpringApplication.from(TestApplication::main).run(args);
    }
}
