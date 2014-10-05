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
        this.setLore(Arrays.asList(ChatColor.WHITE + "Drop this on an item to upgrade it.",
                                   ChatColor.YELLOW + "Chance for item to be destroyed: " + ChatColor.WHITE +
                                   DECIMAL_FORMAT.format(scrollType.getChanceToDestroy() * 100D),
                                   ChatColor.YELLOW + "Can be used on items with the levels: " + ChatColor.WHITE +
                                   scrollType.getMinimumLevel() + " - " + scrollType.getMaximumLevel()));
    }

    public ScrollType getScrollType() {
        return scrollType;
    }

    public enum ScrollType {
        LESSER("Lesser", 0D, 0, 3), ARCANE("Arcane", 0.3D, 4, 7);

        private final String prettyName;
        private final double chanceToDestroy;
        private final int minimumLevel;
        private final int maximumLevel;

        private ScrollType(String prettyName, double chanceToDestroy, int minimumLevel, int maximumLevel) {
            this.prettyName = prettyName;
            this.chanceToDestroy = chanceToDestroy;
            this.minimumLevel = minimumLevel;
            this.maximumLevel = maximumLevel;
        }

        public String getPrettyName() {
            return prettyName;
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

        public static ScrollType getByName(String name) {
            for (ScrollType val : values()) {
                if (val.name().equals(name) || val.getPrettyName().equals(name)) {
                    return val;
                }

            }
            return null;
        }
    }

}
