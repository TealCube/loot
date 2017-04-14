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
package info.faceland.loot.items;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;

import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class LootItemBuilder implements ItemBuilder {

    private final LootPlugin plugin;
    private boolean built = false;
    private Tier tier;
    private Material material;
    private ItemGenerationReason itemGenerationReason = ItemGenerationReason.MONSTER;
    private LootRandom random;
    private double distance;

    public LootItemBuilder(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public HiltItemStack build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        HiltItemStack hiltItemStack;
        int attempts = 0;
        while (tier == null && attempts < 10) {
            tier = chooseTier();
            if (material != null && tier != null && !tier.getAllowedMaterials().contains(material)) {
                tier = null;
            }
            attempts++;
        }
        if (tier == null) {
            throw new IllegalStateException("tier is null");
        }
        if (material == null) {
            Set<Material> set = tier.getAllowedMaterials();
            Material[] array = set.toArray(new Material[set.size()]);
            if (array.length == 0) {
                throw new RuntimeException("array length is 0 for tier: " + tier.getName());
            }
            material = array[random.nextInt(array.length)];
        }
        hiltItemStack = new HiltItemStack(material);
        hiltItemStack.setName(tier.getDisplayColor() + plugin.getNameManager().getRandomPrefix() + " " + plugin
                .getNameManager().getRandomSuffix() + ChatColor.BLACK);
        List<String> lore = new ArrayList<>(tier.getBaseLore());
        lore.addAll(plugin.getSettings().getStringList("corestats." + material.name(),
                                                       new ArrayList<String>()));
        int bonusLore = random.nextIntRange(tier.getMinimumBonusLore(), tier.getMaximumBonusLore());
        for (int i = 0; i < bonusLore; i++) {
            lore.add(tier.getBonusLore().get(random.nextInt(tier.getBonusLore().size())));
        }
        if (tier.isEnchantable()) {
            lore.add("<blue>(Enchantable)");
        }
        int sockets = random.nextIntRange(tier.getMinimumSockets(), tier.getMaximumSockets());
        for (int i = 0; i < sockets; i++) {
            lore.add("<gold>(Socket)");
        }
        if (random.nextDouble() < tier.getExtendableChance()) {
            lore.add("<dark aqua>(+)");
        }
        hiltItemStack.setLore(TextUtils.color(lore));
        ItemMeta itemMeta = hiltItemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        hiltItemStack.setItemMeta(itemMeta);
        return hiltItemStack;
    }

    @Override
    public ItemBuilder withTier(Tier t) {
        tier = t;
        return this;
    }

    @Override
    public ItemBuilder withMaterial(Material m) {
        material = m;
        return this;
    }

    @Override
    public ItemBuilder withItemGenerationReason(ItemGenerationReason reason) {
        itemGenerationReason = reason;
        return this;
    }

    @Override
    public ItemBuilder withDistance(double d) {
        distance = d;
        return this;
    }

    private Tier chooseTier() {
        if (itemGenerationReason == ItemGenerationReason.IDENTIFYING) {
            double totalWeight = 0D;
            for (Tier t : plugin.getTierManager().getLoadedTiers()) {
                totalWeight += t.getIdentifyWeight();
            }
            double chosenWeight = random.nextDouble() * totalWeight;
            double currentWeight = 0D;
            for (Tier t : plugin.getTierManager().getLoadedTiers()) {
                currentWeight += t.getIdentifyWeight();
                if (currentWeight >= chosenWeight) {
                    return t;
                }
            }
            return null;
        }
        return plugin.getTierManager().getRandomTier(true, distance);
    }

}
