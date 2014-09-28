package info.faceland.loot.managers;

import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;
import org.bukkit.ChatColor;

import java.util.HashSet;
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
        if (!withChance) {
            Tier[] array = getLoadedTiers().toArray(new Tier[getLoadedTiers().size()]);
            return array[((int) (Math.random() * array.length))];
        }
        double selectedWeight = random.nextDouble() * getTotalTierWeight();
        double currentWeight = 0D;
        Set<Tier> chooseTiers = getLoadedTiers();
        for (Tier t : chooseTiers) {
            currentWeight += t.getSpawnWeight() + ((distance / 10000D) * t.getDistanceWeight());
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
    public double getTotalTierWeight() {
        double weight = 0;
        for (Tier t : getLoadedTiers()) {
            if (t != null) {
                weight += t.getSpawnWeight();
            }
        }
        return weight;
    }

}
