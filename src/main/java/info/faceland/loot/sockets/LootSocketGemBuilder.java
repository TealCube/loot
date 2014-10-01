package info.faceland.loot.sockets;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.SocketGemBuilder;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.List;

public final class LootSocketGemBuilder implements SocketGemBuilder {

    private boolean built = false;
    private LootSocketGem gem;

    public LootSocketGemBuilder(String name) {
        this.gem = new LootSocketGem(name);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public SocketGem build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        return gem;
    }

    @Override
    public SocketGemBuilder withWeight(double d) {
        gem.setWeight(d);
        return this;
    }

    @Override
    public SocketGemBuilder withPrefix(String s) {
        gem.setPrefix(s);
        return this;
    }

    @Override
    public SocketGemBuilder withSuffix(String s) {
        gem.setSuffix(s);
        return this;
    }

    @Override
    public SocketGemBuilder withLore(List<String> l) {
        gem.setLore(l);
        return this;
    }

    @Override
    public SocketGemBuilder withSocketEffects(List<SocketEffect> effects) {
        gem.setSocketEffects(effects);
        return this;
    }

    @Override
    public SocketGemBuilder withItemGroups(List<ItemGroup> itemGroups) {
        gem.setItemGroups(itemGroups);
        return this;
    }

    @Override
    public SocketGemBuilder withDistanceWeight(double d) {
        gem.setDistanceWeight(d);
        return this;
    }

    @Override
    public SocketGemBuilder withBroadcast(boolean b) {
        gem.setBroadcast(b);
        return this;
    }

    @Override
    public SocketGemBuilder withTriggerable(boolean b) {
        gem.setTriggerable(b);
        return this;
    }

}
