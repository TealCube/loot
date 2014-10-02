package info.faceland.loot.api.tier;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;

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

    TierBuilder withItemGroups(Set<ItemGroup> s);

    TierBuilder withMinimumDurability(double d);

    TierBuilder withMaximumDurability(double d);

    TierBuilder withDistanceWeight(double d);

    TierBuilder withEnchantable(boolean b);

    TierBuilder withBroadcast(boolean b);

    TierBuilder withExtendable(boolean b);
}
