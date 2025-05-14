package com.example.concertbooking.client;

import com.example.concertbooking.grpc.ReservationResponse;
import com.example.concertbooking.grpc.ReservationServiceGrpc;
import com.example.concertbooking.grpc.ReserveRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Reserve2PCClient {

    // Change VIP seat number as 1 and after party tickets as 1 before starting the server.
    public static void main(String[] args) {

        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
        String target = selector.selectNode();
        System.out.println("Selected node for this request: " + target);

        // Connect to the server using etcd
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        ReservationServiceGrpc.ReservationServiceBlockingStub stub =
                ReservationServiceGrpc.newBlockingStub(channel);

        // Build the 2PC reservation request
        ReserveRequest request = ReserveRequest.newBuilder()
                .setConcertId("rockfest2027")
                .setUserId("user2pc")
                .setSeatTier("VIP")
                .setIncludeAfterParty(true) // Trigger 2PC logic
                .setNumberOfTickets(3) // Number of combo tickets to reserve
                .build();

        // Call the new method
        ReservationResponse response = stub.reserveWithAfterParty(request);
        System.out.println("🎟PC Response: " + response.getMessage());

        channel.shutdown();
    }
}
