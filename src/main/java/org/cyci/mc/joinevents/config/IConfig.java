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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.actions.HealAction;
import org.cyci.mc.joinevents.actions.MessageAction;
import org.cyci.mc.joinevents.manager.CooldownManager;
import org.cyci.mc.joinevents.parsers.BossBarParser;
import org.cyci.mc.joinevents.parsers.FireworkParser;
import org.cyci.mc.joinevents.parsers.SoundParser;
import org.cyci.mc.joinevents.utils.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Sun - 20/Aug/2023 - 11:14 PM
 */
public class IConfig {
    private static FileConfiguration file;
    private final String configName;

    /**
     * The IConfig function is used to create a new config file.
     *
     *
     * @param FileConfiguration file Get the file that you want to save/load from
     * @param String configName Get the name of the config file
     *
     * @return The file and the configname
     *
     */
    public IConfig(FileConfiguration file, String configName) {
        this.file = file;
        this.configName = configName;
    }

    /**
     * The getFile function returns the file configuration of the config.yml file
     *
     *
     *
     * @return The file variable
     *
     */
    public FileConfiguration getFile() {
        return this.file;
    }

    /**
     * The contains function checks to see if the file contains a path.
     *
     *
     * @param String path Find the path of the file
     *
     * @return True if the path is a file or directory
     *
     */
    public boolean contains(String path) {
        return file.contains(path);
    }
    /**
     * The getString function is used to get a string from the config.yml file.
     *
     *
     * @param String path Get the value of a string from the config
     *
     * @return The value of a path in the config file
     *
     */
    public String getString(String path) {
        return file.getString(path);
    }

    /**
     * The getStringList function is used to get a list of strings from the config.yml file.
     *
     *
     * @param String path Specify the path to the desired list
     *
     * @return A list of strings
     *
     */
    public List<String> getStringList(String path) {
        return file.getStringList(path);
    }

    /**
     * The getInt function returns the integer value of a given path.
     *
     *
     * @param String path Specify the path of the file that is being read
     *
     * @return The integer value of the path
     *
     */
    public int getInt(String path) {
        return file.getInt(path);
    }


    /**
     * The getLocation function returns the location of a file or directory.
     *
     *
     * @param String path Specify the path of the file
     *
     * @return A location object
     *
     */
    public Location getLocation(String path) {
        return file.getLocation(path);
    }

    /**
     * The getAllRanks function returns a list of all the ranks in the config.yml file.
     *
     *
     *
     * @return A list of all the ranks in the config file
     *
     */
    public List<String> getAllRanks() {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks");
        assert rankSection != null;
        return rankSection != null ? new ArrayList<>(rankSection.getKeys(false)) : new ArrayList<>();
    }
    /**
     * The isRankEnabled function checks if a rank is enabled.
     *
     *
     * @param String rankId Get the rank section in the config
     *
     * @return A boolean value
     *
     */
    public boolean isRankEnabled(String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        return rankSection != null && rankSection.getBoolean(".enabled");
    }
    public boolean isRankEnabled(ConfigurationSection rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        return rankSection != null && rankSection.getBoolean(".enabled");
    }

