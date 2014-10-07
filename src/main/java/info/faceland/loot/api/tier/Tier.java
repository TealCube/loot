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

package info.faceland.loot.api.tier;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;

public interface Tier extends Comparable<Tier> {

    String getName();

    String getDisplayName();

    ChatColor getDisplayColor();

    ChatColor getIdentificationColor();

    double getMinimumDurability();

    double getMaximumDurability();

    double getSpawnWeight();

    double getIdentifyWeight();

    double getDistanceWeight();

    List<String> getBaseLore();

    List<String> getBonusLore();

    Set<ItemGroup> getItemGroups();

    int getMinimumSockets();

    int getMaximumSockets();

    int getMinimumBonusLore();

    int getMaximumBonusLore();

    Set<Material> getAllowedMaterials();

    boolean isEnchantable();

    boolean isBroadcast();

    double getExtendableChance();

}
