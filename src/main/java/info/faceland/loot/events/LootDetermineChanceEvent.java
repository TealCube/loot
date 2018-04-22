/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.events;

import info.faceland.loot.api.events.LootEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LootDetermineChanceEvent extends LootEvent {

    private final LivingEntity entity;
    private final Player killer;
    private double quantityBonus;
    private double rarityBonus;

    public LootDetermineChanceEvent(LivingEntity entity, Player killer) {
        this.entity = entity;
        this.killer = killer;
        this.quantityBonus = 0;
        this.rarityBonus = 0;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Player getKiller() {
        return killer;
    }

    public double getQuantityBonus() {
        return quantityBonus;
    }

    public void setQuantityBonus(double quantityBonus) {
        this.quantityBonus = quantityBonus;
    }

    public double getRarityBonus() {
        return rarityBonus;
    }

    public void setRarityBonus(double rarityBonus) {
        this.rarityBonus = rarityBonus;
    }

}
