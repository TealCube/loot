package info.faceland.loot.managers;

import info.faceland.loot.api.managers.SocketGemManager;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.math.LootRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LootSocketGemManager implements SocketGemManager {

    private final Map<String, SocketGem> gemMap;
    private final LootRandom random;

    public LootSocketGemManager() {
        this.gemMap = new HashMap<>();
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public List<SocketGem> getSocketGems() {
        return new ArrayList<>(gemMap.values());
    }

    @Override
    public List<SocketGem> getSortedGems() {
        List<SocketGem> gems = getSocketGems();
        Collections.sort(gems);
        return gems;
    }

    @Override
    public SocketGem getSocketGem(String name) {
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
        return getRandomSocketGem(withChance, 0D);
    }

    @Override
    public SocketGem getRandomSocketGem(boolean withChance, double distance) {
        return getRandomSocketGem(withChance, distance, new HashMap<SocketGem, Double>());
    }

    @Override
    public SocketGem getRandomSocketGem(boolean withChance, double distance, Map<SocketGem, Double> map) {
        if (!withChance) {
            List<SocketGem> gems = getSocketGems();
            SocketGem[] array = gems.toArray(new SocketGem[gems.size()]);
            return array[random.nextInt(array.length)];
        }
        double selectedWeight = random.nextDouble() * getTotalWeight();
        double currentWeight = 0D;
        List<SocketGem> gems = getSocketGems();
        Collections.shuffle(gems);
        for (SocketGem sg : gems) {
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
        for (SocketGem sg : getSocketGems()) {
            d += sg.getWeight();
        }
        return d;
    }

}
