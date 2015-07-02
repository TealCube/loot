/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.enchantments;

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.kern.apache.commons.lang3.text.WordUtils;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

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
        lore.addAll(Arrays.asList(ChatColor.GRAY + "Place this tome on an item that is",
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
