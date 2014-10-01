package info.faceland.loot.api.sockets;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.List;

public interface SocketGem extends Comparable<SocketGem> {

    String getName();

    double getWeight();

    String getPrefix();

    String getSuffix();

    List<String> getLore();

    List<SocketEffect> getSocketEffects();

    List<ItemGroup> getItemGroups();

    HiltItemStack toItemStack(int amount);

    double getDistanceWeight();

    boolean isBroadcast();

    boolean isTriggerable();

}
