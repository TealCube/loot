/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.items;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.managers.NameManager;
import info.faceland.loot.api.managers.RarityManager;
import info.faceland.loot.api.managers.StatManager;
import info.faceland.loot.api.managers.TierManager;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.math.LootRandom;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class LootItemBuilder implements ItemBuilder {

    private final TierManager tierManager;
    private final StatManager statManager;
    private final RarityManager rarityManager;
    private final NameManager nameManager;

    private boolean built = false;
    private boolean specialStat;
    private Tier tier;
    private ItemRarity rarity;
    private int level;
    private Material material;
    private ItemGenerationReason itemGenerationReason = ItemGenerationReason.MONSTER;
    private LootRandom random;

    private final double specialStatChance;

    public LootItemBuilder(LootPlugin plugin) {
        this.tierManager = plugin.getTierManager();
        this.statManager = plugin.getStatManager();
        this.rarityManager = plugin.getRarityManager();
        this.nameManager = plugin.getNameManager();
        this.random = new LootRandom();

        this.specialStatChance = plugin.getSettings()
                .getDouble("config.special-stats.pool-chance", 0.5D);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public ItemStack build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        ItemStack hiltItemStack;
        if (material == null) {
            Set<Material> set = tier.getAllowedMaterials();
            Material[] array = set.toArray(new Material[set.size()]);
            if (set.size() == 0) {
                throw new RuntimeException("array length is 0 for tier: " + tier.getName());
            }
            material = array[random.nextInt(array.length)];
        }
        hiltItemStack = new ItemStack(material);
        ItemStackExtensionsKt.setDisplayName(hiltItemStack, rarity.getColor() + nameManager.getRandomPrefix() + " " + nameManager.getRandomSuffix());
        List<String> lore = new ArrayList<>();

        lore.add("&fLevel Requirement: " + level);
        lore.add("&fTier: " + rarity.getColor() + rarity.getName() + " " + tier.getName());

        lore.add(statManager.getFinalStat(tier.getPrimaryStat(), level, rarity.getPower()));
        lore.add(statManager.getFinalStat(getRandomSecondaryStat(), level, rarity.getPower()));

        List<ItemStat> bonusStatList = new ArrayList<>(tier.getBonusStats());

        if (specialStat) {
            ItemStat stat;
            if (tier.getSpecialStats().size() > 0 && random.nextDouble() < specialStatChance) {
                stat = getRandomSpecialStat();
            } else {
                stat = bonusStatList.get(random.nextInt(bonusStatList.size()));
            }
            lore.add(statManager.getFinalStat(stat, level, rarity.getPower(), true));
        }

        int bonusStats = random
                .nextIntRange(rarity.getMinimumBonusStats(), rarity.getMaximumBonusStats());
        for (int i = 0; i < bonusStats; i++) {
            ItemStat stat = bonusStatList.get(random.nextInt(bonusStatList.size()));
            lore.add(statManager.getFinalStat(stat, level, rarity.getPower()));
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

        ItemStackExtensionsKt.setLore(hiltItemStack, TextUtils.color(lore));
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
    public ItemBuilder withSpecialStat(boolean b) {
        specialStat = b;
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
            double totalWeight = 0D;
            for (ItemRarity rarity : rarityManager.getLoadedRarities().values()) {
                totalWeight += rarity.getIdWeight();
            }
            double chosenWeight = random.nextDouble() * totalWeight;
            double currentWeight = 0D;
            for (ItemRarity rarity : rarityManager.getLoadedRarities().values()) {
                currentWeight += rarity.getIdWeight();
                if (currentWeight >= chosenWeight) {
                    this.rarity = rarity;
                }
            }
        }
        return this;
    }

    private ItemStat getRandomSecondaryStat() {
        return tier.getSecondaryStats().get(random.nextInt(tier.getSecondaryStats().size()));
    }

    private ItemStat getRandomSpecialStat() {
        return tier.getSpecialStats().get(random.nextInt(tier.getSpecialStats().size()));
    }
}
