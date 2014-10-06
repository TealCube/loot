package info.faceland.loot.creatures;

import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.creatures.CreatureModBuilder;
import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import org.bukkit.entity.EntityType;

import java.util.Map;

public final class LootCreatureModBuilder implements CreatureModBuilder {

    private final LootCreatureMod mod;
    private boolean built = false;

    public LootCreatureModBuilder(EntityType entityType) {
        this.mod = new LootCreatureMod(entityType);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public CreatureMod build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        return mod;
    }

    @Override
    public CreatureModBuilder withCustomItemMults(Map<CustomItem, Double> map) {
        mod.setCustomItemDoubleMap(map);
        return this;
    }

    @Override
    public CreatureModBuilder withSocketGemMults(Map<SocketGem, Double> map) {
        mod.setSocketGemDoubleMap(map);
        return this;
    }

    @Override
    public CreatureModBuilder withTierMults(Map<Tier, Double> map) {
        mod.setTierDoubleMap(map);
        return this;
    }

    @Override
    public CreatureModBuilder withEnchantmentStoneMults(Map<EnchantmentStone, Double> map) {
        mod.setEnchantmentStoneDoubleMap(map);
        return this;
    }

}
