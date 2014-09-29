package info.faceland.loot.api.managers;

import info.faceland.loot.api.enchantments.EnchantmentStone;

import java.util.Map;
import java.util.Set;

public interface EnchantmentStoneManager {

    Set<EnchantmentStone> getEnchantmentStones();

    EnchantmentStone getEnchantmentStone(String name);

    void addEnchantmentStone(EnchantmentStone gem);

    void removeEnchantmentStone(String name);

    EnchantmentStone getRandomEnchantmentStone();

    EnchantmentStone getRandomEnchantmentStone(boolean withChance);

    EnchantmentStone getRandomEnchantmentStone(boolean withChance, double distance);

    EnchantmentStone getRandomEnchantmentStone(boolean withChance, double distance, Map<EnchantmentStone, Double> map);

    double getTotalWeight();
    
}
