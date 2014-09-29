package info.faceland.loot.enchantments;

import info.faceland.loot.api.enchantments.EnchantmentStone;

import java.util.List;

public final class LootEnchantmentStone implements EnchantmentStone {

    private final String name;
    private List<String> lore;
    private double weight;
    private double distanceWeight;
    private int minStats;
    private int maxStats;

    public LootEnchantmentStone(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    void setLore(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public double getDistanceWeight() {
        return distanceWeight;
    }

    void setDistanceWeight(double distanceWeight) {
        this.distanceWeight = distanceWeight;
    }

    @Override
    public int getMinStats() {
        return minStats;
    }

    void setMinStats(int minStats) {
        this.minStats = minStats;
    }

    @Override
    public int getMaxStats() {
        return maxStats;
    }

    void setMaxStats(int maxStats) {
        this.maxStats = maxStats;
    }
    
}
