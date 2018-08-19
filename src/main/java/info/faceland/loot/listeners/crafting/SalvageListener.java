/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.listeners.crafting;

import static info.faceland.loot.utils.inventory.MaterialUtil.buildEssence;
import static info.faceland.loot.utils.inventory.MaterialUtil.getDigit;
import static info.faceland.loot.utils.inventory.MaterialUtil.getItemLevel;
import static info.faceland.loot.utils.inventory.MaterialUtil.getToolLevel;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.inventory.InventoryUtil;
import info.faceland.loot.utils.inventory.MaterialUtil;
import info.faceland.strife.util.PlayerDataUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public final class SalvageListener implements Listener {

  private final LootPlugin plugin;
  private LootRandom random;

  public SalvageListener(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.isCancelled() || !(event.getClickedInventory() instanceof PlayerInventory)) {
      return;
    }
    if (event.getClick() != ClickType.RIGHT) {
      return;
    }
    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
      return;
    }
    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR ||
        !(event.getWhoClicked() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getWhoClicked();
    HiltItemStack currentItem = new HiltItemStack(event.getCurrentItem());
    HiltItemStack cursor = new HiltItemStack(event.getCursor());
    if (cursor.getName() == null) {
      return;
    }
    if (!cursor.getName().endsWith("Craftsman's Tools")) {
      return;
    }
    int toolQuality = 1;
    if (cursor.hasItemMeta()) {
      toolQuality = (int) cursor.getLore().get(1).chars().filter(ch -> ch == '✪').count();
    }

    // NO GOING BACK NOW BOYOS
    event.setCancelled(true);

    if (event.getCurrentItem().getAmount() > 1) {
      MessageUtils
          .sendMessage(player, plugin.getSettings().getString("language.craft.big-stack", ""));
      return;
    }

    if (event.getCursor().getAmount() > 1) {
      MessageUtils
          .sendMessage(player, plugin.getSettings().getString("language.craft.big-cursor", ""));
      return;
    }

    int itemLevel = getItemLevel(currentItem);
    if (itemLevel == -1) {
      MessageUtils
          .sendMessage(player, plugin.getSettings().getString("language.craft.no-level", ""));
      return;
    }
    int craftingLevel = PlayerDataUtil.getCraftLevel(player);
    if (!isHighEnoughCraftingLevel(craftingLevel, itemLevel)) {
      MessageUtils
          .sendMessage(player, plugin.getSettings().getString("language.craft.low-level", ""));
      return;
    }
    if (craftingLevel < getToolLevel(cursor)) {
      MessageUtils
          .sendMessage(player, plugin.getSettings().getString("language.craft.low-level-tool", ""));
      return;
    }

    List<String> lore = currentItem.getLore();
    List<String> possibleStats = new ArrayList<>();
    for (String str : lore) {
      if (str.startsWith("" + ChatColor.GREEN) || str.startsWith("" + ChatColor.YELLOW)) {
        if (str.contains(":")) {
          continue;
        }
        possibleStats.add(str);
      }
    }

    List<Material> possibleMaterials = buildPossibleMaterials(currentItem);
    if (possibleMaterials.size() == 0) {
      MessageUtils
          .sendMessage(player, plugin.getSettings().getString("language.craft.no-materials", ""));
      return;
    }
    Material material = possibleMaterials.get(random.nextInt(possibleMaterials.size()));
    int quality = 1;
    while (random.nextDouble() <= plugin.getSettings()
        .getDouble("config.drops.material-quality-up", 0.1D) &&
        quality < 3) {
      quality++;
    }
    HiltItemStack craftMaterial = MaterialUtil.buildMaterial(
        material, plugin.getCraftMatManager().getCraftMaterials().get(material), itemLevel,
        quality);

    player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 0.8F);
    event.setCurrentItem(null);
    player.getInventory().addItem(craftMaterial);

    double levelSurplus = Math.max(0, (craftingLevel * 2) - itemLevel);
    double essChance = 0.1 + 0.1 * toolQuality + Math.min(levelSurplus * 0.01, 0.35);
    if (possibleStats.size() > 0 && random.nextDouble() < essChance) {
      player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.4F, 2F);
      String type = InventoryUtil.getItemType(currentItem);
      HiltItemStack shard = buildEssence(player, type, itemLevel, craftingLevel, possibleStats);
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(shard);
      } else {
        player.getWorld().dropItem(player.getLocation(), shard);
      }
    }
    List<String> toolLore = cursor.getLore();
    if (ChatColor.stripColor(toolLore.get(toolLore.size() - 1)).startsWith("Remaining Uses: ")) {
      int uses = getDigit(ChatColor.stripColor(toolLore.get(toolLore.size() - 1)));
      if (uses == 1) {
        MessageUtils
            .sendMessage(player, plugin.getSettings().getString("language.craft.tool-decay", ""));
        event.setCursor(null);
      } else {
        uses--;
        toolLore.set(toolLore.size() - 1, ChatColor.WHITE + "Remaining Uses: " + uses);
        cursor.setLore(toolLore);
        event.setCursor(cursor);
      }
    }
    double exp = 0.6 + (itemLevel * 0.25);
    plugin.getStrifePlugin().getCraftExperienceManager().addExperience(player, exp, false);
  }

  private List<Material> buildPossibleMaterials(ItemStack itemStack) {
    List<Material> possibleMaterials = new ArrayList<>();
    for (Recipe recipe : Bukkit.getServer().getRecipesFor(itemStack)) {
      if (recipe instanceof ShapedRecipe) {
        ShapedRecipe shaped = (ShapedRecipe) recipe;
        for (ItemStack i : shaped.getIngredientMap().values()) {
          if (i == null) {
            continue;
          }
          if (plugin.getCraftMatManager().getCraftMaterials().keySet().contains(i.getType())) {
            possibleMaterials.add(i.getType());
          }
        }
      } else if (recipe instanceof ShapelessRecipe) {
        ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
        for (ItemStack i : shapeless.getIngredientList()) {
          if (plugin.getCraftMatManager().getCraftMaterials().keySet().contains(i.getType())) {
            possibleMaterials.add(i.getType());
          }
        }
      }
    }
    return possibleMaterials;
  }

  private boolean isHighEnoughCraftingLevel(int craftLevel, int itemLevel) {
    int lvlBonus = 10 + (int) Math.floor((double) craftLevel / 3) * 5;
    return lvlBonus >= itemLevel;
  }
}
