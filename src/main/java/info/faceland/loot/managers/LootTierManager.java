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

import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootTierManager implements TierManager {

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
    public Tier getTier(ChatColor displayColor, ChatColor identificationColor) {
        for (Tier t : getLoadedTiers()) {
            if (t.getDisplayColor() == displayColor && t.getIdentificationColor() == identificationColor) {
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
    public void removeTier(ChatColor displayColor, ChatColor identificationColor) {
        Tier t = getTier(displayColor, identificationColor);
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
            double calcWeight = t.getSpawnWeight() + ((distance / 10000D) * t.getDistanceWeight());
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

    @Override
    public Set<Tier> getLoadedTiers() {
        return new HashSet<>(loadedTiers);
    }

    @Override
    public double getTotalTierWeight(double distance) {
        double weight = 0;
        for (Tier t : getLoadedTiers()) {
            if (t != null) {
                double d = t.getSpawnWeight() + ((distance / 10000D) * t.getDistanceWeight());
                if (d > 0D) {
                    weight += d;
                }
            }
        }
        return weight;
    }

}
