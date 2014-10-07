/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

package info.faceland.loot.managers;

import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.managers.EnchantmentStoneManager;
import info.faceland.loot.math.LootRandom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootEnchantmentStoneManager implements EnchantmentStoneManager {

    private final Map<String, EnchantmentStone> gemMap;
    private final LootRandom random;

    public LootEnchantmentStoneManager() {
        this.gemMap = new HashMap<>();
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public Set<EnchantmentStone> getEnchantmentStones() {
        return new HashSet<>(gemMap.values());
    }

    @Override
    public EnchantmentStone getEnchantmentStone(String name) {
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
    public void addEnchantmentStone(EnchantmentStone gem) {
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
    public EnchantmentStone getRandomEnchantmentStone() {
        return getRandomEnchantmentStone(false);
    }

    @Override
    public EnchantmentStone getRandomEnchantmentStone(boolean withChance) {
        return getRandomEnchantmentStone(withChance, 0D);
    }

    @Override
    public EnchantmentStone getRandomEnchantmentStone(boolean withChance, double distance) {
        return getRandomEnchantmentStone(withChance, distance, new HashMap<EnchantmentStone, Double>());
    }

    @Override
    public EnchantmentStone getRandomEnchantmentStone(boolean withChance, double distance,
                                                      Map<EnchantmentStone, Double> map) {
        if (!withChance) {
            Set<EnchantmentStone> gems = getEnchantmentStones();
            EnchantmentStone[] array = gems.toArray(new EnchantmentStone[gems.size()]);
            return array[random.nextInt(array.length)];
        }
        double selectedWeight = random.nextDouble() * getTotalWeight();
        double currentWeight = 0D;
        Set<EnchantmentStone> gems = getEnchantmentStones();
        for (EnchantmentStone sg : gems) {
            double calcWeight = sg.getWeight() + ((distance / 10000D) * sg.getDistanceWeight());
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
        for (EnchantmentStone sg : getEnchantmentStones()) {
            d += sg.getWeight();
        }
        return d;
    }

}
