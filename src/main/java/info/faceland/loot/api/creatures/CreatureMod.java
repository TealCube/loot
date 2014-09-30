package info.faceland.loot.api.creatures;

import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import org.bukkit.entity.EntityType;

import java.util.Map;

public interface CreatureMod {

    EntityType getEntityType();

    Map<CustomItem, Double> getCustomItemMults();

    Map<SocketGem, Double> getSocketGemMults();

    Map<Tier, Double> getTierMults();

    double getCustomItemMult(CustomItem ci);

    double getSocketGemMult(SocketGem sg);

    double getTierMult(Tier t);

    Map<EnchantmentStone, Double> getEnchantmentStoneDoubleMap();
}
