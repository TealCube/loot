package info.faceland.loot.managers;

import info.faceland.loot.api.managers.NameManager;
import info.faceland.loot.math.LootRandom;

import java.util.ArrayList;
import java.util.List;

public final class LootNameManager implements NameManager {

    private final List<String> prefixes;
    private final List<String> suffixes;
    private final LootRandom random;

    public LootNameManager() {
        prefixes = new ArrayList<>();
        suffixes = new ArrayList<>();
        random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public List<String> getPrefixes() {
        return new ArrayList<>(prefixes);
    }

    @Override
    public List<String> getSuffixes() {
        return new ArrayList<>(suffixes);
    }

    @Override
    public String getRandomPrefix() {
        return getPrefixes().get(random.nextInt(getPrefixes().size()));
    }

    @Override
    public String getRandomSuffix() {
        return getSuffixes().get(random.nextInt(getSuffixes().size()));
    }

    @Override
    public void addPrefix(String s) {
        if (s != null) {
            prefixes.add(s);
        }
    }

    @Override
    public void addSuffix(String s) {
        if (s != null) {
            suffixes.add(s);
        }
    }

    @Override
    public void removePrefix(String s) {
        if (s != null) {
            prefixes.remove(s);
        }
    }

    @Override
    public void removeSuffix(String s) {
        if (s != null) {
            suffixes.remove(s);
        }
    }

}
