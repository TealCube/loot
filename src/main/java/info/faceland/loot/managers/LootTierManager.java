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

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootTierManager implements TierManager {

    private static final double DISTANCE = 1000;
    private static final double DISTANCE_SQUARED = Math.pow(DISTANCE, 2);
    private Set<Tier> loadedTiers;
    private LootRandom random;

    public LootTierManager() {
        loadedTiers = new HashSet<>();
        random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public Tier getTier(String name) {
        for (Tier t : getLoadedTiers()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void addTier(Tier tier) {
        if (tier != null) {
            loadedTiers.add(tier);
        }
    }

    @Override
    public void removeTier(String name) {
        Tier t = getTier(name);
        if (t != null) {
            loadedTiers.remove(t);
        }
    }

    @Override
    public Tier getRandomTier() {
        return getRandomTier(false);
    }

    @Override
    public Tier getRandomTier(boolean withChance) {
        return getRandomTier(withChance, 0D);
    }

    @Override
    public Tier getRandomTier(boolean withChance, double distance) {
        return getRandomTier(withChance, distance, new HashMap<Tier, Double>());
    }

    @Override
    public Tier getRandomTier(boolean withChance, double distance, Map<Tier, Double> tierWeights) {
        if (!withChance) {
            Tier[] array = getLoadedTiers().toArray(new Tier[getLoadedTiers().size()]);
            return array[((int) (Math.random() * array.length))];
        }
        double selectedWeight = random.nextDouble() * getTotalTierWeight(distance);
        double currentWeight = 0D;
        Set<Tier> chooseTiers = getLoadedTiers();
        for (Tier t : chooseTiers) {
            double calcWeight = t.getSpawnWeight() + ((distance / DISTANCE_SQUARED) * t.getDistanceWeight());
            if (tierWeights.containsKey(t)) {
                calcWeight *= tierWeights.get(t);
            }
            if (calcWeight >= 0) {
                currentWeight += calcWeight;
            }
            if (currentWeight >= selectedWeight) {
                return t;
            }
        }
        return null;
    }

    public Tier getRandomLeveledTier(int level) {
        double selectedWeight = random.nextDouble() * getTotalLeveledTierWeight(level);
        double currentWeight = 0D;
        Set<Tier> chooseTiers = getLoadedTiers();
        for (Tier t : chooseTiers) {
            double diff = Math.abs(t.getLevelBase() - level);
            if (diff >= t.getLevelRange()) {
                continue;
            }
            double calcWeight = t.getSpawnWeight() * (1 - diff/t.getLevelRange());
            currentWeight += calcWeight;
            if (currentWeight >= selectedWeight) {
                return t;
            }
        }
        return null;
    }

    @Override
    public Set<Tier> getLoadedTiers() {
        return new HashSet<>(loadedTiers);
    }

    @Override
    public double getTotalTierWeight(double distance) {
        double weight = 0;
        for (Tier t : getLoadedTiers()) {
            double d = t.getSpawnWeight() + ((distance / DISTANCE_SQUARED) * t.getDistanceWeight());
            if (d > 0D) {
                weight += d;
            }
        }
        return weight;
    }

    public double getTotalLeveledTierWeight(int level) {
        double weight = 0;
        for (Tier t : getLoadedTiers()) {
            double diff = Math.abs(t.getLevelBase() - level);
            if (diff >= t.getLevelRange()) {
                continue;
            }
            double d = t.getSpawnWeight() * (1 - diff/t.getLevelRange());
            weight += d;
        }
        return weight;
    }

}
