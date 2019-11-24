/*
 * This file is part of FlightControl, which is licensed under the MIT License
 *
 * Copyright (c) 2019 Spazzinq
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.spazzinq.flightcontrol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spazzinq.flightcontrol.api.objects.Sound;

final class Listener implements org.bukkit.event.Listener {
    private FlightControl pl;

	Listener(FlightControl pl) { this.pl = pl; Bukkit.getPluginManager().registerEvents(this, pl); }

    // Check fly status
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent e) {
	    pl.flightManager.check(e.getPlayer(), e.getTo());
	}
    // Fly particles
    @EventHandler private void onToggleFly(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (e.isFlying()) {
            pl.trailManager.trailCheck(p);
            if (pl.configManager.isEveryEnable()) {
                Sound.play(p, pl.configManager.getESound());
            }
        } else pl.trailManager.trailRemove(p);
    }
    // Because onMove doesn't trigger right after a TP
    @EventHandler private void onTP(PlayerTeleportEvent e) { pl.flightManager.check(e.getPlayer(), e.getTo()); }
	@EventHandler private void onQuit(PlayerQuitEvent e) { pl.trailManager.trailRemove(e.getPlayer()); }
	@EventHandler private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new BukkitRunnable() {
            public void run() {
                pl.getTempflyManager().getAndSetTempfly(p);
                pl.flightManager.check(p);
                if (p.isFlying()) {
                    pl.trailManager.trailCheck(p);
                }
            }
        }.runTaskLater(pl, 10);

	    p.setFlySpeed(pl.configManager.getFlightSpeed());
	}
	// Because commands might affect permissions/fly
	@EventHandler private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
	    new BukkitRunnable() { public void run() {
            pl.flightManager.check(p);
	        if (p.isFlying() && !pl.trailManager.partTasks.containsKey(p)) new BukkitRunnable() { public void run() { pl.trailManager.trailCheck(p); } }.runTask(pl);
	        else if (!p.isFlying() && pl.trailManager.partTasks.containsKey(p)) pl.trailManager.trailRemove(p);
	    } }.runTask(pl);
	}

	// Fall damage prevention
    @EventHandler private void onFallDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.FALL &&
            pl.flightManager.cancelFallList.remove(e.getEntity())) e.setCancelled(true);
    }
    // On-the-fly permission management
    @EventHandler private void onWorldLoad(WorldLoadEvent e) {
        pl.getConfigManager().loadWorld(e.getWorld());
    }
}