package io.github.isopov.k8stest.service;

import io.github.isopov.k8stest.grpccore.FactorialReply;
import io.github.isopov.k8stest.grpccore.FactorialRequest;
import io.github.isopov.k8stest.grpccore.FactorialsGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


}


@Service
class GrpcServerService extends FactorialsGrpc.FactorialsImplBase {
    @Override
    public void compute(FactorialRequest request,
                        StreamObserver<FactorialReply> responseObserver) {
        int arg = request.getValue();
        var result = BigInteger.ONE;
        for (int i = 1; i <= arg; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        var reply = FactorialReply.newBuilder().setValue(result.doubleValue()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
