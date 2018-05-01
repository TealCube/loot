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
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.math.LootRandom;

import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
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
    private ItemRarity rarity;
    private int level;
    private Material material;
    private ItemGenerationReason itemGenerationReason = ItemGenerationReason.MONSTER;
    private LootRandom random;

    public LootItemBuilder(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom();
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
        if (material == null) {
            Set<Material> set = tier.getAllowedMaterials();
            Material[] array = set.toArray(new Material[set.size()]);
            if (array.length == 0) {
                throw new RuntimeException("array length is 0 for tier: " + tier.getName());
            }
            material = array[random.nextInt(array.length)];
        }
        hiltItemStack = new HiltItemStack(material);
        hiltItemStack.setName(rarity.getColor() + plugin.getNameManager().getRandomPrefix() + " " +
            plugin.getNameManager().getRandomSuffix());
        List<String> lore = new ArrayList<>();

        lore.add("&fLevel Requirement: " + level);
        lore.add("&fTier: " + rarity.getColor() + rarity.getName() + " " + tier.getName());

        lore.add(plugin.getStatManager().getFinalStat(tier.getPrimaryStat(), level, rarity.getPower()));
        lore.add(plugin.getStatManager().getFinalStat(
            tier.getSecondaryStats().get(random.nextInt(tier.getSecondaryStats().size())), level, rarity.getPower()));

        int bonusStats = random.nextIntRange(rarity.getMinimumBonusStats(), rarity.getMaximumBonusStats());
        List<ItemStat> bonusStatList = new ArrayList<>();
        bonusStatList.addAll(tier.getBonusStats());
        for (int i = 0; i < bonusStats; i++) {
            ItemStat stat = bonusStatList.get(random.nextInt(bonusStatList.size()));
            lore.add(plugin.getStatManager().getFinalStat(stat, level, rarity.getPower()));
            bonusStatList.remove(stat);
        }

        for (int i = 0; i < rarity.getEnchantments(); i++) {
            lore.add("&9(Enchantable)");
        }

        int sockets = random.nextIntRange(rarity.getMinimumSockets(), rarity.getMaximumSockets());
        for (int i = 0; i < sockets; i++) {
            lore.add("&6(Socket)");
        }

        for (int i = 0; i < rarity.getExtenderSlots(); i++) {
            lore.add("&3(+)");
        }

        hiltItemStack.setLore(TextUtils.color(lore));
        ItemMeta itemMeta = hiltItemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        hiltItemStack.setItemMeta(itemMeta);
        return hiltItemStack;
    }

    @Override
    public ItemBuilder withRarity(ItemRarity r) {
        rarity = r;
        return this;
    }

    @Override
    public ItemBuilder withLevel(int l) {
        level = l;
        return this;
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
        if (itemGenerationReason == ItemGenerationReason.IDENTIFYING) {
            this.tier = plugin.getTierManager().getRandomTier();
            double totalWeight = 0D;
            for (ItemRarity rarity : plugin.getRarityManager().getLoadedRarities().values()) {
                totalWeight += rarity.getIdWeight();
            }
            double chosenWeight = random.nextDouble() * totalWeight;
            double currentWeight = 0D;
            for (ItemRarity rarity : plugin.getRarityManager().getLoadedRarities().values()) {
                currentWeight += rarity.getIdWeight();
                if (currentWeight >= chosenWeight) {
                    this.rarity = rarity;
                }
            }
        }
        return this;
    }
}
