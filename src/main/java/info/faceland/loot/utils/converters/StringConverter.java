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
