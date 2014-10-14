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

package info.faceland.loot.api.enchantments;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

public interface EnchantmentTomeBuilder {

    boolean isBuilt();

    EnchantmentTome build();

    EnchantmentTomeBuilder withLore(List<String> l);

    EnchantmentTomeBuilder withWeight(double d);

    EnchantmentTomeBuilder withDistanceWeight(double d);

    EnchantmentTomeBuilder withMinStats(int i);

    EnchantmentTomeBuilder withMaxStats(int i);

    EnchantmentTomeBuilder withBroadcast(boolean b);

    EnchantmentTomeBuilder withItemGroups(List<ItemGroup> l);

    EnchantmentTomeBuilder withDescription(String s);

    EnchantmentTomeBuilder withEnchantments(Map<Enchantment, Integer> map);
}