    /**
     * The isSectionEnabled function checks if a section is enabled for a rank.
     *
     *
     * @param String rankId Get the rank section in the config
     * @param String section Get the section of the config
     *
     * @return A boolean value if section is enabled
     *
     */
    public boolean isSectionEnabled(ConfigurationSection rankSection, String section) {
        return rankSection != null && rankSection.getBoolean(section + ".enabled");
    }
    public boolean isSectionEnabled(String rankId, String section) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        return rankSection != null && rankSection.getBoolean(section + ".enabled");
    }

    /**
     * The getJoinItemNames function returns a list of all the join items for a given rank.
     *
     *
     * @param String rankId Get the rank from the config
     *
     * @return A list of keys from the joinitems section
     *
     */
    public List<String> getJoinItemNames(String rankId) {
        ConfigurationSection joinItemsSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems");
        assert joinItemsSection != null;
        return joinItemsSection != null ? new ArrayList<>(joinItemsSection.getKeys(false)) : new ArrayList<>();
    }

    /**
     * The getRankIdForPlayer function returns the rankId of a player.
     *
     *
     * @param Player player Get the player's rank
     *
     * @return The rankid of the player
     *
     */
    public String getRankIdForPlayer(Player player) {
        for (String rankId : file.getConfigurationSection("config.ranks").getKeys(false)) {
            if (hasPermission(player, rankId)) {
                return rankId;
            }
        }
        return null;
    }

    /**
     * The hasPermission function checks if a player has the permission to use a rank.
     *
     *
     * @param Player player Check if the player has a permission
     * @param String rankId Get the rank from the config
     *
     * @return A boolean value
     *
     */
    public boolean hasPermission(Player player, String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null) {
            String permission = rankSection.getString("permission");
            return permission != null && player.hasPermission(permission);
        }
        return false;
    }

    /**
     * The parseMessage function is used to parse a message from the config.yml file
     * and return it as a string. It takes in three parameters:
     *
     *
     * @param Player player Get the player's name, uuid, and other information
     * @param String rankId Get the rank section from the config
     * @param String messageType Get the message from the config
     *
     * @return A string
     *
     */
    public String parseMessage(Player player, String rankId, String messageType) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankSection, "messages")) {
            String message = rankSection.getString("messages." + messageType);
            if (message != null) {
                String finalMessage = PlaceholderAPI.setPlaceholders(player, message);
                return finalMessage;
            }
        }
        throw new IllegalStateException("No enabled rank found with a message.");
    }

    /**
     * The parseNoise function parses the noise section of a rank's configuration.
     *
     *
     * @param String rankId Get the rank from the config file
     *
     * @return A soundparser object
     *
     */
    public SoundParser parseNoise(String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        return getSoundParser(rankSection, isSectionEnabled(rankId, "noise"), isRankEnabled(rankId));
    }
    public SoundParser parseNoise(ConfigurationSection rankSection) {
        return getSoundParser(rankSection, isSectionEnabled(rankSection, "noise"), isRankEnabled(rankSection));
    }

    private SoundParser getSoundParser(ConfigurationSection rankSection, boolean noise2, boolean rankEnabled) {
        if (rankSection != null && noise2) {
            if (rankEnabled) {
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


    /**
     * The getFireworks function is used to get the fireworks that are associated with a rank.
     *
     *
     * @param String rankId Get the rank section from the config
     *
     * @return A list of fireworkparser objects
     *
     */
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

    /**
     * The parseJoinBook function is used to parse a join book for the specified rank.
     *
     *
     * @param Player player Set the placeholders in the book
     * @param String rankId Get the rank section in the config
     *
     * @return An itemstack
     *
     */
    public ItemStack parseJoinBook(Player player, String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (rankSection != null && isSectionEnabled(rankSection, "joinBook")) {
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

    /**
     * The parseBossBar function parses the boss bar configuration section of a rank.
     *
     *
     * @param String rankId Get the rank section in the config
     *
     * @return A bossbarparser object
     *
     */
    public BossBarParser parseBossBar(String rankId) {
        ConfigurationSection rankSection = file.getConfigurationSection("config.ranks." + rankId);
        if (isSectionEnabled(rankSection, "bossBar") && isRankEnabled(rankId)) {
            BarColor color = BarColor.valueOf(rankSection.getString("bossBar.color", "WHITE"));
            BarStyle style = BarStyle.valueOf(rankSection.getString("bossBar.style", "SOLID"));
            String title = rankSection.getString("bossBar.title", "Boss Bar");
            int time = rankSection.getInt("bossBar.time", 1);
            float progress = (float) rankSection.getDouble("bossBar.progress", 1.0);

            return new BossBarParser(color, style, title, time, progress);
        }

        return null;
    }

    /**
     * The parseCustomRewardNoise function parses the custom reward noise for a given reward.
     *
     *
     * @param String rewardId Get the reward section from the config
     *
     * @return A soundparser object
     *
     */
    public SoundParser parseCustomRewardNoise(String rewardId) {
        ConfigurationSection rewardsSection = file.getConfigurationSection("config.time.rewards");
        if (rewardsSection == null || !rewardsSection.contains(rewardId)) {
            return null; // No custom sound configured for this reward
        }

        ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardId);
        return parseNoise(rewardSection);
    }

    /**
     * The isRewardEnabled function checks to see if a reward is enabled in the config.yml file.
     *
     *
     * @param String rewardId Get the reward from the config
     *
     * @return A boolean value, true or false
     *
     */
    public boolean isRewardEnabled(String rewardId) {
        ConfigurationSection rewardsSection = file.getConfigurationSection("config.time.rewards");
        return rewardsSection != null && rewardsSection.getBoolean(rewardId + ".enabled");
    }

    /**
     * The getRequiredPlaytime function returns the required playtime for a given reward.
     *
     *
     * @param String rewardId Get the required playtime for a specific reward
     *
     * @return The required playtime for the reward with the given id
     *
     */
    public int getRequiredPlaytime(String rewardId) {
        ConfigurationSection rewardsSection = file.getConfigurationSection("config.time.rewards");
        return rewardsSection != null ? rewardsSection.getInt(rewardId + ".required-playtime") : 0;
    }
    /**
     * The getRewardCommand function returns the command that should be executed when a player
     * receives a reward. The rankId parameter is used to determine which reward's command should
     * be returned. If no custom command has been configured for this rank, then null will be returned.

     *
     * @param String rankId Get the rankid from the config
     *
     * @return A string
     *
     */
    public String getRewardCommand(String rankId) {
        ConfigurationSection rewardsSection = file.getConfigurationSection("config.time.rewards");
        if (rewardsSection == null || !rewardsSection.contains(rankId)) {
            return null; // No custom command configured for this reward
        }

        ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rankId);
        assert rewardSection != null;
        return rewardSection.getString("command", null);
    }

    /**
     * The getRewardMultipliers function returns a map of multipliers for the specified reward.
     *
     *
     * @param String rewardId Get the reward multipliers for a specific reward
     *
     * @return A map of multipliers
     *
     */
    public Map<String, Double> getRewardMultipliers(String rewardId) {
        ConfigurationSection rewardsSection = file.getConfigurationSection("config.rewards");
        if (rewardsSection != null && rewardsSection.contains(rewardId + ".multiplier")) {
            ConfigurationSection multiplierSection = rewardsSection.getConfigurationSection(rewardId + ".multiplier");
            Map<String, Double> multipliers = new HashMap<>();
            assert multiplierSection != null;
            for (String rank : multiplierSection.getKeys(false)) {
                multipliers.put(rank, multiplierSection.getDouble(rank));
            }
            return multipliers;
        }
        return Collections.emptyMap();
    }

    /**
     * The parseActions function parses the actions section of a rank's join item.
     * It returns a list of Actions objects that can be executed by the JoinItem class.
     *
     *
     * @param String rankId Get the rank id from the config
     * @param String itemName Get the item name from the config
     * @param String clickType Determine what actions to perform when the player clicks on the item
     * @param Player player Get the player's name
     *
     * @return A list of actions
     *
     */
    public List<Actions> parseActions(String rankId, String itemName, String clickType) {
        List<Actions> actions = new ArrayList<>();
        ConfigurationSection actionsSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems." + itemName + ".actions");

        if (actionsSection != null && actionsSection.isList(clickType)) {
            List<String> actionStrings = actionsSection.getStringList(clickType);

            for (String actionString : actionStrings) {
                Actions action = parseAction(actionString);
                if (action != null) {
                    actions.add(action);
                }
            }
        }

        return actions;
    }

    /**
     * The parseAction function takes a string and returns an Action object.
     * The string is expected to be in the format of: {actionType: actionValue}
     *
     * For example, if you wanted to create a HealAction that heals for 10 health points, you would pass in the following String: {heal: 10}

     *
     * @param String actionString Get the action type and value from the string
     *
     * @return An actions object
     *
     */
    private Actions parseAction(String actionString) {
        if (actionString.startsWith("{") && actionString.endsWith("}")) {
            String actionContent = actionString.substring(1, actionString.length() - 1);
            String[] parts = actionContent.split(":", 2);

            if (parts.length == 2) {
                String actionType = parts[0].trim().toLowerCase();
                String actionValue = parts[1].trim();

                switch (actionType) {
                    case "heal":
                        return new HealAction();
                    case "msg":
                        if (actionValue.startsWith("Player:")) {
                            String message = actionValue.substring("Player:".length()).trim();
                            message = message.replace("'", "");

                            return new MessageAction(message);
                        } else {
                            actionValue = actionValue.replace("'", "");
                            return new MessageAction(actionValue);
                        }
                }
            }
        }

        return null;
    }

    /**
     * The parseJoinItem function is used to parse the join items for a rank.
     *
     *
     * @param String rankId Get the rank from the config
     * @param String itemName Get the item from the config
     * @param Player player Get the player's uuid
     *
     * @return An itemstack
     *
     */
    public ItemStack parseJoinItem(String rankId, String itemName) {
        ConfigurationSection joinItemsSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems." + itemName);
        if (joinItemsSection != null && joinItemsSection.getBoolean("enabled")) {
            if (isRankEnabled(rankId)) {
                Material material = Material.getMaterial(Objects.requireNonNull(joinItemsSection.getString("material")));
                String name = joinItemsSection.getString("name");
                List<String> lore = joinItemsSection.getStringList("lore");
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                ConfigurationSection nameTagSection = joinItemsSection.getConfigurationSection("nameTag");
                assert nameTagSection != null;
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
                List<Component> loreList = new ArrayList<>();
                for (String loreLine : lore) {
                    loreList.add(C.c(loreLine));
                }
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

    /**
     * The isCustomItem function checks if the item is a custom item.
     *
     *
     * @param ItemStack item Check if the item has a custom name and lore
     * @param String rankId Get the rankid of the player
    public string getrankidforplayer(player player) {
            if (player == null) {
                return null;
            }

            for (string rank : file
     * @param String itemName Get the configuration section for that item
     * @param Player player Get the player's rankid
    public string getrankidforplayer(player player) {
            if (player == null || !player
     *
     * @return True if the item has the expected nbt tag, but i don't know how to check for that
     *
     */
    public boolean isCustomItem(ItemStack item, String rankId, String itemName, Player player) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ConfigurationSection customItemSection = file.getConfigurationSection("config.ranks." + rankId + ".joinItems." + itemName);

        if (customItemSection == null || !customItemSection.getBoolean("enabled")) {
            return false;
        }

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
                boolean unlimitedUses = customItemSection.getBoolean("unlimitedUses", false);

                /*
                * Bug fix 10/19/23
                * Bug: If config section unlimitedUses didn't exist, app would error out
                * add check for null config section.
                */
                if (!unlimitedUses || customItemSection.getConfigurationSection("unlimitedUses") == null) {
                    if (CustomNBTUtil.getStringNBTValue(item, "uses") == null) {
                        CustomNBTUtil.setIntNBTValue(item, "uses", 0);
                    }

                    int currentUses = CustomNBTUtil.getIntNBTValue(item, "uses");

                    if (currentUses >= uses) {
                        player.getInventory().removeItem(item);
                        return false;
                    }

                    currentUses++;
                    CustomNBTUtil.setIntNBTValue(item, "uses", currentUses);
                }
            }

            return true;
        }

        return false;
    }
    public static void setFile(FileConfiguration config) {
        file = config;
    }
}