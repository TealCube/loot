package info.faceland.loot.api.managers;

import info.faceland.loot.api.sockets.SocketGem;

import java.net.Socket;
import java.util.Set;

public interface SocketGemManager {

    Set<SocketGem> getSocketGems();

    SocketGem getSocketGem(String name);

    void addSocketGem(SocketGem gem);

    void removeSocketGem(String name);

    Socket getRandomSocketGem();

}
