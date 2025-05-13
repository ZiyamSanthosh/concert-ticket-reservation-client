package com.example.concertbooking.client;

import com.example.concertbooking.grpc.CancelReservationRequest;
import com.example.concertbooking.grpc.ReservationResponse;
import com.example.concertbooking.grpc.ReservationServiceGrpc;
import com.example.concertbooking.grpc.ReserveRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ConcurrentCancelReserveSimulator {

    public static void main(String[] args) throws InterruptedException {
        // Step 1: Cancel a reservation by userA in a separate thread
        new Thread(() -> {
//            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
//                    .usePlaintext()
//                    .build();
            EtcdNodeSelector selector = new EtcdNodeSelector("http://localhost:2379");
            String target = selector.selectNode();
            System.out.println("📍 Selected node for this request: " + target);

            // Connect to the server using etcd
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();
            ReservationServiceGrpc.ReservationServiceBlockingStub stub =
                    ReservationServiceGrpc.newBlockingStub(channel);

            CancelReservationRequest cancelRequest = CancelReservationRequest.newBuilder()
                    .setConcertId("rockfest2025")
                    .setUserId("user123") // Make sure userA has already reserved!
                    .build();

            ReservationResponse cancelResponse = stub.cancelReservation(cancelRequest);
            System.out.println("[CANCEL user123] " + cancelResponse.getMessage());

            channel.shutdown();
        }).start();

        // Wait a moment to let the cancel thread start
        Thread.sleep(100);

        // Step 2: Start 4 threads trying to reserve VIP
        for (int i = 1; i <= 4; i++) {
            final int userNum = i;
            new Thread(() -> {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();

                ReservationServiceGrpc.ReservationServiceBlockingStub stub =
                        ReservationServiceGrpc.newBlockingStub(channel);

                ReserveRequest reserveRequest = ReserveRequest.newBuilder()
                        .setConcertId("rockfest2025")
                        .setUserId("racer" + userNum)
                        .setSeatTier("VIP")
                        .setIncludeAfterParty(false)
                        .build();

                ReservationResponse response = stub.reserveTickets(reserveRequest);
                System.out.println("[racer" + userNum + "] " + response.getMessage());

                channel.shutdown();
            }).start();
        }
    }
}
