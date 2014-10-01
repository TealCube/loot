package info.faceland.loot.api.managers;

import info.faceland.loot.api.sockets.SocketGem;

import java.util.List;
import java.util.Map;

public interface SocketGemManager {

    List<SocketGem> getSocketGems();

    List<String> getGemNames();

    List<SocketGem> getSortedGems();

    List<String> getSortedGemNames();

    SocketGem getSocketGem(String name);

    void addSocketGem(SocketGem gem);

    void removeSocketGem(String name);

    SocketGem getRandomSocketGem();

    SocketGem getRandomSocketGem(boolean withChance);

    SocketGem getRandomSocketGem(boolean withChance, double distance);

    SocketGem getRandomSocketGem(boolean withChance, double distance, Map<SocketGem, Double> map);

    double getTotalWeight();

}
