package info.faceland.loot.api.creatures;

import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;

import java.util.Map;

public interface CreatureModBuilder {

    boolean isBuilt();

    CreatureMod build();

    CreatureModBuilder withCustomItemMults(Map<CustomItem, Double> map);

    CreatureModBuilder withSocketGemMults(Map<SocketGem, Double> map);

    CreatureModBuilder withTierMults(Map<Tier, Double> map);

}
