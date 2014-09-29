package info.faceland.loot.enchantments;

import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.enchantments.EnchantmentStoneBuilder;

import java.util.List;

public final class LootEnchantmentStoneBuilder implements EnchantmentStoneBuilder {

    private boolean built = false;
    private LootEnchantmentStone stone;

    public LootEnchantmentStoneBuilder(String name) {
        stone = new LootEnchantmentStone(name);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public EnchantmentStone build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        return stone;
    }

    @Override
    public EnchantmentStoneBuilder withLore(List<String> l) {
        stone.setLore(l);
        return this;
    }

    @Override
    public EnchantmentStoneBuilder withWeight(double d) {
        stone.setWeight(d);
        return this;
    }

    @Override
    public EnchantmentStoneBuilder withDistanceWeight(double d) {
        stone.setDistanceWeight(d);
        return this;
    }

    @Override
    public EnchantmentStoneBuilder withMinStats(int i) {
        stone.setMinStats(i);
        return this;
    }

    @Override
    public EnchantmentStoneBuilder withMaxStats(int i) {
        stone.setMaxStats(i);
        return this;
    }

}
