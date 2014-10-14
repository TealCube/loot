/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

package info.faceland.loot;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.command.CommandHandler;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.settings.IvorySettings;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.creatures.CreatureModBuilder;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.enchantments.EnchantmentTomeBuilder;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.CustomItemBuilder;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.managers.AnticheatManager;
import info.faceland.loot.api.managers.CreatureModManager;
import info.faceland.loot.api.managers.CustomItemManager;
import info.faceland.loot.api.managers.EnchantmentTomeManager;
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
import info.faceland.loot.creatures.LootCreatureModBuilder;
import info.faceland.loot.enchantments.LootEnchantmentTomeBuilder;
import info.faceland.loot.groups.LootItemGroup;
import info.faceland.loot.io.SmartTextFile;
import info.faceland.loot.items.LootCustomItemBuilder;
import info.faceland.loot.items.LootItemBuilder;
import info.faceland.loot.listeners.InteractListener;
import info.faceland.loot.listeners.anticheat.AnticheatListener;
import info.faceland.loot.listeners.crafting.CraftingListener;
import info.faceland.loot.listeners.sockets.SocketsListener;
import info.faceland.loot.listeners.spawning.EntityDeathListener;
import info.faceland.loot.managers.LootAnticheatManager;
import info.faceland.loot.managers.LootCreatureModManager;
import info.faceland.loot.managers.LootCustomItemManager;
import info.faceland.loot.managers.LootEnchantmentTomeManager;
import info.faceland.loot.managers.LootItemGroupManager;
import info.faceland.loot.managers.LootNameManager;
import info.faceland.loot.managers.LootSocketGemManager;
import info.faceland.loot.managers.LootTierManager;
import info.faceland.loot.sockets.LootSocketGemBuilder;
import info.faceland.loot.sockets.effects.LootSocketPotionEffect;
import info.faceland.loot.tier.LootTierBuilder;
import info.faceland.utils.StringConverter;
import info.faceland.utils.TextUtils;
import net.nunnerycode.java.libraries.cannonball.DebugPrinter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private VersionedIvoryYamlConfiguration creaturesYAML;
    private VersionedIvoryYamlConfiguration identifyingYAML;
    private VersionedIvoryYamlConfiguration enchantmentTomesYAML;
    private IvorySettings settings;
    private ItemGroupManager itemGroupManager;
    private TierManager tierManager;
    private NameManager nameManager;
    private CustomItemManager customItemManager;
    private SocketGemManager socketGemManager;
    private CreatureModManager creatureModManager;
    private EnchantmentTomeManager enchantmentStoneManager;
    private AnticheatManager anticheatManager;

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
        creaturesYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "creatures.yml"),
                                                            getResource("creatures.yml"),
                                                            VersionedIvoryConfiguration.VersionUpdateType
                                                                    .BACKUP_AND_UPDATE);
        if (creaturesYAML.update()) {
            getLogger().info("Updating creatures.yml");
            debug("Updating creatures.yml");
        }
        identifyingYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "identifying.yml"),
                                                              getResource("identifying.yml"),
                                                              VersionedIvoryConfiguration.VersionUpdateType
                                                                      .BACKUP_AND_UPDATE);
        if (identifyingYAML.update()) {
            getLogger().info("Updating identifying.yml");
            debug("Updating identifying.yml");
        }
        enchantmentTomesYAML = new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "enchantmentTomes.yml"),
                                                                    getResource("enchantmentTomes.yml"),
                                                                    VersionedIvoryConfiguration.VersionUpdateType
                                                                            .BACKUP_AND_UPDATE);
        if (enchantmentTomesYAML.update()) {
            getLogger().info("Updating enchantmentTomes.yml");
            debug("Updating enchantmentTomes.yml");
        }

        settings = IvorySettings.loadFromFiles(corestatsYAML, languageYAML, configYAML, identifyingYAML);

        itemGroupManager = new LootItemGroupManager();
        tierManager = new LootTierManager();
        nameManager = new LootNameManager();
        customItemManager = new LootCustomItemManager();
        socketGemManager = new LootSocketGemManager();
        creatureModManager = new LootCreatureModManager();
        enchantmentStoneManager = new LootEnchantmentTomeManager();
        anticheatManager = new LootAnticheatManager();
    }

    @Override
    public void enable() {
        loadItemGroups();
        loadTiers();
        loadNames();
        loadCustomItems();
        loadSocketGems();
        loadEnchantmentStones();
        loadCreatureMods();
    }

    @Override
    public void postEnable() {
        CommandHandler handler = new CommandHandler(this);
        handler.registerCommands(new LootCommand(this));
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SocketsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingListener(), this);
        Bukkit.getPluginManager().registerEvents(new AnticheatListener(this), this);
        //Bukkit.getPluginManager().registerEvents(new LoginListener(this), this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                this, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGH,
                Packets.Server.SET_SLOT, Packets.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketID() == Packets.Server.SET_SLOT) {
                    addGlow(new ItemStack[] { event.getPacket().getItemModifier().read(0) });
                } else {
                    addGlow(event.getPacket().getItemArrayModifier().read(0));
                }
            }
        });
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
        anticheatManager = null;
        enchantmentStoneManager = null;
        creatureModManager = null;
        socketGemManager = null;
        customItemManager = null;
        nameManager = null;
        tierManager = null;
        itemGroupManager = null;
        settings = null;
        identifyingYAML = null;
        creaturesYAML = null;
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
        if (debugPrinter != null && (settings == null || settings.getBoolean("config.debug", false))) {
            debugPrinter.debug(level, messages);
        }
    }

    private void loadEnchantmentStones() {
        for (EnchantmentTome es : getEnchantmentStoneManager().getEnchantmentStones()) {
            getEnchantmentStoneManager().removeEnchantmentStone(es.getName());
        }
        Set<EnchantmentTome> stones = new HashSet<>();
        List<String> loadedStones = new ArrayList<>();
        for (String key : enchantmentTomesYAML.getKeys(false)) {
            if (!enchantmentTomesYAML.isConfigurationSection(key)) {
                continue;
            }
            ConfigurationSection cs = enchantmentTomesYAML.getConfigurationSection(key);
            EnchantmentTomeBuilder builder = getNewEnchantmentStoneBuilder(key);
            builder.withDescription(cs.getString("description"));
            builder.withWeight(cs.getDouble("weight"));
            builder.withDistanceWeight(cs.getDouble("distance-weight"));
            builder.withLore(cs.getStringList("lore"));
            builder.withMinStats(cs.getInt("min-stats"));
            builder.withMaxStats(cs.getInt("max-stats"));
            builder.withBroadcast(cs.getBoolean("broadcast"));
            List<ItemGroup> groups = new ArrayList<>();
            for (String groop : cs.getStringList("item-groups")) {
                ItemGroup g = itemGroupManager.getItemGroup(groop);
                if (g == null) {
                    continue;
                }
                groups.add(g);
            }
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            if (cs.isConfigurationSection("enchantments")) {
                ConfigurationSection enchCS = cs.getConfigurationSection("enchantments");
                for (String eKey : enchCS.getKeys(false)) {
                    Enchantment ench = StringConverter.toEnchantment(eKey);
                    if (ench == null) {
                        continue;
                    }
                    int i = enchCS.getInt(eKey);
                    enchantments.put(ench, i);
                }
            }
            builder.withEnchantments(enchantments);
            builder.withItemGroups(groups);
            EnchantmentTome stone = builder.build();
            stones.add(stone);
            loadedStones.add(stone.getName());
        }
        for (EnchantmentTome es : stones) {
            getEnchantmentStoneManager().addEnchantmentStone(es);
        }
        debug("Loaded enchantment stones: " + loadedStones.toString());
    }

    private void loadCreatureMods() {
        for (CreatureMod cm : getCreatureModManager().getCreatureMods()) {
            getCreatureModManager().removeCreatureMod(cm.getEntityType());
        }
        Set<CreatureMod> mods = new HashSet<>();
        List<String> loadedMods = new ArrayList<>();
        for (String key : creaturesYAML.getKeys(false)) {
            if (!creaturesYAML.isConfigurationSection(key)) {
                continue;
            }
            ConfigurationSection cs = creaturesYAML.getConfigurationSection(key);
            CreatureModBuilder builder = getNewCreatureModBuilder(EntityType.valueOf(key));
            if (cs.isConfigurationSection("custom-items")) {
                Map<CustomItem, Double> map = new HashMap<>();
                for (String k : cs.getConfigurationSection("custom-items").getKeys(false)) {
                    if (!cs.isConfigurationSection("custom-items." + k)) {
                        continue;
                    }
                    CustomItem ci = customItemManager.getCustomItem(k);
                    if (ci == null) {
                        continue;
                    }
                    map.put(ci, cs.getDouble("custom-items." + k));
                }
                builder.withCustomItemMults(map);
            }
            if (cs.isConfigurationSection("socket-gems")) {
                Map<SocketGem, Double> map = new HashMap<>();
                for (String k : cs.getConfigurationSection("socket-gems").getKeys(false)) {
                    if (!cs.isConfigurationSection("socket-gems." + k)) {
                        continue;
                    }
                    SocketGem sg = socketGemManager.getSocketGem(k);
                    if (sg == null) {
                        continue;
                    }
                    map.put(sg, cs.getDouble("socket-gems." + k));
                }
                builder.withSocketGemMults(map);
            }
            if (cs.isConfigurationSection("tiers")) {
                Map<Tier, Double> map = new HashMap<>();
                for (String k : cs.getConfigurationSection("tiers").getKeys(false)) {
                    if (!cs.isConfigurationSection("tiers." + k)) {
                        continue;
                    }
                    Tier t = tierManager.getTier(k);
                    if (t == null) {
                        continue;
                    }
                    map.put(t, cs.getDouble("tiers." + k));
                }
                builder.withTierMults(map);
            }
            if (cs.isConfigurationSection("enchantment-stone")) {
                Map<EnchantmentTome, Double> map = new HashMap<>();
                for (String k : cs.getConfigurationSection("enchantment-stones").getKeys(false)) {
                    if (!cs.isConfigurationSection("enchantment-stones." + k)) {
                        continue;
                    }
                    EnchantmentTome es = enchantmentStoneManager.getEnchantmentStone(k);
                    if (es == null) {
                        continue;
                    }
                    map.put(es, cs.getDouble("enchantment-stones." + k));
                }
                builder.withEnchantmentStoneMults(map);
            }
            CreatureMod mod = builder.build();
            mods.add(mod);
            loadedMods.add(mod.getEntityType().name());
        }
        for (CreatureMod cm : mods) {
            creatureModManager.addCreatureMod(cm);
        }
        debug("Loaded creature mods: " + loadedMods.toString());
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
            builder.withDistanceWeight(cs.getDouble("distance-weight"));
            List<SocketEffect> effects = new ArrayList<>();
            for (String eff : cs.getStringList("effects")) {
                effects.add(LootSocketPotionEffect.parseString(eff));
            }
            builder.withSocketEffects(effects);
            List<ItemGroup> groups = new ArrayList<>();
            for (String groop : cs.getStringList("item-groups")) {
                ItemGroup g = itemGroupManager.getItemGroup(groop);
                if (g == null) {
                    continue;
                }
                groups.add(g);
            }
            builder.withItemGroups(groups);
            builder.withBroadcast(cs.getBoolean("broadcast"));
            builder.withTriggerable(cs.getBoolean("triggerable"));
            builder.withTriggerText(cs.getString("trigger-text"));
            SocketGem gem = builder.build();
            gems.add(gem);
            loadedSocketGems.add(gem.getName());
        }
        for (SocketGem sg : gems) {
            getSocketGemManager().addSocketGem(sg);
        }
        debug("Loaded socket gems: " + loadedSocketGems.toString());
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
            builder.withWeight(cs.getDouble("weight"));
            builder.withDistanceWeight(cs.getDouble("distance-weight"));
            builder.withBroadcast(cs.getBoolean("broadcast"));
            CustomItem ci = builder.build();
            customItems.add(ci);
            loaded.add(ci.getName());
        }
        for (CustomItem ci : customItems) {
            getCustomItemManager().addCustomItem(ci);
        }
        debug("Loaded custom items: " + loaded.toString());
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
                    itemGroups.add(!ig.isInverse() ? ig.getInverse() : ig);
                } else {
                    ig = getItemGroupManager().getItemGroup(s);
                    if (ig == null) {
                        continue;
                    }
                    itemGroups.add(!ig.isInverse() ? ig : ig.getInverse());
                }
            }
            builder.withItemGroups(itemGroups);
            builder.withMinimumDurability(cs.getDouble("minimum-durability"));
            builder.withMaximumDurability(cs.getDouble("maximum-durability"));
            builder.withEnchantable(cs.getBoolean("enchantable"));
            builder.withBroadcast(cs.getBoolean("broadcast"));
            builder.withExtendableChance(cs.getDouble("extendable-chance"));
            Tier t = builder.build();
            loadedTiers.add(t.getName());
            tiers.add(t);
        }
        debug("Loaded tiers: " + loadedTiers.toString());
        for (Tier t : tiers) {
            getTierManager().addTier(t);
        }
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

    public CreatureModBuilder getNewCreatureModBuilder(EntityType entityType) {
        return new LootCreatureModBuilder(entityType);
    }

    public EnchantmentTomeBuilder getNewEnchantmentStoneBuilder(String name) {
        return new LootEnchantmentTomeBuilder(name);
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

    public CreatureModManager getCreatureModManager() {
        return creatureModManager;
    }

    public EnchantmentTomeManager getEnchantmentStoneManager() {
        return enchantmentStoneManager;
    }

    public AnticheatManager getAnticheatManager() {
        return anticheatManager;
    }

    private void addGlow(ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null) {
                // Only update those stacks that have our flag enchantment
                if (stack.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 32) {
                    NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
                    compound.put(NbtFactory.ofList("ench"));
                }
            }
        }
    }

}
