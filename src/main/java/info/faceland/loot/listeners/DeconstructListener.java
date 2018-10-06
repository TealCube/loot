package info.faceland.loot.listeners;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;
import static info.faceland.loot.utils.inventory.MaterialUtil.buildEssence;
import static info.faceland.loot.utils.inventory.MaterialUtil.getDigit;
import static info.faceland.loot.utils.inventory.MaterialUtil.getItemLevel;
import static info.faceland.loot.utils.inventory.MaterialUtil.getToolLevel;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.events.LootDeconstructEvent;
import info.faceland.loot.events.LootDeconstructEvent.DeconstructType;
import info.faceland.loot.items.prefabs.ShardOfFailure;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.inventory.InventoryUtil;
import info.faceland.loot.utils.inventory.MaterialUtil;
import info.faceland.strife.util.PlayerDataUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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

public class DeconstructListener implements Listener {

  private final LootPlugin plugin;
  private LootRandom random;

  public DeconstructListener(LootPlugin plugin) {
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

    String curName = cursor.getName();
    if (StringUtils.isBlank(curName)) {
      return;
    }

    DeconstructType type;
    if (curName.endsWith("Craftsman's Tools")) {
      type = DeconstructType.CRAFTING;
    } else if (curName.endsWith("Enchanter's Arcana")) {
      type = DeconstructType.ENCHANTING;
    } else {
      return;
    }

    int itemLevel = getItemLevel(currentItem);
    if (itemLevel == -1) {
      MessageUtils.sendMessage(
          player, plugin.getSettings().getString("language.craft.no-level", ""));
      return;
    }

    // NO GOING BACK NOW BOYOS
    event.setCancelled(true);

    if (event.getCurrentItem().getAmount() > 1) {
      MessageUtils.sendMessage(
          player, plugin.getSettings().getString("language.craft.big-stack", ""));
      return;
    }

    if (event.getCursor().getAmount() > 1) {
      MessageUtils.sendMessage(
          player, plugin.getSettings().getString("language.craft.big-cursor", ""));
      return;
    }

    LootDeconstructEvent deconstructEvent = new LootDeconstructEvent();
    deconstructEvent.setDeconstructType(type);
    deconstructEvent.setCursorItem(cursor);
    deconstructEvent.setTargetItem(currentItem);
    deconstructEvent.setPlayer(player);
    Bukkit.getPluginManager().callEvent(deconstructEvent);

    if (deconstructEvent.isCancelled()) {
      return;
    }
    event.setCurrentItem(deconstructEvent.getTargetItem());
    event.setCursor(deconstructEvent.getCursorItem());
  }

  @EventHandler
  public void onDeconstruct(LootDeconstructEvent event) {
    if (event.getDeconstructType() == DeconstructType.CRAFTING) {
      doCraftDeconstruct(event);
    } else if (event.getDeconstructType() == DeconstructType.ENCHANTING) {
      doEnchantDeconstruct(event);
    }
  }

  private void doCraftDeconstruct(LootDeconstructEvent event) {
    Player player = event.getPlayer();
    HiltItemStack targetItem = event.getTargetItem();
    HiltItemStack cursorItem = event.getCursorItem();

    int itemLevel = getItemLevel(targetItem);

    int craftingLevel = PlayerDataUtil.getCraftLevel(player);
    int effectiveCraftLevel = PlayerDataUtil.getCraftSkill(player, true);

    int toolQuality = 1;
    if (cursorItem.hasItemMeta()) {
      toolQuality = (int) cursorItem.getLore().get(1).chars().filter(ch -> ch == '✪').count();
    }

    if (!isHighEnoughCraftingLevel(craftingLevel, itemLevel)) {
      sendMessage(player, plugin.getSettings().getString("language.craft.low-level", ""));
      return;
    }
    if (craftingLevel < getToolLevel(cursorItem)) {
      sendMessage(player, plugin.getSettings().getString("language.craft.low-level-tool", ""));
      return;
    }

    List<String> lore = targetItem.getLore();
    List<String> possibleStats = new ArrayList<>();
    for (String str : lore) {
      if (str.startsWith("" + ChatColor.GREEN) || str.startsWith("" + ChatColor.YELLOW)) {
        if (str.contains(":")) {
          continue;
        }
        possibleStats.add(str);
      }
    }

    List<Material> possibleMaterials = buildPossibleMaterials(targetItem);
    if (possibleMaterials.size() == 0) {
      sendMessage(player, plugin.getSettings().getString("language.craft.no-materials", ""));
      return;
    }
    Material material = possibleMaterials.get(random.nextInt(possibleMaterials.size()));
    int quality = 1;
    while (random.nextDouble() <= plugin.getSettings()
        .getDouble("config.drops.material-quality-up", 0.1D) && quality < 5) {
      quality++;
    }
    HiltItemStack craftMaterial = MaterialUtil.buildMaterial(material,
        plugin.getCraftMatManager().getCraftMaterials().get(material), itemLevel, quality);

    player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 0.8F);
    event.setTargetItem(null);
    player.getInventory().addItem(craftMaterial);

    double levelSurplus = Math.max(0, (effectiveCraftLevel * 2) - itemLevel);
    double essChance = 0.1 + 0.1 * toolQuality + Math.min(levelSurplus * 0.015, 0.35);
    if (possibleStats.size() > 0 && random.nextDouble() < essChance) {
      player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.4F, 2F);
      String type = InventoryUtil.getItemType(targetItem);
      HiltItemStack shard = buildEssence(player, type, itemLevel, craftingLevel, possibleStats);
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(shard);
      } else {
        player.getWorld().dropItem(player.getLocation(), shard);
      }
    }
    List<String> toolLore = cursorItem.getLore();
    if (ChatColor.stripColor(toolLore.get(toolLore.size() - 1)).startsWith("Remaining Uses: ")) {
      int uses = getDigit(ChatColor.stripColor(toolLore.get(toolLore.size() - 1)));
      if (uses == 1) {
        sendMessage(player, plugin.getSettings().getString("language.craft.tool-decay", ""));
        event.setCursorItem(null);
      } else {
        uses--;
        toolLore.set(toolLore.size() - 1, ChatColor.WHITE + "Remaining Uses: " + uses);
        cursorItem.setLore(toolLore);
        event.setCursorItem(cursorItem);
      }
    }
    double exp = 0.6 + (itemLevel * 0.25);
    plugin.getStrifePlugin().getCraftExperienceManager().addExperience(player, exp, false);
  }

  private void doEnchantDeconstruct(LootDeconstructEvent event) {
    Player player = event.getPlayer();
    HiltItemStack targetItem = event.getTargetItem();

    int itemPlus = MaterialUtil.getDigit(targetItem.getName());
    if (itemPlus == 0) {
      sendMessage(player, plugin.getSettings().getString("language.enchant.too-low-to-deconstruct", ""));
      event.setCancelled(true);
      return;
    }

    event.setTargetItem(null);
    player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1.5F);
    player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);

    double maxBonus = Math.pow(MaterialUtil.getDigit(targetItem.getName()), 1.5);
    ShardOfFailure shardOfFailure = new ShardOfFailure(player.getName());
    shardOfFailure.setAmount(1 + random.nextIntRange(0, Math.max((int)(maxBonus / 2) - 4, 1)));
    player.getInventory().addItem(shardOfFailure);
    plugin.getStrifePlugin().getEnchantExperienceManager().addExperience(player, maxBonus, false);
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