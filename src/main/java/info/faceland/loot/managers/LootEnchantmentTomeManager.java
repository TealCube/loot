/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.managers;

import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.managers.EnchantmentTomeManager;
import info.faceland.loot.math.LootRandom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootEnchantmentTomeManager implements EnchantmentTomeManager {

    private static final double DISTANCE = 1000;
    private static final double DISTANCE_SQUARED = Math.pow(DISTANCE, 2);
    private final Map<String, EnchantmentTome> gemMap;
    private final LootRandom random;

    public LootEnchantmentTomeManager() {
        this.gemMap = new HashMap<>();
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public Set<EnchantmentTome> getEnchantmentStones() {
        return new HashSet<>(gemMap.values());
    }

    @Override
    public EnchantmentTome getEnchantmentStone(String name) {
        if (gemMap.containsKey(name.toLowerCase())) {
            return gemMap.get(name.toLowerCase());
        }
        if (gemMap.containsKey(name.toLowerCase().replace(" ", "_"))) {
            return gemMap.get(name.toLowerCase().replace(" ", "_"));
        }
        if (gemMap.containsKey(name.toLowerCase().replace("_", " "))) {
            return gemMap.get(name.toLowerCase().replace("_", " "));
        }
        return null;
    }

    @Override
    public void addEnchantmentStone(EnchantmentTome gem) {
        if (gem != null) {
            gemMap.put(gem.getName().toLowerCase(), gem);
        }
    }

    @Override
    public void removeEnchantmentStone(String name) {
        if (name != null) {
            gemMap.remove(name.toLowerCase());
        }
    }

    @Override
    public EnchantmentTome getRandomEnchantmentStone() {
        return getRandomEnchantmentStone(false);
    }

    @Override
    public EnchantmentTome getRandomEnchantmentStone(boolean withChance) {
        return getRandomEnchantmentStone(withChance, 0D);
    }

    @Override
    public EnchantmentTome getRandomEnchantmentStone(boolean withChance, double distance) {
        return getRandomEnchantmentStone(withChance, distance, new HashMap<EnchantmentTome, Double>());
    }

    @Override
    public EnchantmentTome getRandomEnchantmentStone(boolean withChance, double distance,
                                                      Map<EnchantmentTome, Double> map) {
        if (!withChance) {
            Set<EnchantmentTome> gems = getEnchantmentStones();
            EnchantmentTome[] array = gems.toArray(new EnchantmentTome[gems.size()]);
            return array[random.nextInt(array.length)];
        }
        double selectedWeight = random.nextDouble() * getTotalWeight();
        double currentWeight = 0D;
        Set<EnchantmentTome> gems = getEnchantmentStones();
        for (EnchantmentTome sg : gems) {
            double calcWeight = sg.getWeight() + ((distance / DISTANCE_SQUARED) * sg.getDistanceWeight());
            if (map.containsKey(sg)) {
                calcWeight *= map.get(sg);
            }
            currentWeight += calcWeight;
            if (currentWeight >= selectedWeight) {
                return sg;
            }
        }
        return null;
    }

    @Override
    public double getTotalWeight() {
        double d = 0;
        for (EnchantmentTome sg : getEnchantmentStones()) {
            d += sg.getWeight();
        }
        return d;
    }

}
