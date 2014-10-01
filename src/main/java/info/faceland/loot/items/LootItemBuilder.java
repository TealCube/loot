package info.faceland.loot.items;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;
import info.faceland.utils.TextUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class LootItemBuilder implements ItemBuilder {

    private final LootPlugin plugin;
    private boolean built = false;
    private Tier tier;
    private Material material;
    private ItemGenerationReason itemGenerationReason;
    private LootRandom random;

    public LootItemBuilder(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    private String randomFromSet(Set<ItemGroup> itemGroups) {
        ItemGroup[] array = itemGroups.toArray(new ItemGroup[itemGroups.size()]);
        return array[random.nextInt(array.length)].getName().toUpperCase();
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
            if (tier == null) {
                tier = plugin.getTierManager().getRandomTier(false);
            }
            Set<Material> set = tier.getAllowedMaterials();
            Material[] array = set.toArray(new Material[set.size()]);
            if (array.length == 0) {
                throw new RuntimeException("array length is 0 for tier: " + tier.getName());
            }
            material = array[random.nextInt(array.length)];
        }
        hiltItemStack = new HiltItemStack(material);
        hiltItemStack.setName(tier.getDisplayColor() + plugin.getNameManager().getRandomPrefix() + " " + plugin
                .getNameManager().getRandomSuffix() + tier.getIdentificationColor());
        List<String> lore = new ArrayList<>(tier.getBaseLore());
        lore.addAll(plugin.getSettings().getStringList("corestats." + material.name(),
                                                       new ArrayList<String>()));
        for (int i = 0; i < random.nextIntRange(tier.getMinimumBonusLore(), tier.getMaximumBonusLore()); i++) {
            lore.add(tier.getBonusLore().get(random.nextInt(tier.getBonusLore().size())));
        }
        if (tier.isEnchantable()) {
            lore.add("<blue>(Enchantable)");
        }
        for (int i = 0; i < random.nextIntRange(tier.getMinimumSockets(), tier.getMaximumSockets()); i++) {
            lore.add("<yellow>(Socket)");
        }
        hiltItemStack.setLore(TextUtils.color(lore));
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

}
