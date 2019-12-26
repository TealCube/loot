/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.creatures;

import info.faceland.loot.api.creatures.MobInfo;
import info.faceland.loot.api.creatures.CreatureModBuilder;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.JunkItemData;
import org.bukkit.entity.EntityType;

import java.util.Map;

public final class LootCreatureModBuilder implements CreatureModBuilder {

    private final LootMobInfo mod;
    private boolean built = false;

    public LootCreatureModBuilder(EntityType entityType) {
        this.mod = new LootMobInfo(entityType);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public MobInfo build() {
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
    public CreatureModBuilder withEnchantmentStoneMults(Map<EnchantmentTome, Double> map) {
        mod.setEnchantmentStoneDoubleMap(map);
        return this;
    }

    @Override
    public CreatureModBuilder withJunkMap(Map<String, Map<JunkItemData, Double>> map) {
        mod.setJunkItemDataDoubleMap(map);
        return this;
    }

}
