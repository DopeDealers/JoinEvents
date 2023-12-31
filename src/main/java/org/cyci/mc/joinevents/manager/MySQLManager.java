package org.cyci.mc.joinevents.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.cyci.mc.joinevents.Registry;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 02/Oct/2023 - 4:35 PM
 */
public class MySQLManager {
    private HikariDataSource dataSource;
    private ExecutorService executorService;
    private AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    public MySQLManager(String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
        config.setUsername(username);
        config.setPassword(password);

        dataSource = new HikariDataSource(config);
        executorService = Executors.newCachedThreadPool();
    }


    public CompletableFuture<Void> connectAsync() {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                if (!conn.isClosed()) {
                    Registry.instance.getLogger().info("Connected to MySQL!");
                    createPlayerDataTable();
                } else {
                    Registry.instance.getLogger().severe("Connection is closed immediately after connecting.");
                }
            } catch (SQLException e) {
                String errorMessage = "Failed to connect to MySQL: " + e.getMessage();
                Registry.instance.getLogger().severe(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }, executorService);
    }

    public CompletableFuture<Void> closeConnectionAsync() {
        if (isShuttingDown.get()) {
            return CompletableFuture.completedFuture(null);
        }

        isShuttingDown.set(true);

        return CompletableFuture.runAsync(() -> {
            try {
                dataSource.close();
                Registry.instance.getLogger().info("Closed MySQL connection.");
            } catch (Exception e) {
                String errorMessage = "Error while closing MySQL connection: " + e.getMessage();
                Registry.instance.getLogger().severe(errorMessage);
            }
        }, executorService).thenRun(this::shutdownExecutorService);
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                Registry.instance.getLogger().severe("Executor service shutdown timed out.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Registry.instance.getLogger().severe("Executor service shutdown interrupted: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (isShuttingDown.compareAndSet(false, true)) {
            closeConnectionAsync().join();
            shutdownExecutorService();
            Registry.instance.getLogger().info("Executor service for MySQL manager shut down.");
            Registry.instance.getLogger().info("Plugin disabled!");
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void createPlayerDataTable() {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS player_data ("
                             + "uuid VARCHAR(36) PRIMARY KEY,"
                             + "logins INT NOT NULL DEFAULT 0,"
                             + "playtime_minutes INT NOT NULL DEFAULT 0"
                             + ");")) {

            statement.execute();
            Registry.instance.getLogger().info("Creating player_data table if it doesn't exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
}