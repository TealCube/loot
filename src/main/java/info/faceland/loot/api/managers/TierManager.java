package info.faceland.loot.api.managers;

import info.faceland.loot.api.tier.Tier;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.Set;

public interface TierManager {

    Tier getTier(String name);

    Tier getTier(ChatColor displayColor, ChatColor identificationColor);

    void addTier(Tier tier);

    void removeTier(String name);

    void removeTier(ChatColor displayColor, ChatColor identificationColor);

    Tier getRandomTier();

    Tier getRandomTier(boolean withChance);

    Tier getRandomTier(boolean withChance, double distance);

    Tier getRandomTier(boolean withChance, double distance, Map<Tier, Double> tierWeights);

    Set<Tier> getLoadedTiers();

    double getTotalTierWeight();

}
