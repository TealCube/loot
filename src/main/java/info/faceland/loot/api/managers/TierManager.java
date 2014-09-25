package info.faceland.loot.api.managers;

import info.faceland.loot.api.tier.Tier;
import org.bukkit.ChatColor;

import java.util.Set;

public interface TierManager {

    Tier getTier(String name);

    Tier getTier(ChatColor displayColor, ChatColor identificationColor);

    void addTier(Tier tier);

    void removeTier(String name);

    void removeTier(ChatColor displayColor, ChatColor identificationColor);

    Tier getRandomTier(boolean withChance);

    Set<Tier> getLoadedTiers();

    double getTotalTierWeight();

}
