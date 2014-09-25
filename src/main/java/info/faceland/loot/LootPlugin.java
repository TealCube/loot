package info.faceland.loot;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.api.tier.TierBuilder;
import info.faceland.loot.managers.LootTierManager;
import info.faceland.loot.tier.LootTierBuilder;
import info.faceland.utils.TextUtils;
import net.nunnerycode.java.libraries.cannonball.DebugPrinter;
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
    private TierManager tierManager;

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

        tierManager = new LootTierManager();
    }

    @Override
    public void enable() {
        loadTiers();
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
            builder.withMinimumSockets(cs.getInt("minimum-sockets"));
            builder.withMaximumSockets(cs.getInt("maximum-sockets"));
            builder.withMinimumBonusLore(cs.getInt("minimum-bonus-lore"));
            builder.withMaximumBonusLore(cs.getInt("maximum-bonus-lore"));
            builder.withBaseLore(cs.getStringList("base-lore"));
            builder.withBonusLore(cs.getStringList("bonus-lore"));
            builder.withItemGroups(cs.getStringList("item-groups"));
            builder.withMinimumDurability(cs.getDouble("minimum-durability"));
            builder.withMaximumDurability(cs.getDouble("maximum-durability"));
            builder.withOptimalSpawnDistance(cs.getDouble("optimal-spawn-distance", -1));
            builder.withMaximumRadiusFromOptimalSpawnDistance(cs.getDouble("maximum-spawn-radius", -1));
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
        tierManager = null;
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

}
