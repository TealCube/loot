package info.faceland.loot.events;

import info.faceland.loot.api.events.LootEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LootDetermineChanceEvent extends LootEvent {

    private final LivingEntity entity;
    private final Player killer;
    private double chance;

    public LootDetermineChanceEvent(LivingEntity entity, Player killer, double chance) {
        this.entity = entity;
        this.killer = killer;
        this.chance = chance;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Player getKiller() {
        return killer;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

}
