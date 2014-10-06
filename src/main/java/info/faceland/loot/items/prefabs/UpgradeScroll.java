package info.faceland.loot.items.prefabs;

import info.faceland.hilt.HiltItemStack;
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
        this.setLore(Arrays.asList(ChatColor.GRAY + "Drag this scroll onto an item with",
                                   ChatColor.GRAY + "stats to upgrade it. The item is",
                                   ChatColor.WHITE + "destroyed" + ChatColor.GRAY + " if the upgrade fails!",
                                   ChatColor.GREEN + "Success Chance: " + ChatColor.WHITE +
                                   DECIMAL_FORMAT.format(100D - (scrollType.getChanceToDestroy() * 100D)),
                                   ChatColor.YELLOW + "Level Range: " + ChatColor.WHITE + "+" +
                                   scrollType.getMinimumLevel() + " to +" + (scrollType.getMaximumLevel() + 1)));
    }

    public ScrollType getScrollType() {
        return scrollType;
    }

    public enum ScrollType {
        LESSER("Lesser", 0D, 0, 2, 70D), STANDARD("Standard", 0.5D, 3, 5, 25D), GREATER("Greater", 0.8D, 6, 8, 5D),
        ULTIMATE("Ultimate", 0D, 0, 8, 1D);

        private final String prettyName;
        private final double chanceToDestroy;
        private final int minimumLevel;
        private final int maximumLevel;
        private final double weight;

        private ScrollType(String prettyName, double chanceToDestroy, int minimumLevel, int maximumLevel,
                           double weight) {
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
