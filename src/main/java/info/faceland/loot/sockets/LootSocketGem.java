package info.faceland.loot.sockets;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LootSocketGem implements SocketGem {

    private final String name;
    private double weight;
    private String prefix;
    private String suffix;
    private List<String> lore;
    private List<SocketEffect> socketEffects;
    private ItemGroup itemGroup;

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

    @Override
    public ItemGroup getItemGroup() {
        return itemGroup;

    }

    @Override
    public HiltItemStack toItemStack(int amount) {
        HiltItemStack itemStack = new HiltItemStack(Material.EMERALD);
        itemStack.setName(ChatColor.GOLD + "Socket Gem - " + getName());
        itemStack.setAmount(amount);
        itemStack.setLore(Arrays.asList(ChatColor.GRAY + "Drop this gem on an item with an",
                                        ChatColor.GRAY + "open " + ChatColor.GOLD + "(Socket) " + ChatColor.GRAY +
                                        "to use it.", ChatColor.DARK_GRAY + "(" + ChatColor.GRAY
                                                      + (itemGroup != null ? itemGroup.getName().toUpperCase() : "ANY")));
        return itemStack;
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

    void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

}
