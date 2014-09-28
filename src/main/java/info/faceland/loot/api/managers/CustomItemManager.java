package info.faceland.loot.api.managers;

import info.faceland.loot.api.items.CustomItem;

import java.util.Map;
import java.util.Set;

public interface CustomItemManager {

    Set<CustomItem> getCustomItems();

    CustomItem getCustomItem(String name);

    void addCustomItem(CustomItem ci);

    void removeCustomItem(String name);

    CustomItem getRandomCustomItem();

    CustomItem getRandomCustomItem(boolean withChance);

    CustomItem getRandomCustomItem(boolean withChance, Map<CustomItem, Double> map);

    double getTotalWeight();

}
