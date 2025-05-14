package com.example.concertbooking.client;

import com.example.concertbooking.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class UpdateConcertClient {

    public static void main(String[] args) {

        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
        String target = selector.selectNode();
        System.out.println("📍 Sending updateConcert request to: " + target);

        UpdateConcertRequest request = UpdateConcertRequest.newBuilder()
                .setConcertId("rockfest2027")
                .addSeatTiers(SeatTier.newBuilder()
                        .setTierName("VIP")
                        .setTotalSeats(70)
                        .setPrice(125.00)
                        .build())
                .addSeatTiers(SeatTier.newBuilder()
                        .setTierName("Regular")
                        .setTotalSeats(180)
                        .setPrice(48.00)
                        .build())
                .setAfterPartyTickets(40)
                .build();

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        ConcertServiceGrpc.ConcertServiceBlockingStub stub =
                ConcertServiceGrpc.newBlockingStub(channel);

        ConcertResponse response = stub.updateConcert(request);
        System.out.println("🛠️ Update Response: " + response.getMessage());

        channel.shutdown();
    }
}

