package org.cyci.mc.joinevents.db;

import org.bukkit.entity.Player;
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
    private final MySQLManager mysqlManager;

    public PlayerTimeTracker(MySQLManager mysqlManager) {
        this.mysqlManager = mysqlManager;
    }

    public void recordLogin(Player player) {
        String uuid = player.getUniqueId().toString();
        try {
            Connection connection = mysqlManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE player_data SET logins = logins + 1 WHERE uuid = ?"
            );
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recordPlaytime(Player player, int minutes) {
        String uuid = player.getUniqueId().toString();
        try {
            Connection connection = mysqlManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE player_data SET playtime_minutes = playtime_minutes + ? WHERE uuid = ?"
            );
            statement.setInt(1, minutes);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlaytime(Player player) {
        String uuid = player.getUniqueId().toString();
        try {
            Connection connection = mysqlManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT playtime_minutes FROM player_data WHERE uuid = ?"
            );
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

    public int getLogins(Player player) {
        String uuid = player.getUniqueId().toString();
        try {
            Connection connection = mysqlManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT logins FROM player_data WHERE uuid = ?"
            );
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