/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

package info.faceland.loot.creatures;

import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public final class LootCreatureMod implements CreatureMod {

    private final EntityType entityType;
    private Map<CustomItem, Double> customItemDoubleMap;
    private Map<SocketGem, Double> socketGemDoubleMap;
    private Map<Tier, Double> tierDoubleMap;
    private Map<EnchantmentTome, Double> enchantmentStoneDoubleMap;

    public LootCreatureMod(EntityType entityType) {
        this.entityType = entityType;
        this.customItemDoubleMap = new HashMap<>();
        this.socketGemDoubleMap = new HashMap<>();
        this.tierDoubleMap = new HashMap<>();
        this.enchantmentStoneDoubleMap = new HashMap<>();
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public Map<CustomItem, Double> getCustomItemMults() {
        return new HashMap<>(customItemDoubleMap);
    }

    @Override
    public Map<SocketGem, Double> getSocketGemMults() {
        return new HashMap<>(socketGemDoubleMap);
    }

    @Override
    public Map<Tier, Double> getTierMults() {
        return new HashMap<>(tierDoubleMap);
    }

    @Override
    public double getCustomItemMult(CustomItem ci) {
        if (getCustomItemMults().containsKey(ci)) {
            return getCustomItemMults().get(ci);
        }
        return 0;
    }

    @Override
    public double getSocketGemMult(SocketGem sg) {
        if (getSocketGemMults().containsKey(sg)) {
            return getSocketGemMults().get(sg);
        }
        return 0;
    }

    @Override
    public double getTierMult(Tier t) {
        if (getTierMults().containsKey(t)) {
            return getTierMults().get(t);
        }
        return 0;
    }

    @Override
    public Map<EnchantmentTome, Double> getEnchantmentStoneMults() {
        return new HashMap<>(enchantmentStoneDoubleMap);
    }

    void setEnchantmentStoneDoubleMap(Map<EnchantmentTome, Double> enchantmentStoneDoubleMap) {
        this.enchantmentStoneDoubleMap = enchantmentStoneDoubleMap;
    }

    void setCustomItemDoubleMap(Map<CustomItem, Double> customItemDoubleMap) {
        this.customItemDoubleMap = customItemDoubleMap;
    }

    void setSocketGemDoubleMap(Map<SocketGem, Double> socketGemDoubleMap) {
        this.socketGemDoubleMap = socketGemDoubleMap;
    }

    void setTierDoubleMap(Map<Tier, Double> tierDoubleMap) {
        this.tierDoubleMap = tierDoubleMap;
    }

    @Override
    public int hashCode() {
        return entityType != null ? entityType.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LootCreatureMod that = (LootCreatureMod) o;

        return entityType == that.entityType;
    }

}
