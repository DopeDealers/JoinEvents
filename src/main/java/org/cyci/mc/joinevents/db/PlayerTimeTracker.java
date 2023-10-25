package org.cyci.mc.joinevents.db;

import com.zaxxer.hikari.HikariDataSource;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.manager.MySQLManager;
import org.cyci.mc.joinevents.utils.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 02/Oct/2023 - 4:37 PM
 */
public class PlayerTimeTracker {
    private final HikariDataSource dataSource;
    private List<PlayerData> topPlayers = new ArrayList<>();
    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());


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

    public void displayPlaytimeLeaderboard(int numPlayers) {
        List<PlayerData> topPlayers = getTopPlayers(numPlayers);
        Location scoreboardLocation = config.getLocation("config.leaderboard.location");
        Hologram hologram = (Hologram) HolographicDisplaysAPI.get(Registry.instance).createHologram(scoreboardLocation);

        assert topPlayers != null;
        if (topPlayers.isEmpty()) {
            hologram.getLines().insertText(0, "Leaderboard is empty.");
        } else {
            hologram.getLines().insertText(0, "Playtime Leaderboard:");

            for (int i = 0; i < topPlayers.size(); i++) {
                PlayerData player = topPlayers.get(i);
                int playtime = getPlaytime(player.getUuid());
                Player playerActual = Bukkit.getPlayer(player.getUuid());
                assert playerActual != null;
                String playerName = playerActual.getName();
                int rank = i + 1;
                hologram.getLines().insertText(rank, "#" + rank + ": " + playerName + " - " + playtime + " minutes");
            }
        }
    }


    private List<PlayerData> getTopPlayers(int numPlayers) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT * FROM player_data ORDER BY playtimes_minutes DESC LIMIT 10")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                int playtimeMinutes = resultSet.getInt("playtime_minutes");
                topPlayers.add(new PlayerData(uuid, playtimeMinutes));
            }
            return topPlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}