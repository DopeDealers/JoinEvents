package org.cyci.mc.joinevents.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public MySQLManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void createPlayerData(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO player_data (uuid, playtime_minutes, logins) VALUES (?, 0, 0)"
            );
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}