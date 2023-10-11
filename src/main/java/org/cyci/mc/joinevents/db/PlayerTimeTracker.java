package org.cyci.mc.joinevents.db;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.manager.MySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 02/Oct/2023 - 4:37 PM
 */
public class PlayerTimeTracker {
    private final HikariDataSource dataSource;

    public PlayerTimeTracker(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addPlayerIfNotExists(Player player) {
        String uuid = player.getUniqueId().toString();

        if (!playerExists(uuid)) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement statement = conn.prepareStatement(
                         "INSERT INTO player_data (uuid, logins, playtime_minutes) VALUES (?, 0, 0)")) {
                statement.setString(1, uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean playerExists(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT COUNT(*) AS count FROM player_data WHERE uuid = ?")) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void recordLogin(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "UPDATE player_data SET logins = logins + 1 WHERE uuid = ?")) {

            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recordPlaytime(String uuid, int minutes) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "UPDATE player_data SET playtime_minutes = playtime_minutes + ? WHERE uuid = ?")) {

            statement.setInt(1, minutes);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlaytime(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT playtime_minutes FROM player_data WHERE uuid = ?")) {

            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("playtime_minutes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getLogins(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT logins FROM player_data WHERE uuid = ?")) {

            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("logins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}