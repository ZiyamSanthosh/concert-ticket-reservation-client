package com.example.concertbooking.client;

import com.example.concertbooking.grpc.AddConcertRequest;
import com.example.concertbooking.grpc.ConcertResponse;
import com.example.concertbooking.grpc.ConcertServiceGrpc;
import com.example.concertbooking.grpc.SeatTier;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AddConcertClient {

    public static void main(String[] args) {

        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");

        String target = selector.selectNode();
        System.out.println("Sending addConcert request to: " + target);

        AddConcertRequest request = AddConcertRequest.newBuilder()
                .setConcertId("rockfest2027")
                .setConcertName("Rock Fest 2026")
                .addSeatTiers(SeatTier.newBuilder()
                        .setTierName("VIP")
                        .setTotalSeats(50)
                        .setPrice(120.00)
                        .build())
                .addSeatTiers(SeatTier.newBuilder()
                        .setTierName("Regular")
                        .setTotalSeats(200)
                        .setPrice(50.00)
                        .build())
                .setAfterPartyTickets(30)
                .build();

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        ConcertServiceGrpc.ConcertServiceBlockingStub stub =
                ConcertServiceGrpc.newBlockingStub(channel);

        ConcertResponse response = stub.addConcert(request);
        System.out.println("Response from " + target + ": " + response.getMessage());

        channel.shutdown();
    }
}
