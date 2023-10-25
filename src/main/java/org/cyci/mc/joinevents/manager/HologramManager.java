package org.cyci.mc.joinevents.manager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.utils.C;

import java.util.ArrayList;
import java.util.List;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Wed - 25/Oct/2023 - 12:23 PM
 */
public class HologramManager {
    private final ProtocolManager protocolManager;
    private final List<ArmorStand> holograms;

    public HologramManager() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.holograms = new ArrayList<>();
    }

    public void createHologram(Location location, String text, long durationTicks) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.customName(C.c(text));
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);

        holograms.add(armorStand);

        new BukkitRunnable() {
            @Override
            public void run() {
                removeHologram(armorStand);
            }
        }.runTaskLater(Registry.instance, durationTicks);
    }

    public void removeHologram(ArmorStand armorStand) {
        armorStand.remove();
        holograms.remove(armorStand);
    }

    public void removeAllHolograms() {
        for (ArmorStand hologram : holograms) {
            removeHologram(hologram);
        }
    }

    public void showHologramToPlayer(Player player, ArmorStand armorStand) {
        protocolManager.updateEntity((Entity) armorStand, (List<Player>) player);
    }

    public void hideHologramFromPlayer(Player player, ArmorStand armorStand) {
        protocolManager.updateEntity((Entity) armorStand, (List<Player>) player);
    }

    public void showHologramsToPlayer(Player player) {
        for (ArmorStand hologram : holograms) {
            showHologramToPlayer(player, hologram);
        }
    }

    public void hideHologramsFromPlayer(Player player) {
        for (ArmorStand hologram : holograms) {
            hideHologramFromPlayer(player, hologram);
        }
    }
}
