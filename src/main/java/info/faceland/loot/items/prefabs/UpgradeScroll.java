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

import java.text.DecimalFormat;
import java.util.Arrays;

public final class UpgradeScroll extends HiltItemStack {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private final ScrollType scrollType;

    public UpgradeScroll(ScrollType scrollType) {
        super(Material.PAPER);
        this.scrollType = scrollType;
        this.setName(ChatColor.DARK_GREEN + scrollType.getPrettyName() + " Upgrade Scroll");
        this.setLore(Arrays.asList(ChatColor.GRAY + "Place this scroll onto an item with",
                                   ChatColor.GRAY + "stats to upgrade it. The item can be",
                                   ChatColor.WHITE + "destroyed" + ChatColor.GRAY + " if the upgrade fails!",
                                   ChatColor.GREEN + "Success Chance: " + ChatColor.WHITE +
                                   DECIMAL_FORMAT.format(100D - (scrollType.getChanceToDestroy() * 100D)) + "%",
                                   ChatColor.YELLOW + "Level Range: " + ChatColor.WHITE + "+" +
                                   scrollType.getMinimumLevel() + " -> +" + (scrollType.getMaximumLevel() + 1)));
    }

    public ScrollType getScrollType() {
        return scrollType;
    }

    public enum ScrollType {
        WEAK("Weak", 0.1D, 0, 2, 9000D),
        EMPOWERED("Empowered", 0.4D, 3, 5, 3000D),
        ARCANE("Arcane", 0.5D, 6, 8, 1000D),
        AWAKENED("Awakened", 0.6D, 9, 11, 300D),
        ANCIENT("Ancient", 0.7D, 12, 14, 100D),
        FLAWLESS("Flawless", 0.1D, 0, 14, 10D),
        LESSER("Lesser", 0D, 0, 2, 0D),
        STANDARD("Standard", 0.5D, 3, 5, 0D),
        STANDARDEST("Standardest", 0.35D, 3, 5, 0D),
        GREATER("Greater", 0.8D, 6, 8, 0D),
        GRAND("Grand", 0.7D, 6, 8, 0D),
        ULTIMATE("Ultimate", 0D, 0, 8, 0D);

        private final String prettyName;
        private final double chanceToDestroy;
        private final int minimumLevel;
        private final int maximumLevel;
        private final double weight;

        ScrollType(String prettyName, double chanceToDestroy, int minimumLevel, int maximumLevel, double weight) {
            this.prettyName = prettyName;
            this.chanceToDestroy = chanceToDestroy;
            this.minimumLevel = minimumLevel;
            this.maximumLevel = maximumLevel;
            this.weight = weight;
        }

        public static ScrollType getByName(String name) {
            for (ScrollType val : values()) {
                if (val.name().equals(name) || val.getPrettyName().equals(name)) {
                    return val;
                }
            }
            return null;
        }

        public String getPrettyName() {
            return prettyName;
        }

        public static ScrollType random(boolean withChance) {
            if (!withChance) {
                return values()[((int) (Math.random() * values().length))];
            }
            double selectedWeight = Math.random() * getTotalWeight();
            double currentWeight = 0D;
            for (ScrollType st : values()) {
                currentWeight += st.getWeight();
                if (currentWeight >= selectedWeight) {
                    return st;
                }
            }
            return null;
        }

        public static double getTotalWeight() {
            double d = 0D;
            for (ScrollType val : values()) {
                d += val.getWeight();
            }
            return d;
        }

        public double getWeight() {
            return weight;
        }

        public double getChanceToDestroy() {
            return chanceToDestroy;
        }

        public int getMinimumLevel() {
            return minimumLevel;
        }

        public int getMaximumLevel() {
            return maximumLevel;
        }

    }

}
