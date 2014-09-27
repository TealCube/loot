package info.faceland.loot.managers;

import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.managers.CustomItemManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootCustomItemManager implements CustomItemManager {

    private Map<String, CustomItem> customItemMap;

    public LootCustomItemManager() {
        customItemMap = new HashMap<>();
    }

    @Override
    public Set<CustomItem> getCustomItems() {
        return new HashSet<>(customItemMap.values());
    }

    @Override
    public CustomItem getCustomItem(String name) {
        if (customItemMap.containsKey(name.toLowerCase())) {
            return customItemMap.get(name.toLowerCase());
        }
        return null;
    }

    @Override
    public void addCustomItem(CustomItem ci) {
        customItemMap.put(ci.getName().toLowerCase(), ci);
    }

    @Override
    public void removeCustomItem(String name) {
        if (customItemMap.containsKey(name.toLowerCase())) {
            customItemMap.remove(name.toLowerCase());
        }
    }

}
