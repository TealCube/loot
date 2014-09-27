package info.faceland.loot.sockets;

import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.ArrayList;
import java.util.List;

public final class LootSocketGem implements SocketGem {

    private final String name;
    private double weight;
    private String prefix;
    private String suffix;
    private List<String> lore;
    private List<SocketEffect> socketEffects;

    public LootSocketGem(String name) {
        this.name = name;
        this.lore = new ArrayList<>();
        this.socketEffects = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    @Override
    public List<SocketEffect> getSocketEffects() {
        return new ArrayList<>(socketEffects);
    }

    void setSocketEffects(List<SocketEffect> socketEffects) {
        this.socketEffects = socketEffects;
    }

    void setLore(List<String> lore) {
        this.lore = lore;
    }

    void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

}
