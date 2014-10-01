package info.faceland.loot.sockets;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import info.faceland.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LootSocketGem implements SocketGem {

    private final String name;
    private double weight;
    private double distanceWeight;
    private String prefix;
    private String suffix;
    private List<String> lore;
    private List<SocketEffect> socketEffects;
    private List<ItemGroup> itemGroups;
    private boolean broadcast;
    private boolean triggerable;

    public LootSocketGem(String name) {
        this.name = name;
        this.lore = new ArrayList<>();
        this.socketEffects = new ArrayList<>();
        this.itemGroups = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LootSocketGem that = (LootSocketGem) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
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
    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    @Override
    public HiltItemStack toItemStack(int amount) {
        HiltItemStack itemStack = new HiltItemStack(Material.EMERALD);
        itemStack.setName(ChatColor.GOLD + "Socket Gem - " + getName());
        itemStack.setAmount(amount);
        List<String> lore = new ArrayList<>();
        Collections.addAll(lore, ChatColor.DARK_GRAY + "(" + ChatColor.GRAY
                                 + (!itemGroups.isEmpty() ? itemGroupsToString() :
                                    "ANY") + ChatColor.DARK_GRAY + ")",
                           ChatColor.GRAY + "Drop this gem on an item with an",
                           ChatColor.GRAY + "open " + ChatColor.GOLD + "(Socket) " + ChatColor.GRAY +
                           "to use it.");
        lore.addAll(getLore());
        itemStack.setLore(TextUtils.color(lore));
        return itemStack;
    }

    private String itemGroupsToString() {
        StringBuilder sb = new StringBuilder();
        for (ItemGroup ig :  getItemGroups()) {
            sb.append(ig.getName()).append(" ");
        }
        return sb.toString().trim();
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

    void setItemGroups(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    @Override
    public double getDistanceWeight() {
        return distanceWeight;
    }

    void setDistanceWeight(double distanceWeight) {
        this.distanceWeight = distanceWeight;
    }

    @Override
    public boolean isBroadcast() {
        return broadcast;
    }

    @Override
    public boolean isTriggerable() {
        return triggerable;
    }

    void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    @Override
    public int compareTo(SocketGem o) {
        if (o == null) {
            return 1;
        }
        int compareTo = getName().compareTo(o.getName());
        return Math.min(1, Math.max(compareTo, -1));
    }

    void setTriggerable(boolean triggerable) {
        this.triggerable = triggerable;
    }

}
