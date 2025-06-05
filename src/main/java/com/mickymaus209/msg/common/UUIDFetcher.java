package com.mickymaus209.msg.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UUIDFetcher {
    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final ConcurrentHashMap<String, UUID> cache = new ConcurrentHashMap<>();

    public static CompletableFuture<UUID> getUUID(String playerName) {
        // Return from cache if available
        if (cache.containsKey(playerName.toLowerCase())) {
            return CompletableFuture.completedFuture(cache.get(playerName.toLowerCase()));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(MOJANG_API_URL + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == 204) {
                    // Player not found
                    return null;
                }

                if (responseCode == 429) {
                    // Too many requests
                    System.out.println("[MSG] UUIDFetcher has reached its limit! Try again later.");
                    return null;
                }

                if (responseCode != 200) {
                    //System.out.println("[UUIDFetcher] Error: HTTP " + responseCode);
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();

                String id = json.get("id").getAsString();
                UUID uuid = UUID.fromString(id.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"));
                cache.put(playerName.toLowerCase(), uuid);
                return uuid;

            } catch (Exception e) {
                System.out.println("[UUIDFetcher] Error fetching UUID: " + e.getMessage());
                return null;
            }
        });
    }
}
