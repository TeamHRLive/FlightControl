package org.Spazzinq.FlightControl;

import com.earth2me.essentials.Essentials;
import net.minelink.ctplus.CombatTagPlus;
import org.Spazzinq.FlightControl.Hooks.Combat.AntiLogging;
import org.Spazzinq.FlightControl.Hooks.Combat.TagPlus;
import org.Spazzinq.FlightControl.Hooks.Factions.Factions;
import org.Spazzinq.FlightControl.Hooks.Factions.Massive;
import org.Spazzinq.FlightControl.Hooks.Factions.UUIDSavage;
import org.Spazzinq.FlightControl.Hooks.Plot.Plot;
import org.Spazzinq.FlightControl.Hooks.Plot.Squared;
import org.Spazzinq.FlightControl.Hooks.Vanish.Ess;
import org.Spazzinq.FlightControl.Hooks.Vanish.PremiumSuper;
import org.Spazzinq.FlightControl.Hooks.Vanish.Vanish;
import org.Spazzinq.FlightControl.Multiversion.Combat;
import org.Spazzinq.FlightControl.Multiversion.Particles;
import org.Spazzinq.FlightControl.Multiversion.v13.LogX13;
import org.Spazzinq.FlightControl.Multiversion.v13.Particles13;
import org.Spazzinq.FlightControl.Multiversion.v8.LogX8;
import org.Spazzinq.FlightControl.Multiversion.v8.Particles8;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;

class Listener implements org.bukkit.event.Listener {
    private FlightControl pl;
	private PluginManager pm = Bukkit.getPluginManager();
    private boolean isSpigot = Bukkit.getServer().getVersion().contains("Spigot");

    private Particles particles = Config.is13 ? new Particles13() : new Particles8();
	private Plot plot = pm.getPlugin("PlotSquared") != null ? new Squared() : new Plot();
	private Combat combat = new Combat();
	private Factions fac = new Factions();
	private Vanish vanish = new Vanish();

	private ArrayList<Player> fall = new ArrayList<>();

	Listener(FlightControl i) {
	    pl = i; pm.registerEvents(this, i);
	    if (pm.getPlugin("CombatLogX") != null) combat = Config.is13 ? new LogX13() : new LogX8();
	    else if (pm.getPlugin("CombatTagPlus") != null) combat = new TagPlus(((CombatTagPlus) pm.getPlugin("CombatTagPlus")).getTagManager());
	    else if (pm.getPlugin("AntiCombatLogging") != null) combat = new AntiLogging();
        if (pm.getPlugin("PremiumVanish") != null || pm.getPlugin("SuperVanish") != null) vanish = new PremiumSuper();
        else if (pm.getPlugin("Essentials") != null) vanish = new Ess((Essentials) pm.getPlugin("Essentials"));
        if (pm.getPlugin("Factions") != null) fac = pm.getPlugin("MassiveCore") != null ? new Massive() : new UUIDSavage();
    }

	@EventHandler
	private void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.FALL) { Player p = (Player) e.getEntity();
            if (fall.contains(p)) { e.setCancelled(true); fall.remove(p); }
        }
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if (!p.hasPermission("flightcontrol.bypass") && !(vanish.vanished(p) && Config.vanishBypass) && p.getGameMode() != GameMode.SPECTATOR) {
			String world = e.getTo().getWorld().getName();
			String region = pl.regions.region(e.getTo());
			boolean enable = fac.rel(p, true)
                            || p.hasPermission("flightcontrol.flyall")
                            || p.hasPermission("flightcontrol.fly." + world)
                            || (region != null && p.hasPermission("flightcontrol.fly." + world + "." + region))
                            || Config.eWorlds.contains(world)
                            || Config.eRegions.containsKey(world) && Config.eRegions.get(world).contains(region),
                    disable = combat.tagged(p) || fac.rel(p, false) || !plot.flight(e.getTo())
                            || p.hasPermission("flightcontrol.nofly." + world)
                            || Config.dWorlds.contains(world)
                            || Config.dRegions.containsKey(world) && Config.dRegions.get(world).contains(region)
                            || region != null && p.hasPermission("flightcontrol.nofly." + world + "." + region);
			if (p.getAllowFlight()) {
			    if (disable || !enable) disableFlight(p);
			} else if (enable && !disable) enableFlight(p);
		} else if (!p.getAllowFlight()) enableFlight(p);
		if ((particles instanceof Particles13 || (isSpigot && particles instanceof Particles8)) && Config.flightTrail &&
                !Config.trailPrefs.contains(p.getUniqueId().toString()) && e.getFrom().distance(e.getTo()) > 0 &&
                p.isFlying() && p.getGameMode() != GameMode.SPECTATOR && !vanish.vanished(p)) particles.play(p.getWorld(), p, e.getTo(), e.getFrom());
	}

	private void enableFlight(Player p) {
        p.setAllowFlight(true);
        Sound.play(p, Config.eSound);
        pl.msg(p, Config.eFlight);
    }

    private void disableFlight(Player p) {
        p.setAllowFlight(false);
        p.setFlying(false);
        Sound.play(p, Config.dSound);
        if (Config.cancelFall) { fall.add(p); Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> fall.remove(p), 120); }
        pl.msg(p, Config.dFlight);
    }
}