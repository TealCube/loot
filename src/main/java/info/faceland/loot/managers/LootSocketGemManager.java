package info.faceland.loot.managers;

import info.faceland.loot.api.managers.SocketGemManager;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.math.LootRandom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootSocketGemManager implements SocketGemManager {

    private final Map<String, SocketGem> gemMap;
    private final LootRandom random;

    public LootSocketGemManager() {
        this.gemMap = new HashMap<>();
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public Set<SocketGem> getSocketGems() {
        return new HashSet<>(gemMap.values());
    }

    @Override
    public SocketGem getSocketGem(String name) {
        if (gemMap.containsKey(name.toLowerCase())) {
            return gemMap.get(name.toLowerCase());
        }
        if (gemMap.containsKey(name.toLowerCase().replace(" ", "_"))) {
            return gemMap.get(name.toLowerCase().replace(" ", "_"));
        }
        return null;
    }

    @Override
    public void addSocketGem(SocketGem gem) {
        if (gem != null) {
            gemMap.put(gem.getName().toLowerCase(), gem);
        }
    }

    @Override
    public void removeSocketGem(String name) {
        if (name != null) {
            gemMap.remove(name.toLowerCase());
        }
    }

    @Override
    public SocketGem getRandomSocketGem() {
        return getRandomSocketGem(false);
    }

    @Override
    public SocketGem getRandomSocketGem(boolean withChance) {
        if (!withChance) {
            Set<SocketGem> gems = getSocketGems();
            SocketGem[] array = gems.toArray(new SocketGem[gems.size()]);
            return array[random.nextInt(array.length)];
        }
        double selectedWeight = random.nextDouble() * getTotalWeight();
        double currentWeight = 0D;
        Set<SocketGem> gems = getSocketGems();
        for (SocketGem sg : gems) {
            currentWeight += sg.getWeight();
            if (currentWeight >= selectedWeight) {
                return sg;
            }
        }
        return null;
    }

    @Override
    public double getTotalWeight() {
        double d = 0;
        for (SocketGem sg : getSocketGems()) {
            d += sg.getWeight();
        }
        return d;
    }

}
