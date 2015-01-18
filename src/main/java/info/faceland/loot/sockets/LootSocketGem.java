/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.loot.sockets;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.nunnerycode.facecore.hilt.HiltItemStack;
import org.nunnerycode.facecore.utilities.TextUtils;
import org.nunnerycode.kern.apache.commons.lang3.text.WordUtils;

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
    private String triggerText;

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
    public int compareTo(SocketGem o) {
        if (o == null) {
            return 1;
        }
        int compareTo = getName().compareTo(o.getName());
        return Math.min(1, Math.max(compareTo, -1));
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
        return prefix != null ? prefix : "";
    }

    @Override
    public String getSuffix() {
        return suffix != null ? suffix : "";
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
        Collections.addAll(lore, ChatColor.WHITE + "Type: " + (!itemGroups.isEmpty() ? itemGroupsToString() : "Any"),
                           ChatColor.GRAY + "Drop this gem on an item with an",
                           ChatColor.GRAY + "open " + ChatColor.GOLD + "(Socket) " + ChatColor.GRAY + "to upgrade it!",
                           ChatColor.WHITE + "Bonuses Applied:");
        lore.addAll(getLore());
        itemStack.setLore(TextUtils.color(lore));
        return itemStack;
    }

    private String itemGroupsToString() {
        StringBuilder sb = new StringBuilder();
        for (ItemGroup ig : getItemGroups()) {
            sb.append(ig.getName()).append(" ");
        }
        return WordUtils.capitalizeFully(sb.toString().trim());
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

    void setTriggerable(boolean triggerable) {
        this.triggerable = triggerable;
    }

    void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    void setItemGroups(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
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

    @Override
    public String getTriggerText() {
        return triggerText;
    }

    void setTriggerText(String triggerText) {
        this.triggerText = triggerText;
    }

}
