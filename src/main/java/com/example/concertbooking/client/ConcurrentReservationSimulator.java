package com.example.concertbooking.client;

import com.example.concertbooking.grpc.ReservationResponse;
import com.example.concertbooking.grpc.ReservationServiceGrpc;
import com.example.concertbooking.grpc.ReserveRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ConcurrentReservationSimulator {

    // Change VIP seat number as 2 before starting the server.
    public static void main(String[] args) {
        int threads = 5;

        for (int i = 0; i < threads; i++) {
            final int userNum = i;
            new Thread(() -> {
                // Each thread makes its own gRPC channel and stub
//                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
//                        .usePlaintext()
//                        .build();

                EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
                String target = selector.selectNode();
                System.out.println("📍 Selected node for this request: " + target);

                // Connect to the server using etcd
                ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                        .usePlaintext()
                        .build();

                ReservationServiceGrpc.ReservationServiceBlockingStub stub =
                        ReservationServiceGrpc.newBlockingStub(channel);

                String userId = "user" + userNum;
                ReserveRequest request = ReserveRequest.newBuilder()
                        .setConcertId("rockfest2027")
                        .setUserId(userId)
                        .setSeatTier("VIP")
                        .setIncludeAfterParty(false)
                        .build();

                ReservationResponse response = stub.reserveTickets(request);
                System.out.println("[" + userId + "] " + response.getMessage());

                channel.shutdown();
            }).start();
        }
    }
}
