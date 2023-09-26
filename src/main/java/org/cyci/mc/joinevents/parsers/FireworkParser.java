package org.cyci.mc.joinevents.parsers;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;


/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 21/Aug/2023 - 9:47 PM
 */
public class FireworkParser {
    private final Color mainColor;
    private final Color fadeColor;
    private final boolean flicker;
    private final boolean trail;
    private final FireworkEffect.Type type;

    public FireworkParser(Color mainColor, Color fadeColor, boolean flicker, boolean trail, FireworkEffect.Type type) {
        this.mainColor = mainColor;
        this.fadeColor = fadeColor;
        this.flicker = flicker;
        this.trail = trail;
        this.type = type;
    }

    public Color getMainColor() {
        return mainColor;
    }

    public Color getFadeColor() {
        return fadeColor;
    }

    public boolean hasFlicker() {
        return flicker;
    }

    public boolean hasTrail() {
        return trail;
    }

    public FireworkEffect.Type getFireworkType() {
        return type;
    }

    public static void shootFirework(Location location, FireworkParser parser) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .with(parser.getFireworkType())
                .withColor(parser.getMainColor())
                .withFade(parser.getFadeColor())
                .flicker(parser.hasFlicker())
                .trail(parser.hasTrail())
                .build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }
}