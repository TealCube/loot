package info.faceland.loot.api.tier;

import org.bukkit.ChatColor;

import java.util.List;

public interface Tier extends Comparable<Tier> {

    String getName();

    String getDisplayName();

    ChatColor getDisplayColor();

    ChatColor getIdentificationColor();

    double getMinimumDurability();

    double getMaximumDurability();

    double getSpawnWeight();

    double getOptimalSpawnDistance();

    double getMaximumRadiusFromOptimalSpawnDistance();

    double getIdentifyWeight();

    List<String> getBaseLore();

    List<String> getBonusLore();

    List<String> getItemGroups();

    int getMinimumSockets();

    int getMaximumSockets();

    int getMinimumBonusLore();

    int getMaximumBonusLore();

}
