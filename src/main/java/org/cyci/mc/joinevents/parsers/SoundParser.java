package org.cyci.mc.joinevents.parsers;

import org.bukkit.Sound;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 21/Aug/2023 - 3:37 PM
 */
public class SoundParser {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundParser(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}