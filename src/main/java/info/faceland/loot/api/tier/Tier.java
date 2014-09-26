package info.faceland.loot.api.tier;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;

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

    Set<ItemGroup> getItemGroups();

    int getMinimumSockets();

    int getMaximumSockets();

    int getMinimumBonusLore();

    int getMaximumBonusLore();

    Set<Material> getAllowedMaterials();

}
