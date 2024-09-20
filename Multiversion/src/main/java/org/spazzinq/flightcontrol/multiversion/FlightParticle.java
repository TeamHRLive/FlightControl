/*
 * This file is part of FlightControl, which is licensed under the MIT License.
 * Copyright (c) 2024 George Fang
 */

package org.spazzinq.flightcontrol.multiversion;

import org.bukkit.Location;

public interface FlightParticle {
    void spawn(Location l);

    void setParticle(String s);

    void setCount(int count);

    void setRBG(int r, int g, int b);
}
