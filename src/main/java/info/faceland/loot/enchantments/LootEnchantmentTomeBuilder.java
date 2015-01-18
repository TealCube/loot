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
package info.faceland.loot.enchantments;

import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.enchantments.EnchantmentTomeBuilder;
import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

public final class LootEnchantmentTomeBuilder implements EnchantmentTomeBuilder {

    private boolean built = false;
    private LootEnchantmentTome stone;

    public LootEnchantmentTomeBuilder(String name) {
        stone = new LootEnchantmentTome(name);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public EnchantmentTome build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        return stone;
    }

    @Override
    public EnchantmentTomeBuilder withLore(List<String> l) {
        stone.setLore(l);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withWeight(double d) {
        stone.setWeight(d);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withDistanceWeight(double d) {
        stone.setDistanceWeight(d);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withMinStats(int i) {
        stone.setMinStats(i);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withMaxStats(int i) {
        stone.setMaxStats(i);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withBroadcast(boolean b) {
        stone.setBroadcast(b);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withItemGroups(List<ItemGroup> l) {
        stone.setItemGroups(l);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withDescription(String s) {
        stone.setDescription(s);
        return this;
    }

    @Override
    public EnchantmentTomeBuilder withEnchantments(Map<Enchantment, Integer> map) {
        stone.setEnchantments(map);
        return this;
    }

}
