package info.faceland.loot.groups;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class LootItemGroup implements ItemGroup {

    private final Set<Material> legalMaterials;

    public LootItemGroup(Set<Material> legalMaterials) {
        this.legalMaterials = new HashSet<>(legalMaterials);
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


}
