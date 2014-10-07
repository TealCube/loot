/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

package info.faceland.loot.enchantments;

import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.enchantments.EnchantmentStoneBuilder;
import info.faceland.loot.api.groups.ItemGroup;

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

    @Override
    public EnchantmentStoneBuilder withBroadcast(boolean b) {
        stone.setBroadcast(b);
        return this;
    }

    @Override
    public EnchantmentStoneBuilder withItemGroups(List<ItemGroup> l) {
        stone.setItemGroups(l);
        return this;
    }

    @Override
    public EnchantmentStoneBuilder withDescription(String s) {
        stone.setDescription(s);
        return this;
    }

}
