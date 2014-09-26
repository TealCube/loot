package info.faceland.loot.api.groups;

import org.bukkit.Material;

import java.util.Set;

public interface ItemGroup {

    Set<Material> getLegalMaterials();

    void addLegalMaterial(Material material);

    void removeLegalMaterial(Material material);

}
