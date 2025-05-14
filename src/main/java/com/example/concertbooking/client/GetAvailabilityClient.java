package com.example.concertbooking.client;

import com.example.concertbooking.grpc.AvailabilityRequest;
import com.example.concertbooking.grpc.AvailabilityResponse;
import com.example.concertbooking.grpc.ReservationServiceGrpc;
import com.example.concertbooking.grpc.TierAvailability;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetAvailabilityClient {

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

        // Create request for concert
        AvailabilityRequest request = AvailabilityRequest.newBuilder()
                .setConcertId("rockfest2027")
                .build();

        // Call the service
        AvailabilityResponse response = stub.getAvailability(request);

        System.out.println("Available Seats for 'rockfest2025':");
        for (TierAvailability tier : response.getSeatTiersList()) {
            System.out.println(" - " + tier.getTierName() + ": " + tier.getAvailable());
        }

        System.out.println("After-party tickets available: " + response.getAfterPartyTicketsAvailable());

        channel.shutdown();
    }
}
