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
package info.faceland.loot.enchantments;

import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.nunnerycode.facecore.hilt.HiltItemStack;
import org.nunnerycode.facecore.utilities.TextUtils;
import org.nunnerycode.kern.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LootEnchantmentTome implements EnchantmentTome {

    private final String name;
    private List<String> lore;
    private double weight;
    private double distanceWeight;
    private int minStats;
    private int maxStats;
    private boolean broadcast;
    private List<ItemGroup> itemGroups;
    private String description;
    private Map<Enchantment, Integer> enchantments;

    public LootEnchantmentTome(String name) {
        this.name = name;
        this.lore = new ArrayList<>();
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

        LootEnchantmentTome that = (LootEnchantmentTome) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    void setLore(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public double getDistanceWeight() {
        return distanceWeight;
    }

    void setDistanceWeight(double distanceWeight) {
        this.distanceWeight = distanceWeight;
    }

    @Override
    public int getMinStats() {
        return minStats;
    }

    void setMinStats(int minStats) {
        this.minStats = minStats;
    }

    @Override
    public int getMaxStats() {
        return maxStats;
    }

    void setMaxStats(int maxStats) {
        this.maxStats = maxStats;
    }

    @Override
    public HiltItemStack toItemStack(int amount) {
        HiltItemStack is = new HiltItemStack(Material.BOOK);
        is.setAmount(amount);
        is.setName(ChatColor.BLUE + "Enchantment Tome - " + getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Type: " + (itemGroups.isEmpty() ? "Any" : itemGroupsToString()));
        lore.addAll(Arrays.asList(ChatColor.GRAY + "Drop this tome on an item that is",
                                  ChatColor.BLUE + "(Enchantable) " + ChatColor.GRAY + "while close to an",
                                  ChatColor.GRAY + "enchanting table to upgrade it!",
                                  ChatColor.WHITE + "Bonuses Applied:"));
        if (description != null && !description.isEmpty()) {
            lore.add(description);
        }
        is.setLore(TextUtils.color(lore));
        return is;
    }

    private String itemGroupsToString() {
        StringBuilder sb = new StringBuilder();
        for (ItemGroup ig : getItemGroups()) {
            sb.append(ig.getName()).append(" ");
        }
        return WordUtils.capitalizeFully(sb.toString().trim());
    }


    @Override
    public boolean isBroadcast() {
        return broadcast;
    }

    void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    @Override
    public List<ItemGroup> getItemGroups() {
        return new ArrayList<>(itemGroups);
    }

    void setItemGroups(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    @Override
    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return new HashMap<>(enchantments);
    }

    void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

}
