package com.mickymaus209.msg.spigot.data.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQLManager {
    private final HikariDataSource dataSource;
    private final ExecutorService dbExecutor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "Msg-DB");
        t.setDaemon(true);
        return t;
    });

    public MySQLManager(String host, String port, String username, String password) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/mcdb"
                + "?useUnicode=true&characterEncoding=utf8"
                + "&serverTimezone=UTC"
                + "&cachePrepStmts=true&prepStmtCacheSize=250&prepStmtCacheSqlLimit=2048"
                + "&useServerPrepStmts=true&rewriteBatchedStatements=true"
                + "&tcpKeepAlive=true");
        cfg.setUsername(username);
        cfg.setPassword(password);

        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(10000);
        cfg.setValidationTimeout(3000);
        cfg.setIdleTimeout(600000);        // 10 min
        cfg.setMaxLifetime(1700000);       // 28m20s (< MySQL default 30m)
        cfg.setKeepaliveTime(300000);      // 5 min ping

        cfg.setPoolName("Msg-Hikari");

        this.dataSource = new HikariDataSource(cfg);
    }

    public CompletableFuture<Void> createTablesAsync() {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid CHAR(36) NOT NULL," +
                        "deactivated BOOLEAN NOT NULL DEFAULT FALSE," +
                        "PRIMARY KEY (uuid))");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_blocks (" +
                        "player_uuid CHAR(36) NOT NULL," +
                        "blocked_uuid CHAR(36) NOT NULL," +
                        "PRIMARY KEY (player_uuid, blocked_uuid)," +
                        "FOREIGN KEY (player_uuid) REFERENCES player_data(uuid) ON DELETE CASCADE)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, dbExecutor);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void close() {
        if (dataSource != null) dataSource.close();
        dbExecutor.shutdown();
    }
}
