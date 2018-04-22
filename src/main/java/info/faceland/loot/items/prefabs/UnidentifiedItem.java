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
package info.faceland.loot.items.prefabs;

import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

public final class UnidentifiedItem extends HiltItemStack {

    public UnidentifiedItem(Material material, int level) {
        super(material);
        setName(ChatColor.LIGHT_PURPLE + "Unidentified Item");
        if (level > -1) {
            setLore(Arrays.asList(
                    ChatColor.WHITE + "Item Level: " + level,
                    ChatColor.GRAY + "Find an " + ChatColor.DARK_PURPLE + "Identity Tome" + ChatColor.GRAY + " to",
                    ChatColor.GRAY + "reveal this item's stats!"));
        } else {
            setLore(Arrays.asList(
                    ChatColor.GRAY + "Find an " + ChatColor.DARK_PURPLE + "Identity Tome" + ChatColor.GRAY + " to",
                    ChatColor.GRAY + "reveal this item's stats!"));
        }
    }

}
