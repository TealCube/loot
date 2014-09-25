package info.faceland.loot.api.tier;

import org.bukkit.ChatColor;

import java.util.List;

public interface TierBuilder {

    boolean isBuilt();

    Tier build();

    TierBuilder withDisplayName(String s);

    TierBuilder withDisplayColor(ChatColor c);

    TierBuilder withIdentificationColor(ChatColor c);

    TierBuilder withSpawnWeight(double d);

    TierBuilder withIdentifyWeight(double d);

    TierBuilder withMinimumSockets(int i);

    TierBuilder withMaximumSockets(int i);

    TierBuilder withMinimumBonusLore(int i);

    TierBuilder withMaximumBonusLore(int i);

    TierBuilder withBaseLore(List<String> l);

    TierBuilder withBonusLore(List<String> l);

    TierBuilder withItemGroups(List<String> l);

    TierBuilder withMinimumDurability(double d);

    TierBuilder withMaximumDurability(double d);

    TierBuilder withOptimalSpawnDistance(double d);

    TierBuilder withMaximumRadiusFromOptimalSpawnDistance(double d);

}
