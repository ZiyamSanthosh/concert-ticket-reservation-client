package com.example.concertbooking.client;

import com.example.concertbooking.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CancelConcertClient {

    public static void main(String[] args) {

        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
        String target = selector.selectNode();
        System.out.println("Sending cancelConcert request to: " + target);

        CancelConcertRequest request = CancelConcertRequest.newBuilder()
                .setConcertId("rockfest2027")
                .build();

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        ConcertServiceGrpc.ConcertServiceBlockingStub stub =
                ConcertServiceGrpc.newBlockingStub(channel);

        ConcertResponse response = stub.cancelConcert(request);
        System.out.println("Cancel Response: " + response.getMessage());

        channel.shutdown();
    }
}

