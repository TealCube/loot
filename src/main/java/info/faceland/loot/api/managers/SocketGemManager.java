package info.faceland.loot.api.managers;

import info.faceland.loot.api.sockets.SocketGem;

import java.util.Map;
import java.util.Set;

public interface SocketGemManager {

    Set<SocketGem> getSocketGems();

    SocketGem getSocketGem(String name);

    void addSocketGem(SocketGem gem);

    void removeSocketGem(String name);

    SocketGem getRandomSocketGem();

    SocketGem getRandomSocketGem(boolean withChance);

    SocketGem getRandomSocketGem(boolean withChance, Map<SocketGem, Double> map);

    double getTotalWeight();

}
