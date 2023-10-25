package org.cyci.mc.joinevents.config;

import org.yaml.snakeyaml.Yaml;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Thu - 12/Oct/2023 - 7:58 PM
 */
public class Config {
    private String serverName;
    private int maxPlayers;

    // Getters and setters

    public String serialize() {
        Yaml yaml = new Yaml();
        return yaml.dump(this);
    }

    public static Config deserialize(String yamlString) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(yamlString, Config.class);
    }
}