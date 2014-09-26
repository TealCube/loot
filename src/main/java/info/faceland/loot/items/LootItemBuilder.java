package info.faceland.loot.items;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;
import info.faceland.utils.TextUtils;
import org.apache.commons.lang.StringUtils;
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

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public HiltItemStack build() {
        HiltItemStack hiltItemStack;
        if (material == null) {
            if (tier == null) {
                tier = plugin.getTierManager().getRandomTier(false);
            }
            Set<Material> set = tier.getAllowedMaterials();
            Material[] array = set.toArray(new Material[set.size()]);
            material = array[random.nextInt(array.length)];
        }
        hiltItemStack = new HiltItemStack(material);
        hiltItemStack.setName(tier.getDisplayColor() + plugin.getNameManager().getRandomPrefix() + " " + plugin
                .getNameManager().getRandomSuffix() + tier.getIdentificationColor());
        List<String> lore = new ArrayList<>(plugin.getSettings().getStringList("corestats." + material.name(),
                                                                               new ArrayList<String>()));
        if (tier.isEnchantable() || lore.size() > 0) {
            lore.add(TextUtils.color("<blue>(Enchantable)"));
        }
        for (int i = 0; i < random.nextInt(tier.getMaximumSockets() - tier.getMinimumSockets()) + tier
                .getMinimumSockets(); i++) {
            lore.add(TextUtils.color("<gold>(Socket)"));
        }
        lore.add(
                TextUtils.color("<dark gray>(<gray>" + randomFromSet(plugin.getItemGroupManager()
                                                                           .getMatchingItemGroups(material))) +
                "<dark gray>)");
        hiltItemStack.setLore(lore);
        built = true;
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

    private String randomFromSet(Set<ItemGroup> itemGroups) {
        ItemGroup[] array = itemGroups.toArray(new ItemGroup[itemGroups.size()]);
        return StringUtils.capitalize(array[random.nextInt(array.length)].getName());
    }

}
