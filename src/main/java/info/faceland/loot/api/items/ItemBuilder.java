package info.faceland.loot.api.items;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.api.tier.Tier;
import org.bukkit.Material;

public interface ItemBuilder {

    boolean isBuilt();

    HiltItemStack build();

    ItemBuilder withTier(Tier t);

    ItemBuilder withMaterial(Material m);

    ItemBuilder withItemGenerationReason(ItemGenerationReason reason);

    ItemBuilder withDistance(double d);
}
