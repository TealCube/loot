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
package info.faceland.loot.api.tier;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;

public interface TierBuilder {

    boolean isBuilt();

    Tier build();

    TierBuilder withDisplayName(String s);

    TierBuilder withDisplayColor(ChatColor c);

    TierBuilder withSpawnWeight(double d);

    TierBuilder withLevelBase(int i);

    TierBuilder withLevelRange(int i);

    TierBuilder withIdentifyWeight(double d);

    TierBuilder withMinimumSockets(int i);

    TierBuilder withMaximumSockets(int i);

    TierBuilder withMinimumBonusLore(int i);

    TierBuilder withMaximumBonusLore(int i);

    TierBuilder withBaseLore(List<String> l);

    TierBuilder withBonusLore(List<String> l);

    TierBuilder withItemGroups(Set<ItemGroup> s);

    TierBuilder withMinimumDurability(double d);

    TierBuilder withMaximumDurability(double d);

    TierBuilder withDistanceWeight(double d);

    TierBuilder withEnchantable(boolean b);

    TierBuilder withBroadcast(boolean b);

    TierBuilder withExtendableChance(double d);

}
