package info.faceland.loot;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.command.CommandHandler;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.settings.IvorySettings;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.CustomItemBuilder;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.managers.CustomItemManager;
import info.faceland.loot.api.managers.ItemGroupManager;
import info.faceland.loot.api.managers.NameManager;
import info.faceland.loot.api.managers.SocketGemManager;
import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.SocketGemBuilder;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.api.tier.TierBuilder;
import info.faceland.loot.commands.LootCommand;
import info.faceland.loot.groups.LootItemGroup;
import info.faceland.loot.io.SmartTextFile;
import info.faceland.loot.items.LootCustomItemBuilder;
import info.faceland.loot.items.LootItemBuilder;
import info.faceland.loot.managers.LootCustomItemManager;
import info.faceland.loot.managers.LootItemGroupManager;
import info.faceland.loot.managers.LootNameManager;
import info.faceland.loot.managers.LootSocketGemManager;
import info.faceland.loot.managers.LootTierManager;
import info.faceland.loot.sockets.LootSocketGemBuilder;
import info.faceland.loot.sockets.effects.LootSocketPotionEffect;
import info.faceland.loot.tier.LootTierBuilder;
import info.faceland.loot.utils.converters.StringConverter;
import info.faceland.utils.TextUtils;
import net.nunnerycode.java.libraries.cannonball.DebugPrinter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class LootPlugin extends FacePlugin {

    private DebugPrinter debugPrinter;
    private VersionedIvoryYamlConfiguration itemsYAML;
    private VersionedIvoryYamlConfiguration tierYAML;
    private VersionedIvoryYamlConfiguration corestatsYAML;
    private VersionedIvoryYamlConfiguration customItemsYAML;
    private VersionedIvoryYamlConfiguration socketGemsYAML;
    private VersionedIvoryYamlConfiguration languageYAML;
    private VersionedIvoryYamlConfiguration configYAML;
    private VersionedIvoryYamlConfiguration anticheatYAML;
    private IvorySettings settings;
    private ItemGroupManager itemGroupManager;
    private TierManager tierManager;
    private NameManager nameManager;
    private CustomItemManager customItemManager;
    private SocketGemManager socketGemManager;

    @Override
    public void preEnable() {
        debugPrinter = new DebugPrinter(getDataFolder().getPath(), "debug.log");
        itemsYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "items.yml"),
                                                        getResource("items.yml"),
                                                        VersionedIvoryConfiguration.VersionUpdateType
                                                                .BACKUP_AND_UPDATE);
        if (itemsYAML.update()) {
            getLogger().info("Updating items.yml");
            debug("Updating items.yml");
        }
        tierYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "tier.yml"),
                                                       getResource("tier.yml"),
                                                       VersionedIvoryConfiguration.VersionUpdateType
                                                               .BACKUP_AND_UPDATE);
        if (tierYAML.update()) {
            getLogger().info("Updating tier.yml");
            debug("Updating tier.yml");
        }
        corestatsYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "corestats.yml"),
                                                            getResource("corestats.yml"),
                                                            VersionedIvoryConfiguration.VersionUpdateType
                                                                    .BACKUP_AND_UPDATE);
        if (corestatsYAML.update()) {
            getLogger().info("Updating corestats.yml");
            debug("Updating corestats.yml");
        }
        customItemsYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "customItems.yml"),
                                                               getResource("customItems.yml"),
                                                               VersionedIvoryConfiguration.VersionUpdateType
                                                                       .BACKUP_AND_UPDATE);
        if (customItemsYAML.update()) {
            getLogger().info("Updating customItems.yml");
            debug("Updating customItems.yml");
        }
        socketGemsYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "socketGems.yml"),
                                                              getResource("socketGems.yml"),
                                                              VersionedIvoryConfiguration.VersionUpdateType
                                                                      .BACKUP_AND_UPDATE);
        if (socketGemsYAML.update()) {
            getLogger().info("Updating socketGems.yml");
            debug("Updating socketGems.yml");
        }
        languageYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "language.yml"),
                                                             getResource("language.yml"),
                                                             VersionedIvoryConfiguration.VersionUpdateType
                                                                     .BACKUP_AND_UPDATE);
        if (languageYAML.update()) {
            getLogger().info("Updating language.yml");
            debug("Updating language.yml");
        }
        configYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "config.yml"),
                                                           getResource("config.yml"),
                                                           VersionedIvoryConfiguration.VersionUpdateType
                                                                   .BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
            debug("Updating config.yml");
        }
        anticheatYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "anticheat.yml"),
                                                         getResource("anticheat.yml"),
                                                         VersionedIvoryConfiguration.VersionUpdateType
                                                                 .BACKUP_AND_UPDATE);
        if (anticheatYAML.update()) {
            getLogger().info("Updating anticheat.yml");
            debug("Updating anticheat.yml");
        }

        settings = IvorySettings.loadFromFiles(corestatsYAML, languageYAML, configYAML, anticheatYAML);

        itemGroupManager = new LootItemGroupManager();
        tierManager = new LootTierManager();
        nameManager = new LootNameManager();
        customItemManager = new LootCustomItemManager();
        socketGemManager = new LootSocketGemManager();
    }

    @Override
    public void enable() {
        loadItemGroups();
        loadTiers();
        loadNames();
        loadCustomItems();
        loadSocketGems();
    }

    private void loadSocketGems() {
        for (SocketGem sg : getSocketGemManager().getSocketGems()) {
            getSocketGemManager().removeSocketGem(sg.getName());
        }
        Set<SocketGem> gems = new HashSet<>();
        List<String> loadedSocketGems = new ArrayList<>();
        for (String key : socketGemsYAML.getKeys(false)) {
            if (!socketGemsYAML.isConfigurationSection(key)) {
                continue;
            }
            ConfigurationSection cs = socketGemsYAML.getConfigurationSection(key);
            SocketGemBuilder builder = getNewSocketGemBuilder(key);
            builder.withPrefix(cs.getString("prefix"));
            builder.withSuffix(cs.getString("suffix"));
            builder.withLore(cs.getStringList("lore"));
            builder.withWeight(cs.getDouble("weight"));
            List<SocketEffect> effects = new ArrayList<>();
            for (String eff : cs.getStringList("effects")) {
                effects.add(LootSocketPotionEffect.parseString(eff));
            }
            builder.withSocketEffects(effects);
            SocketGem gem = builder.build();
            gems.add(gem);
            loadedSocketGems.add(gem.getName());
        }
        for (SocketGem sg : gems) {
            getSocketGemManager().addSocketGem(sg);
        }
        debug("Loaded socket gems: " + loadedSocketGems.toString());
    }

    @Override
    public void postEnable() {
        CommandHandler handler = new CommandHandler(this);
        handler.registerCommands(new LootCommand(this));
        //Bukkit.getPluginManager().registerEvents(new LoginListener(this), this);
        debug("v" + getDescription().getVersion() + " enabled");
    }

    @Override
    public void preDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void disable() {

    }

    @Override
    public void postDisable() {
        socketGemManager = null;
        customItemManager = null;
        nameManager = null;
        tierManager = null;
        itemGroupManager = null;
        settings = null;
        anticheatYAML = null;
        configYAML = null;
        languageYAML = null;
        customItemsYAML = null;
        corestatsYAML = null;
        tierYAML = null;
        itemsYAML = null;
        debugPrinter = null;
    }

    public void debug(String... messages) {
        debug(Level.INFO, messages);
    }

    public void debug(Level level, String... messages) {
        if (debugPrinter != null) {
            debugPrinter.debug(level, messages);
        }
    }

    private void loadCustomItems() {
        for (CustomItem ci : getCustomItemManager().getCustomItems()) {
            getCustomItemManager().removeCustomItem(ci.getName());
        }
        Set<CustomItem> customItems = new HashSet<>();
        List<String> loaded = new ArrayList<>();
        for (String key : customItemsYAML.getKeys(false)) {
            if (!customItemsYAML.isConfigurationSection(key)) {
                continue;
            }
            ConfigurationSection cs = customItemsYAML.getConfigurationSection(key);
            CustomItemBuilder builder = getNewCustomItemBuilder(key);
            builder.withMaterial(StringConverter.toMaterial(cs.getString("material")));
            builder.withDisplayName(cs.getString("display-name"));
            builder.withLore(cs.getStringList("lore"));
            CustomItem ci = builder.build();
            customItems.add(ci);
            loaded.add(ci.getName());
        }
        for (CustomItem ci : customItems) {
            getCustomItemManager().addCustomItem(ci);
        }
        debug("Loaded custom items: "  + loaded.toString());
    }

    private void loadNames() {
        for (String s : getNameManager().getPrefixes()) {
            getNameManager().removePrefix(s);
        }
        for (String s : getNameManager().getSuffixes()) {
            getNameManager().removeSuffix(s);
        }

        File prefixFile = new File(getDataFolder(), "prefix.txt");
        File suffixFile = new File(getDataFolder(), "suffix.txt");

        SmartTextFile.writeToFile(getResource("prefix.txt"), prefixFile, true);
        SmartTextFile.writeToFile(getResource("suffix.txt"), suffixFile, true);

        SmartTextFile smartPrefixFile = new SmartTextFile(prefixFile);
        SmartTextFile smartSuffixFile = new SmartTextFile(suffixFile);

        for (String s : smartPrefixFile.read()) {
            getNameManager().addPrefix(s);
        }
        for (String s : smartSuffixFile.read()) {
            getNameManager().addSuffix(s);
        }

        debug("Loaded prefixes: " + getNameManager().getPrefixes().size(), "Loaded suffixes: " + getNameManager()
                .getSuffixes().size());
    }

    private void loadItemGroups() {
        for (ItemGroup ig : getItemGroupManager().getItemGroups()) {
            getItemGroupManager().removeItemGroup(ig.getName());
        }
        Set<ItemGroup> itemGroups = new HashSet<>();
        List<String> loadedItemGroups = new ArrayList<>();
        for (String key : itemsYAML.getKeys(false)) {
            if (!itemsYAML.isList(key)) {
                continue;
            }
            List<String> list = itemsYAML.getStringList(key);
            ItemGroup ig = new LootItemGroup(key, false);
            for (String s : list) {
                Material m = StringConverter.toMaterial(s);
                if (m == Material.AIR) {
                    continue;
                }
                ig.addMaterial(m);
            }
            itemGroups.add(ig);
            loadedItemGroups.add(key);
        }
        for (ItemGroup ig : itemGroups) {
            getItemGroupManager().addItemGroup(ig);
        }
        debug("Loaded item groups: " + loadedItemGroups.toString());
    }

    private void loadTiers() {
        for (Tier t : getTierManager().getLoadedTiers()) {
            getTierManager().removeTier(t.getName());
        }
        Set<Tier> tiers = new HashSet<>();
        List<String> loadedTiers = new ArrayList<>();
        for (String key : tierYAML.getKeys(false)) {
            if (!tierYAML.isConfigurationSection(key)) {
                continue;
            }
            ConfigurationSection cs = tierYAML.getConfigurationSection(key);
            TierBuilder builder = getNewTierBuilder(key);
            builder.withDisplayName(cs.getString("display-name"));
            builder.withDisplayColor(TextUtils.convertTag(cs.getString("display-color")));
            builder.withIdentificationColor(TextUtils.convertTag(cs.getString("identification-color")));
            builder.withSpawnWeight(cs.getDouble("spawn-weight"));
            builder.withIdentifyWeight(cs.getDouble("identify-weight"));
            builder.withDistanceWeight(cs.getDouble("distance-weight"));
            builder.withMinimumSockets(cs.getInt("minimum-sockets"));
            builder.withMaximumSockets(cs.getInt("maximum-sockets"));
            builder.withMinimumBonusLore(cs.getInt("minimum-bonus-lore"));
            builder.withMaximumBonusLore(cs.getInt("maximum-bonus-lore"));
            builder.withBaseLore(cs.getStringList("base-lore"));
            builder.withBonusLore(cs.getStringList("bonus-lore"));
            List<String> sl = cs.getStringList("item-groups");
            Set<ItemGroup> itemGroups = new HashSet<>();
            for (String s : sl) {
                ItemGroup ig;
                if (s.startsWith("-")) {
                    ig = getItemGroupManager().getItemGroup(s.substring(1));
                    if (ig == null) {
                        continue;
                    }
                    ig = ig.getInverse();
                } else {
                    ig = getItemGroupManager().getItemGroup(s);
                    if (ig == null) {
                        continue;
                    }
                }
                itemGroups.add(ig.getInverse());
            }
            builder.withItemGroups(itemGroups);
            builder.withMinimumDurability(cs.getDouble("minimum-durability"));
            builder.withMaximumDurability(cs.getDouble("maximum-durability"));
            Tier t = builder.build();
            loadedTiers.add(t.getName());
            tiers.add(t);
        }
        for (Tier t : tiers) {
            getTierManager().addTier(t);
        }
        debug("Loaded tiers: " + loadedTiers.toString());
    }

    public TierBuilder getNewTierBuilder(String name) {
        return new LootTierBuilder(name);
    }

    public ItemBuilder getNewItemBuilder() {
        return new LootItemBuilder(this);
    }

    public CustomItemBuilder getNewCustomItemBuilder(String name) {
        return new LootCustomItemBuilder(name);
    }

    public SocketGemBuilder getNewSocketGemBuilder(String name) {
        return new LootSocketGemBuilder(name);
    }

    public TierManager getTierManager() {
        return tierManager;
    }

    public ItemGroupManager getItemGroupManager() {
        return itemGroupManager;
    }

    public NameManager getNameManager() {
        return nameManager;
    }

    public IvorySettings getSettings() {
        return settings;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public SocketGemManager getSocketGemManager() {
        return socketGemManager;
    }

}
