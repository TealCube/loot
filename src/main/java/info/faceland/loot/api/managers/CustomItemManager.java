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
package info.faceland.loot.api.managers;

import info.faceland.loot.api.items.CustomItem;

import java.util.Map;
import java.util.Set;

public interface CustomItemManager {

    Set<CustomItem> getCustomItems();

    CustomItem getCustomItem(String name);

    void addCustomItem(CustomItem ci);

    void removeCustomItem(String name);

    CustomItem getRandomCustomItem();

    CustomItem getRandomCustomItem(boolean withChance);

    CustomItem getRandomCustomItem(boolean withChance, double distance);

    CustomItem getRandomCustomItem(boolean withChance, double distance, Map<CustomItem, Double> map);

    double getTotalWeight();

}
