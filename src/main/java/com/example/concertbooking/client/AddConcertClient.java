package com.example.concertbooking.client;

import com.example.concertbooking.grpc.AddConcertRequest;
import com.example.concertbooking.grpc.ConcertResponse;
import com.example.concertbooking.grpc.ConcertServiceGrpc;
import com.example.concertbooking.grpc.SeatTier;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AddConcertClient {

//    public static void main(String[] args) {
//
//        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
//        String target = selector.selectNode();
//        System.out.println("📍 Selected node for this request: " + target);
//
//        // Connect to the server (without etcd)
////        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
////                .usePlaintext()  // no TLS
////                .build();
//
//        // Connect to the server using etcd
//        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
//                .usePlaintext()
//                .build();
//
//        // Create the blocking stub
//        ConcertServiceGrpc.ConcertServiceBlockingStub stub =
//                ConcertServiceGrpc.newBlockingStub(channel);
//
//        // Build request
//        AddConcertRequest request = AddConcertRequest.newBuilder()
//                .setConcertId("rockfest2025")
//                .setConcertName("Rock Fest 2025")
//                .addSeatTiers(SeatTier.newBuilder()
//                        .setTierName("VIP")
//                        .setTotalSeats(50)    // previously this was 50.
//                        .setPrice(120.00)
//                        .build())
//                .addSeatTiers(SeatTier.newBuilder()
//                        .setTierName("Regular")
//                        .setTotalSeats(200)
//                        .setPrice(50.00)
//                        .build())
//                .setAfterPartyTickets(30)    // previously this was 30.
//                .build();
//
//
//        // Make the gRPC call
//        ConcertResponse response = stub.addConcert(request);
//
//        // Print result
//        System.out.println("✅ Response: " + response.getMessage());
//
//        // Cleanup
//        channel.shutdown();
//    }

    // After leader implementation
    public static void main(String[] args) {

        EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");

        String target = selector.selectNode();
        System.out.println("📍 Sending addConcert request to: " + target);

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
        System.out.println("🎤 Response from " + target + ": " + response.getMessage());

        channel.shutdown();
    }
}
