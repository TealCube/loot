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

package info.faceland.loot.utils.converters;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public final class StringConverter {

    private StringConverter() {
        // do nothing
    }

    public static int toInt(String s) {
        return toInt(s, 0);
    }

    public static int toInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static double toDouble(String s) {
        return toDouble(s, 0D);
    }

    public static double toDouble(String s, double def) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static Material toMaterial(String s) {
        return toMaterial(s, Material.AIR);
    }

    public static Material toMaterial(String s, Material def) {
        try {
            return Material.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static Enchantment toEnchantment(String s) {
        return toEnchantment(s, Enchantment.ARROW_DAMAGE);
    }

    public static Enchantment toEnchantment(String s, Enchantment def) {
        Enchantment e = Enchantment.getByName(s);
        if (e == null) {
            return def;
        }
        return e;
    }

}
