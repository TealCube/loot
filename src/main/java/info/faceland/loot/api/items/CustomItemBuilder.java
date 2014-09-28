package info.faceland.loot.api.items;

import org.bukkit.Material;

import java.util.List;

public interface CustomItemBuilder {

    boolean isBuilt();

    CustomItem build();

    CustomItemBuilder withDisplayName(String displayName);

    CustomItemBuilder withLore(List<String> lore);

    CustomItemBuilder withMaterial(Material material);

    CustomItemBuilder withWeight(double d);

}
