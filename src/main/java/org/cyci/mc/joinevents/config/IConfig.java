package org.cyci.mc.joinevents.config;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.manager.CooldownManager;
import org.cyci.mc.joinevents.parsers.BossBarParser;
import org.cyci.mc.joinevents.parsers.FireworkParser;
import org.cyci.mc.joinevents.parsers.SoundParser;
import org.cyci.mc.joinevents.utils.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Sun - 20/Aug/2023 - 11:14 PM
 */
public class IConfig {
    private final FileConfiguration file;
    private final String configName;

    public IConfig(FileConfiguration file, String configName) {
        this.file = file;
        this.configName = configName;
    }

    public String getString(String path) {
        return file.getString(path);
    }

    public List<String> getStringList(String path) {
        return file.getStringList(path);
    }

    public boolean isRankEnabled(String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        return rankSection != null && rankSection.getBoolean(".enabled");
    }

    public boolean isSectionEnabled(String rankId, String section) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        return rankSection != null && rankSection.getBoolean(section + ".enabled");
    }

    public List<String> getJoinItemNames(String rankId) {
        ConfigurationSection joinItemsSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems");
        assert joinItemsSection != null;
        return joinItemsSection != null ? new ArrayList<>(joinItemsSection.getKeys(false)) : new ArrayList<>();
    }

    public String getRankIdForPlayer(Player player) {
        for (String rankId : file.getConfigurationSection("config.ranks").getKeys(false)) {
            if (hasPermission(player, rankId)) {
                return rankId;
            }
        }
        return null;
    }

    public boolean hasPermission(Player player, String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null) {
            String permission = rankSection.getString("permission");
            return permission != null && player.hasPermission(permission);
        }
        return false;
    }

    public String parseMessage(Player player, String rankId, String messageType) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankId, "messages")) {
            String message = rankSection.getString("messages." + messageType);
            if (message != null) {
                String finalMessage = PlaceholderAPI.setPlaceholders(player, message);
                return finalMessage;
            }
        }
        throw new IllegalStateException("No enabled rank found with a message.");
    }

    public SoundParser parseNoise(String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankId, "noise")) {
            if (isRankEnabled(rankId)) {
                String noise = rankSection.getString("noise.sound");
                if (noise != null) {
                    Sound sound = Sound.valueOf(noise);
                    float volume = (float) rankSection.getDouble("noise.volume", 1.0);
                    float pitch = (float) rankSection.getDouble("noise.pitch", 1.0);
                    return new SoundParser(sound, volume, pitch);
                }
            }
        }
        return null;
    }

    public List<FireworkParser> getFireworks(String rankId) {
        List<FireworkParser> fireworks = new ArrayList<>();
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankId, "fireworks")) {
            if (isRankEnabled(rankId)) {
                Color mainColor = Color.fromRGB(rankSection.getInt("mainColor", 16777215));
                Color fadeColor = Color.fromRGB(rankSection.getInt("fadeColor", 16777215));
                boolean flicker = rankSection.getBoolean("flicker", false);
                boolean trail = rankSection.getBoolean("trail", false);
                FireworkEffect.Type type = FireworkEffect.Type.valueOf(rankSection.getString("type", "BALL"));

                FireworkParser fireworkParser = new FireworkParser(mainColor, fadeColor, flicker, trail, type);
                fireworks.add(fireworkParser);
            }
        }
        return fireworks;
    }

    public ItemStack parseJoinBook(Player player, String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankId, "joinBook")) {
            if (isRankEnabled(rankId)) {
                ItemStack joinBook = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta bookMeta = (BookMeta) joinBook.getItemMeta();

                bookMeta.setTitle(PlaceholderAPI.setPlaceholders(player, rankSection.getString("joinBook.title", "Join Book")));
                bookMeta.setAuthor(PlaceholderAPI.setPlaceholders(player, rankSection.getString("joinBook.author", "Server")));

                List<String> pages = rankSection.getStringList("joinBook.pages");
                for (String page : pages)
                    bookMeta.addPages(C.c(PlaceholderAPI.setPlaceholders(player, page)));

                bookMeta.getPersistentDataContainer().set(Registry.UNMOVABLE_TAG_KEY, PersistentDataType.BYTE, (byte) 1);

                joinBook.setItemMeta(bookMeta);

                return joinBook;
            }
        }
        return null;
    }

    public BossBarParser parseBossBar(String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankId, "bossBar")) {
            if (isRankEnabled(rankId)) {
                BarColor color = BarColor.valueOf(rankSection.getString("bossBar.color", "WHITE"));
                BarStyle style = BarStyle.valueOf(rankSection.getString("bossBar.style", "SOLID"));
                String title = rankSection.getString("bossBar.title", "Boss Bar");
                int time = rankSection.getInt("bossBar.time", 1);
                float progress = (float) rankSection.getDouble("bossBar.progress", 1.0);

                return new BossBarParser(color, style, title, time, progress);
            }
        }
        return null;
    }

    public ItemStack parseJoinItem(String rankId, String itemName, Player player) {
        ConfigurationSection joinItemsSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems." + itemName);
        if (joinItemsSection != null && joinItemsSection.getBoolean("enabled")) {
            if (isRankEnabled(rankId)) {
                Material material = Material.getMaterial(Objects.requireNonNull(joinItemsSection.getString("material")));
                String name = joinItemsSection.getString("name");
                List<String> lore = joinItemsSection.getStringList("lore");
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                ConfigurationSection nameTagSection = joinItemsSection.getConfigurationSection("nameTag");
                String nameTagName = nameTagSection.getString("nbtTagName");
                String nameTagValue = nameTagSection.getString("nbtValue");
                ConfigurationSection enchantmentsConfig = joinItemsSection.getConfigurationSection("enchantments");
                if (enchantmentsConfig != null) {
                    for (String enchantmentName : enchantmentsConfig.getKeys(false)) {
                        Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(Registry.instance, enchantmentName.toLowerCase()));
                        if (enchantment != null) {
                            int level = enchantmentsConfig.getInt(enchantmentName);
                            enchantments.put(enchantment, level);
                        }
                    }
                }

                assert material != null;
                ItemStack itemStack = new ItemStack(material);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(C.c(name));
                @Nullable List<Component> loreList = itemMeta.lore();
                if (loreList == null) {
                    loreList = new ArrayList<>();
                }
                loreList.addAll(C.c(lore.toString()).children());
                itemMeta.lore(loreList);
                itemStack.addUnsafeEnchantments(enchantments);

                if (joinItemsSection.getBoolean("unmoveable")) {
                    CustomNBTUtil.setStringNBTValue(itemStack, "unmovable", "true");
                }

                itemStack.setItemMeta(itemMeta);
                CustomNBTUtil.setStringNBTValue(itemStack, nameTagName, nameTagValue);
                return itemStack;
            }
        }
        Registry.instance.getLogger().info("Failed to create join item for rank: " + rankId + ", item: " + itemName);
        return null;
    }

    public boolean isCustomItem(ItemStack item, String rankId, String itemName, Player player) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ConfigurationSection customItemSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems." + itemName);

        if (customItemSection == null || !customItemSection.getBoolean("enabled")) {
            return false;
        }

        // Check if the item has the expected NBT tag
        String nbtTagName = customItemSection.getString("nameTag.nbtTagName");
        String expectedNbtValue = customItemSection.getString("nameTag.nbtValue");

        if (nbtTagName == null || expectedNbtValue == null) {
            return false;
        }

        String actualNbtValue = CustomNBTUtil.getStringNBTValue(item, nbtTagName);

        if (!expectedNbtValue.equals(actualNbtValue)) {
            return false;
        }
        String rankForItem = getRankIdForPlayer(player);

        if (rankForItem != null && rankForItem.equals(rankId)) {
            if (customItemSection.isInt("uses")) {
                int uses = customItemSection.getInt("uses");
                int currentUses = CustomNBTUtil.getIntNBTValue(item, "uses");

                if (currentUses >= uses) {
                    player.getInventory().removeItem(item);
                    return false;
                }
                currentUses++;
                CustomNBTUtil.setIntNBTValue(item, "uses", currentUses);
            }

            return true;
        }

        return false;
    }
}