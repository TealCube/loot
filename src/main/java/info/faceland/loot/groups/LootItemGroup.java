package info.faceland.loot.groups;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class LootItemGroup implements ItemGroup {

    private final String name;
    private final Set<Material> legalMaterials;

    public LootItemGroup(String name) {
        this.name = name;
        this.legalMaterials = new HashSet<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Material> getLegalMaterials() {
        return new HashSet<>(legalMaterials);
    }

    @Override
    public void addLegalMaterial(Material material) {
        legalMaterials.add(material);
    }

    @Override
    public void removeLegalMaterial(Material material) {
        legalMaterials.remove(material);
    }

    @Override
    public boolean isLegalMaterial(Material material) {
        return legalMaterials.contains(material);
    }

}
