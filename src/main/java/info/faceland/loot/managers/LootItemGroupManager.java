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

package info.faceland.loot.managers;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.managers.ItemGroupManager;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootItemGroupManager implements ItemGroupManager {

    private final Map<String, ItemGroup> itemGroupMap;

    public LootItemGroupManager() {
        itemGroupMap = new HashMap<>();
    }

    @Override
    public Set<ItemGroup> getItemGroups() {
        return new HashSet<>(itemGroupMap.values());
    }

    @Override
    public void addItemGroup(ItemGroup itemGroup) {
        if (itemGroup != null) {
            itemGroupMap.put(itemGroup.getName().toLowerCase(), itemGroup);
        }
    }

    @Override
    public void removeItemGroup(String name) {
        if (name != null) {
            itemGroupMap.remove(name.toLowerCase());
        }
    }

    @Override
    public ItemGroup getItemGroup(String name) {
        if (name != null && itemGroupMap.containsKey(name.toLowerCase())) {
            return itemGroupMap.get(name.toLowerCase());
        }
        return null;
    }

    @Override
    public Set<ItemGroup> getMatchingItemGroups(Material m) {
        Set<ItemGroup> groups = new HashSet<>();
        for (ItemGroup ig : getItemGroups()) {
            if (ig.hasMaterial(m)) {
                groups.add(ig);
            }
        }
        return groups;
    }

}
