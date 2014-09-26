package info.faceland.loot;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.managers.ItemGroupManager;
import info.faceland.loot.api.managers.NameManager;
import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.api.tier.TierBuilder;
import info.faceland.loot.groups.LootItemGroup;
import info.faceland.loot.io.SmartTextFile;
import info.faceland.loot.managers.LootItemGroupManager;
import info.faceland.loot.managers.LootNameManager;
import info.faceland.loot.managers.LootTierManager;
import info.faceland.loot.tier.LootTierBuilder;
import info.faceland.loot.utils.converters.StringConverter;
import info.faceland.utils.TextUtils;
import net.nunnerycode.java.libraries.cannonball.DebugPrinter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

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
    private ItemGroupManager itemGroupManager;
    private TierManager tierManager;
    private NameManager nameManager;

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

        itemGroupManager = new LootItemGroupManager();
        tierManager = new LootTierManager();
        nameManager = new LootNameManager();
    }

    @Override
    public void enable() {
        loadItemGroups();
        loadTiers();
        loadNames();
    }

    @Override
    public void postEnable() {
        debug("v" + getDescription().getVersion() + " enabled");
    }

    @Override
    public void preDisable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void postDisable() {
        nameManager = null;
        tierManager = null;
        itemGroupManager = null;
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

    private void loadNames() {
        for (String s : getNameManager().getPrefixes()) {
            getNameManager().removePrefix(s);
        }
        for (String s : getNameManager().getSuffixes()) {
            getNameManager().removeSuffix(s);
        }

        SmartTextFile prefixFile = new SmartTextFile(new File(getDataFolder(), "prefix.txt"));
        SmartTextFile suffixFile = new SmartTextFile(new File(getDataFolder(), "suffix.txt"));

        for (String s : prefixFile.read()) {
            getNameManager().addPrefix(s);
        }
        for (String s : suffixFile.read()) {
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

    public TierManager getTierManager() {
        return tierManager;
    }

    public ItemGroupManager getItemGroupManager() {
        return itemGroupManager;
    }

    public NameManager getNameManager() {
        return nameManager;
    }

}
