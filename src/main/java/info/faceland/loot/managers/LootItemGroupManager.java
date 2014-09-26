package info.faceland.loot.managers;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.managers.ItemGroupManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootItemGroupManager implements ItemGroupManager {

    private final Map<String, ItemGroup> itemGroupMap;

    public LootItemGroupManager() {
        itemGroupMap = new HashMap<>();
    }

    @Override
    public Set<ItemGroup> getItemGroups() {
        return new HashSet<>(itemGroupMap.values());
    }

    @Override
    public void addItemGroup(ItemGroup itemGroup) {
        if (itemGroup != null) {
            itemGroupMap.put(itemGroup.getName().toLowerCase(), itemGroup);
        }
    }

    @Override
    public void removeItemGroup(String name) {
        if (name != null) {
            itemGroupMap.remove(name.toLowerCase());
        }
    }

    @Override
    public ItemGroup getItemGroup(String name) {
        if (name != null && itemGroupMap.containsKey(name.toLowerCase())) {
            return itemGroupMap.get(name.toLowerCase());
        }
        return null;
    }

}
