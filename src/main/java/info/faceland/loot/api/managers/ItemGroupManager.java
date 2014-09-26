package info.faceland.loot.api.managers;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.Material;

import java.util.Set;

public interface ItemGroupManager {

    Set<ItemGroup> getItemGroups();

    void addItemGroup(ItemGroup itemGroup);

    void removeItemGroup(String name);

    ItemGroup getItemGroup(String name);

    Set<ItemGroup> getMatchingItemGroups(Material m);

}
