package info.faceland.loot.api.managers;

import info.faceland.loot.api.creatures.CreatureMod;
import org.bukkit.entity.EntityType;

import java.util.Set;

public interface CreatureModManager {

    Set<CreatureMod> getCreatureMods();

    CreatureMod getCreatureMod(EntityType entityType);

    void addCreatureMod(CreatureMod mod);

    void removeCreatureMod(EntityType entityType);

}
