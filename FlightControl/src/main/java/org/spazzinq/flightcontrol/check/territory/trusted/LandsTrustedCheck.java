/*
 * This file is part of FlightControl, which is licensed under the MIT License.
 * Copyright (c) 2024 George Fang
 */

package org.spazzinq.flightcontrol.check.territory.trusted;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.entity.Player;
import org.spazzinq.flightcontrol.FlightControl;
import org.spazzinq.flightcontrol.check.territory.TerritoryCheck;

public final class LandsTrustedCheck extends TerritoryCheck {
    private final LandsIntegration landsIntegration;

    public LandsTrustedCheck() {
        landsIntegration = LandsIntegration.of(FlightControl.getInstance());
    }

    @Override public boolean check(Player p) {
        Area area = landsIntegration.getArea(p.getLocation());

        return area != null && area.isTrusted(p.getUniqueId());
    }
}
