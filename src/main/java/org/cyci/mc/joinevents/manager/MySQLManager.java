package org.cyci.mc.joinevents.manager;

import org.cyci.mc.joinevents.Registry;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 02/Oct/2023 - 4:35 PM
 */
public class MySQLManager {
    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MySQLManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public CompletableFuture<Void> connectAsync() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                Registry.instance.getLogger().info("Connected to MySQL!");

                // Create the player_data table if it doesn't exist
                createPlayerDataTableAsync();
            } catch (SQLException e) {
                Registry.instance.getLogger().severe("Failed to connect to MySQL: " + e.getMessage());
            }
        }, executorService);

        return future;
    }

    public CompletableFuture<Void> closeConnectionAsync() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            if (connection != null) {
                try {
                    connection.close();
                    Registry.instance.getLogger().info("Closed MySQL connection.");
                } catch (SQLException e) {
                    Registry.instance.getLogger().severe("Error while closing MySQL connection: " + e.getMessage());
                }
            }
        }, executorService);

        return future;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public Connection getConnection() {
        return connection;
    }

    public void createPlayerDataTableAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS player_data ("
                            + "uuid VARCHAR(36) PRIMARY KEY,"
                            + "logins INT NOT NULL DEFAULT 0,"
                            + "playtime_minutes INT NOT NULL DEFAULT 0"
                            + ");";
                    statement.execute(createTableSQL);
                    statement.close();
                    Registry.instance.getLogger().info("Creating player_data table if it doesn't exist.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, executorService);
    }
}