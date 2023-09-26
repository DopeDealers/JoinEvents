package org.cyci.mc.joinevents.utils;


import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 05/Sep/2023 - 7:19 PM
 */
public class CustomNBTUtil {

    // Method to get a string NBT tag value from an ItemStack
    public static String getStringNBTValue(ItemStack item, String tag) {
        String getNBTValue = NBT.get(item, nbt -> nbt.getString(tag));
        if (!getNBTValue.isEmpty()) return getNBTValue;
        return null; // Tag not found
    }

    // Method to set a string NBT tag value on an ItemStack
    public static void setStringNBTValue(ItemStack item, String tag, String value) {
        NBT.modify(item, nbt -> {
            nbt.setString(tag, value);
        });
    }


    public static int getIntNBTValue(ItemStack item, String uses) {
        return NBT.get(item, nbt -> nbt.getInteger(uses));
    }

    public static void setIntNBTValue(ItemStack item, String uses, int currentUses) {
        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setInteger(uses, currentUses));
    }
}