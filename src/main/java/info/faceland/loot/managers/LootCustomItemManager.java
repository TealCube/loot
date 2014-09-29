package info.faceland.loot.managers;

import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.managers.CustomItemManager;
import info.faceland.loot.math.LootRandom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootCustomItemManager implements CustomItemManager {

    private final Map<String, CustomItem> customItemMap;
    private final LootRandom random;

    public LootCustomItemManager() {
        customItemMap = new HashMap<>();
        random = new LootRandom(System.currentTimeMillis());
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

    @Override
    public CustomItem getRandomCustomItem() {
        return getRandomCustomItem(false);
    }

    @Override
    public CustomItem getRandomCustomItem(boolean withChance) {
        return getRandomCustomItem(withChance, 0D);
    }

    @Override
    public CustomItem getRandomCustomItem(boolean withChance, double distance) {
        return getRandomCustomItem(withChance, distance, new HashMap<CustomItem, Double>());
    }

    @Override
    public CustomItem getRandomCustomItem(boolean withChance, double distance, Map<CustomItem, Double> map) {
        if (!withChance) {
            Set<CustomItem> set = getCustomItems();
            CustomItem[] array = set.toArray(new CustomItem[set.size()]);
            return array[random.nextInt(array.length)];
        }
        double selectedWeight = random.nextDouble() * getTotalWeight();
        double currentWeight = 0D;
        for (CustomItem ci : getCustomItems()) {
            double calcWeight = ci.getWeight() + ((distance / 10000D) * ci.getDistanceWeight());
            if (map.containsKey(ci)) {
                calcWeight *= map.get(ci);
            }
            currentWeight += calcWeight;
            if (currentWeight >= selectedWeight) {
                return ci;
            }
        }
        return null;
    }

    @Override
    public double getTotalWeight() {
        double d = 0;
        for (CustomItem ci : getCustomItems()) {
            d += ci.getWeight();
        }
        return d;
    }

}
