package info.faceland.loot.api.groups;

import org.bukkit.Material;

import java.util.Set;

public interface ItemGroup {

    String getName();

    Set<Material> getMaterials();

    void addMaterial(Material material);

    void removeMaterial(Material material);

    boolean hasMaterial(Material material);

    boolean isInverse();

    ItemGroup getInverse();

}
