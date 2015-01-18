/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.loot.creatures;

import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.creatures.CreatureModBuilder;
import info.faceland.loot.api.enchantments.EnchantmentTome;
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
    public CreatureModBuilder withEnchantmentStoneMults(Map<EnchantmentTome, Double> map) {
        mod.setEnchantmentStoneDoubleMap(map);
        return this;
    }

}
