package com.example.concertbooking.client;

import com.example.concertbooking.grpc.CancelReservationRequest;
import com.example.concertbooking.grpc.ReservationResponse;
import com.example.concertbooking.grpc.ReservationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CancelReservationClient {

    public static void main(String[] args) {
        // Connect to gRPC server
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
//                .usePlaintext()
//                .build();

        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
        String target = selector.selectNode();
        System.out.println("📍 Selected node for this request: " + target);

        // Connect to the server using etcd
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        ReservationServiceGrpc.ReservationServiceBlockingStub stub =
                ReservationServiceGrpc.newBlockingStub(channel);

        // Build cancel request
        CancelReservationRequest request = CancelReservationRequest.newBuilder()
                .setConcertId("rockfest2027")
                .setUserId("user123")
                .build();

        // Send cancel request
        ReservationResponse response = stub.cancelReservation(request);

        System.out.println("❌ Cancel Response: " + response.getMessage());

        channel.shutdown();
    }
}
