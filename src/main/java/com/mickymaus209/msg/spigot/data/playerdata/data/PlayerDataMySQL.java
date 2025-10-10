package com.mickymaus209.msg.spigot.data.playerdata.data;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.playerdata.PlayerData;
import com.mickymaus209.msg.spigot.data.sql.MySQLManager;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerDataMySQL extends PlayerData {
    private final UUID uuid;
    private final MySQLManager mySQLManager;
    private boolean deactivated;
    private List<UUID> ignoredPlayers;
    public static Map<UUID, PlayerDataMySQL> DATA = new HashMap<>();

    public PlayerDataMySQL(Msg msg, UUID uuid) {
        this.uuid = uuid;
        this.mySQLManager = msg.getPlayerDataManager().getMySQLManager();
        ignoredPlayers = new ArrayList<>();
        DATA.put(this.uuid, this);
    }

    @Override
    public CompletableFuture<Void> loadPlayerData() {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                // 1) Load (or create) main row
                try (PreparedStatement ps = conn.prepareStatement("SELECT deactivated FROM player_data WHERE uuid = ?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next())
                            this.deactivated = rs.getBoolean("deactivated");
                        else {
                            deactivated = false;
                        /*try (PreparedStatement insert = conn.prepareStatement("INSERT INTO player_data (uuid, deactivated) VALUES (?, FALSE)")) {
                            insert.setString(1, uuid.toString());
                            insert.executeUpdate();
                            this.deactivated = false;
                        }

                         */
                        }
                    }
                }

                // 2) Load blocked/ignored list
                try (PreparedStatement ps = conn.prepareStatement("SELECT blocked_uuid FROM player_blocks WHERE player_uuid = ?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next())
                            ignoredPlayers.add(UUID.fromString(rs.getString("blocked_uuid")));
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerData() {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                // Upsert player_data
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO player_data (uuid, deactivated) VALUES (?, ?) " +
                                "ON DUPLICATE KEY UPDATE deactivated = VALUES(deactivated)")) {
                    ps.setString(1, uuid.toString());
                    ps.setBoolean(2, deactivated);
                    ps.executeUpdate();
                }

                // Replace player_blocks
                try (PreparedStatement del = conn.prepareStatement(
                        "DELETE FROM player_blocks WHERE player_uuid = ?")) {
                    del.setString(1, uuid.toString());
                    del.executeUpdate();
                }

                if (ignoredPlayers.isEmpty())
                    return;

                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO player_blocks (player_uuid, blocked_uuid) VALUES (?, ?)")) {
                    for (UUID blocked : ignoredPlayers) {
                        ins.setString(1, uuid.toString());
                        ins.setString(2, blocked.toString());
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
                conn.commit();
            } catch (
                    SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public boolean hasIgnored(UUID uuid) {
        return ignoredPlayers.contains(uuid);
    }

    @Override
    public void ignore(UUID uuid) {
        ignoredPlayers.add(uuid);
    }

    @Override
    public void unIgnore(UUID uuid) {
        ignoredPlayers.remove(uuid);
    }

    @Override
    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

   /* public static void reloadAllPlayerData(Msg msg){
        Bukkit.getOnlinePlayers().forEach(p -> {
            PlayerDataMySQL playerDataMySQL = new PlayerDataMySQL(msg, p.getUniqueId());
            playerDataMySQL.loadPlayerData();
        });
    }

    */

    @Override
    public List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    @Override
    public void setIgnoredPlayers(List<UUID> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    @Override
    public void removeFromCache() {
        DATA.remove(uuid);
    }

    private Connection getConnection() throws SQLException {
        return mySQLManager.getDataSource().getConnection();
    }
}
