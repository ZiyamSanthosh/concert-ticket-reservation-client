package com.example.concertbooking.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class EtcdNodeSelector {

    private final String etcdUrl;

    public EtcdNodeSelector(String etcdUrl) {
        this.etcdUrl = etcdUrl; // e.g., http://localhost:2379
    }

    public String selectNode() {
        List<String> nodes = getRegisteredNodes();
        if (nodes.isEmpty()) {
            throw new IllegalStateException("No nodes registered in etcd.");
        }
        // You can use round-robin, random, etc. Here we use random
        Collections.shuffle(nodes);
        return nodes.get(0); // Pick one node to connect
    }

    private List<String> getRegisteredNodes() {
        List<String> nodes = new ArrayList<>();
        try {
            URL url = new URL(etcdUrl + "/v3/kv/range");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("key", base64("/concert_nodes/"));
            body.put("range_end", base64(getPrefixEnd("/concert_nodes/")));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            JSONArray kvs = response.optJSONArray("kvs");
            if (kvs != null) {
                for (int i = 0; i < kvs.length(); i++) {
                    JSONObject kv = kvs.getJSONObject(i);
                    String value = kv.getString("value");
                    String decoded = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
                    nodes.add(decoded); // e.g., "localhost:50051"
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            System.out.println("❌ etcd fetch error: " + e.getMessage());
        }

        return nodes;
    }

    private String base64(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    private String getPrefixEnd(String prefix) {
        byte[] bytes = prefix.getBytes(StandardCharsets.UTF_8);
        bytes[bytes.length - 1]++;
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
