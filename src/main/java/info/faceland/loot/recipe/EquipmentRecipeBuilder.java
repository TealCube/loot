package info.faceland.loot.recipe;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.LootPlugin;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentRecipeBuilder {

  public static final String INFUSE_NAME = ChatColor.AQUA + "Item Essence Infusion";
  private static List<Material> MATERIAL_LIST;
  private static List<String> INFUSE_LORE;
  private LootPlugin plugin;

  public EquipmentRecipeBuilder(LootPlugin plugin) {
    this.plugin = plugin;
    MATERIAL_LIST = setupMaterialList();
    INFUSE_LORE = setupInfusionItem();
  }

  public void setupAllRecipes() {
    for (Material m : MATERIAL_LIST) {
      for(int i=0; i<8; i++) {
        setupEssenceRecipe(m, i);
      }
    }
  }

  private void setupEssenceRecipe(Material material, int index) {
    ItemStack itemStack = new ItemStack(material);

    ItemStackExtensionsKt.setDisplayName(itemStack, INFUSE_NAME);
    ItemStackExtensionsKt.setLore(itemStack, INFUSE_LORE);
    ItemMeta meta = itemStack.getItemMeta();
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemStack.setItemMeta(meta);
    ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, material.toString()+"_"+index), itemStack);
    recipe.addIngredient(1, material).addIngredient(1 + index, Material.PRISMARINE_SHARD);
    try {
      plugin.getServer().addRecipe(recipe);
    } catch (Exception e) {
      // Spigot doesn't allow you to remove recipes... so just catching the error...
    }
  }

  private List<String> setupInfusionItem() {
    List<String> lore = new ArrayList<>();
    lore.add(TextUtils.color("&7Clicking this will put an essence"));
    lore.add(TextUtils.color("&7from a random craft slot into"));
    lore.add(TextUtils.color("&7an open stat slot on your item."));
    lore.add(TextUtils.color("&7"));
    lore.add(TextUtils.color("&eYou cannot add duplicate stat"));
    lore.add(TextUtils.color("&etypes to the same item."));
    lore.add(TextUtils.color("&7"));
    lore.add(TextUtils.color("&cAll essences will be consumed"));
    lore.add(TextUtils.color("&cregardless of the outcome."));
    return lore;
  }

  private static List<Material> setupMaterialList() {
    return Arrays.asList(
        Material.IRON_HELMET,
        Material.IRON_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS,
        Material.IRON_SWORD,
        Material.IRON_AXE,
        Material.IRON_HOE,

        Material.DIAMOND_HELMET,
        Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_LEGGINGS,
        Material.DIAMOND_BOOTS,
        Material.DIAMOND_SWORD,
        Material.DIAMOND_AXE,
        Material.DIAMOND_HOE,

        Material.GOLDEN_HELMET,
        Material.GOLDEN_CHESTPLATE,
        Material.GOLDEN_LEGGINGS,
        Material.GOLDEN_BOOTS,
        Material.GOLDEN_SWORD,
        Material.GOLDEN_AXE,
        Material.GOLDEN_HOE,

        Material.LEATHER_HELMET,
        Material.LEATHER_CHESTPLATE,
        Material.LEATHER_LEGGINGS,
        Material.LEATHER_BOOTS,

        Material.CHAINMAIL_HELMET,
        Material.CHAINMAIL_CHESTPLATE,
        Material.CHAINMAIL_LEGGINGS,
        Material.CHAINMAIL_BOOTS,

        Material.STONE_SWORD,
        Material.STONE_AXE,
        Material.STONE_HOE,

        Material.WOODEN_SWORD,
        Material.WOODEN_AXE,
        Material.WOODEN_HOE,

        Material.BOW,
        Material.ARROW,
        Material.BOOK,
        Material.SHIELD
    );
  }
}
