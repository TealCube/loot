package info.faceland.loot.api.enchantments;

import java.util.List;

public interface EnchantmentStone {

    String getName();

    List<String> getLore();

    double getWeight();

    double getDistanceWeight();

    int getMinStats();

    int getMaxStats();

}
