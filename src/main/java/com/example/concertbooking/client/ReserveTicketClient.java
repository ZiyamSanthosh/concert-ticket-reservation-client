package com.example.concertbooking.client;

import com.example.concertbooking.grpc.ReservationResponse;
import com.example.concertbooking.grpc.ReservationServiceGrpc;
import com.example.concertbooking.grpc.ReserveRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ReserveTicketClient {

    public static void main(String[] args) {
        // Connect to the gRPC server
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

        // Create a reservation request
        ReserveRequest request = ReserveRequest.newBuilder()
                .setConcertId("rockfest2027")
                .setUserId("user123")
                .setSeatTier("VIP")
                .setIncludeAfterParty(true)
                .build();

        // Make the reservation
        ReservationResponse response = stub.reserveTickets(request);

        // Print the result
        System.out.println("🎫 Reserve Response: " + response.getMessage());

        channel.shutdown();
    }
}
