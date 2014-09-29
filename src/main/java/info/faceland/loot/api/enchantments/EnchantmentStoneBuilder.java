package info.faceland.loot.api.enchantments;

import java.util.List;

public interface EnchantmentStoneBuilder {

    boolean isBuilt();

    EnchantmentStone build();

    EnchantmentStoneBuilder withLore(List<String> l);

    EnchantmentStoneBuilder withWeight(double d);

    EnchantmentStoneBuilder withDistanceWeight(double d);

    EnchantmentStoneBuilder withMinStats(int i);

    EnchantmentStoneBuilder withMaxStats(int i);

}
