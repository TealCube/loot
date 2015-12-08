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
package info.faceland.loot.listeners.anticheat;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.anticheat.AnticheatTag;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class AnticheatListener implements Listener {

    private final LootPlugin plugin;

    public AnticheatListener(LootPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        LivingEntity li = null;
        if (event.getDamager() instanceof Player) {
            li = (LivingEntity) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if ( ((Projectile)event.getDamager()).getShooter() instanceof Player) {
                li = ((Player) ((Projectile) event.getDamager()).getShooter()).getPlayer();
            }
        }
        if (li == null) {
            return;
        }
        if (!plugin.getAnticheatManager().isTagged((LivingEntity) event.getEntity())) {
            plugin.getAnticheatManager().addTag((LivingEntity) event.getEntity());
        }
        AnticheatTag tag = plugin.getAnticheatManager().getTag((LivingEntity) event.getEntity());
        if (tag.getTaggerLocation(li.getUniqueId()) != null) {
            return;
        }
        tag.setTaggerLocation(li.getUniqueId(), li.getLocation());
        tag.setTaggerDamage(li.getUniqueId(), tag.getTaggerDamage(li.getUniqueId()) + event.getFinalDamage());
        plugin.getAnticheatManager().pushTag((LivingEntity) event.getEntity(), tag);
    }

}
