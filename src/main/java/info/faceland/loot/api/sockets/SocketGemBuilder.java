package info.faceland.loot.api.sockets;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.List;

public interface SocketGemBuilder {

    boolean isBuilt();

    SocketGem build();

    SocketGemBuilder withWeight(double d);

    SocketGemBuilder withPrefix(String s);

    SocketGemBuilder withSuffix(String s);

    SocketGemBuilder withLore(List<String> l);

    SocketGemBuilder withSocketEffects(List<SocketEffect> effects);

    SocketGemBuilder withItemGroup(ItemGroup itemGroup);

    SocketGemBuilder withDistanceWeight(double d);

    SocketGemBuilder withBroadcast(boolean b);

    SocketGemBuilder withTriggerable(boolean b);
}
