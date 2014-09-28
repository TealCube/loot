package info.faceland.loot.managers;

import info.faceland.loot.api.managers.SocketGemManager;
import info.faceland.loot.api.sockets.SocketGem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootSocketGemManager implements SocketGemManager {

    private Map<String, SocketGem> gemMap;

    public LootSocketGemManager() {
        this.gemMap = new HashMap<>();
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

}
