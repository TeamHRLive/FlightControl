/*
 * This file is part of FlightControl, which is licensed under the MIT License.
 * Copyright (c) 2024 George Fang
 */

package org.spazzinq.flightcontrol.manager;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.spazzinq.flightcontrol.FlightControl;
import org.spazzinq.flightcontrol.multiversion.FactionsGenericHook;
import org.spazzinq.flightcontrol.multiversion.WorldGuardGenericHook;
import org.spazzinq.flightcontrol.multiversion.current.FactionsUUIDHook;
import org.spazzinq.flightcontrol.multiversion.current.WorldGuard7Hook;
import org.spazzinq.flightcontrol.placeholder.ClipPlaceholder;

import java.util.ArrayList;

public class HookManager {
    private final FlightControl pl;
    private final PluginManager pm;

    @Getter private String hookedMsg;
    private final ArrayList<String> hooked = new ArrayList<>();

    // Load early to prevent NPEs
    @Getter private WorldGuardGenericHook worldGuardHook = new WorldGuardGenericHook();
    @Getter private FactionsGenericHook factionsHook = new FactionsGenericHook();

    public HookManager() {
        pl = FlightControl.getInstance();
        pm = pl.getServer().getPluginManager();
    }

    public void loadHooks() {
        loadFactionsHook();
        loadPlaceholderHooks();

        if (pluginLoading("WorldGuard")) {
            worldGuardHook = new WorldGuard7Hook();
        }

        printLoadedHooks();
    }

    private void loadFactionsHook() {
        if (pluginLoading("Factions")) {
            factionsHook = new FactionsUUIDHook();
        }
    }

    private void loadPlaceholderHooks() {
        if (pluginLoading("PlaceholderAPI")) {
            new ClipPlaceholder(pl).register();
        }
    }

    private void printLoadedHooks() {
        hookedMsg = hooked.isEmpty() ? "Hooked with no plugins." : "Loaded hooks: " + hooked;

        pl.getLogger().info(hookedMsg);
    }

    private boolean pluginLoading(String pluginName) {
        // Don't use .isPluginEnabled()--plugin might not yet be ready
        boolean enabled = pm.getPlugin(pluginName) != null;

        if (enabled) {
            hooked.add(pluginName);
        }

        return enabled;
    }
}
