package info.faceland.loot.tier;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.api.tier.TierBuilder;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;

public final class LootTierBuilder implements TierBuilder {
    private boolean built = false;
    private LootTier tier;

    public LootTierBuilder(String name) {
        tier = new LootTier(name);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public Tier build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        return tier;
    }

    @Override
    public TierBuilder withDisplayName(String s) {
        tier.setDisplayName(s);
        return this;
    }

    @Override
    public TierBuilder withDisplayColor(ChatColor c) {
        tier.setDisplayColor(c);
        return this;
    }

    @Override
    public TierBuilder withIdentificationColor(ChatColor c) {
        tier.setIdentificationColor(c);
        return this;
    }

    @Override
    public TierBuilder withSpawnWeight(double d) {
        tier.setSpawnWeight(d);
        return this;
    }

    @Override
    public TierBuilder withIdentifyWeight(double d) {
        tier.setIdentifyWeight(d);
        return this;
    }

    @Override
    public TierBuilder withMinimumSockets(int i) {
        tier.setMinimumSockets(i);
        return this;
    }

    @Override
    public TierBuilder withMaximumSockets(int i) {
        tier.setMaximumSockets(i);
        return this;
    }

    @Override
    public TierBuilder withMinimumBonusLore(int i) {
        tier.setMinimumBonusLore(i);
        return this;
    }

    @Override
    public TierBuilder withMaximumBonusLore(int i) {
        tier.setMaximumBonusLore(i);
        return this;
    }

    @Override
    public TierBuilder withBaseLore(List<String> l) {
        tier.setBaseLore(l);
        return this;
    }

    @Override
    public TierBuilder withBonusLore(List<String> l) {
        tier.setBonusLore(l);
        return this;
    }

    @Override
    public TierBuilder withItemGroups(Set<ItemGroup> s) {
        tier.setItemGroups(s);
        return this;
    }

    @Override
    public TierBuilder withMinimumDurability(double d) {
        tier.setMinimumDurability(d);
        return this;
    }

    @Override
    public TierBuilder withMaximumDurability(double d) {
        tier.setMaximumDurability(d);
        return this;
    }

    @Override
    public TierBuilder withDistanceWeight(double d) {
        tier.setDistanceWeight(d);
        return this;
    }

    @Override
    public TierBuilder withEnchantable(boolean b) {
        tier.setEnchantable(b);
        return this;
    }

}
