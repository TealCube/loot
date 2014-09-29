package info.faceland.loot.items;

import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.CustomItemBuilder;
import org.bukkit.Material;

import java.util.List;

public final class LootCustomItemBuilder implements CustomItemBuilder {

    private boolean built = false;
    private LootCustomItem customItem;

    public LootCustomItemBuilder(String name) {
        this.customItem = new LootCustomItem(name);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public CustomItem build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        this.built = true;
        return customItem;
    }

    @Override
    public CustomItemBuilder withDisplayName(String displayName) {
        customItem.setDisplayName(displayName);
        return this;
    }

    @Override
    public CustomItemBuilder withLore(List<String> lore) {
        customItem.setLore(lore);
        return this;
    }

    @Override
    public CustomItemBuilder withMaterial(Material material) {
        customItem.setMaterial(material);
        return this;
    }

    @Override
    public CustomItemBuilder withWeight(double d) {
        customItem.setWeight(d);
        return this;
    }

    @Override
    public CustomItemBuilder withDistanceWeight(double d) {
        customItem.setDistanceWeight(d);
        return this;
    }

    @Override
    public CustomItemBuilder withBroadcast(boolean b) {
        customItem.setBroadcast(b);
        return this;
    }

}
