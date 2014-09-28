package info.faceland.loot.items;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.utils.TextUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class LootCustomItem implements CustomItem {

    private final String name;
    private String displayName;
    private List<String> lore;
    private Material material;
    private double weight;

    public LootCustomItem(String name) {
        this.name = name;
        this.lore = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public HiltItemStack toItemStack(int amount) {
        HiltItemStack itemStack = new HiltItemStack(this.material);
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        itemStack.setAmount(amount);
        itemStack.setName(TextUtils.color(this.displayName));
        itemStack.setLore(TextUtils.color(this.lore));
        return itemStack;
    }

    void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LootCustomItem that = (LootCustomItem) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
