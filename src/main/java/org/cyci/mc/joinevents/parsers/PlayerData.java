package org.cyci.mc.joinevents.parsers;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 24/Oct/2023 - 2:14 PM
 */
public class PlayerData {
    private String uuid;
    private int playtimeMinutes;

    public PlayerData(String uuid, int playtimeMinutes) {
        this.uuid = uuid;
        this.playtimeMinutes = playtimeMinutes;
    }

    public String getUuid() {
        return uuid;
    }

    public int getPlaytimeMinutes() {
        return playtimeMinutes;
    }
}
