/*
 * This file is part of FlightControl, which is licensed under the MIT License.
 * Copyright (c) 2024 George Fang
 */

package org.spazzinq.flightcontrol.multiversion.current;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.spazzinq.flightcontrol.multiversion.FlightParticle;

public class ParticleNewAPI implements FlightParticle {
    private Particle particle = Particle.CLOUD;
    private Object data;
    private int count = 4;

    public void spawn(Location loc) {
        if (loc.getWorld() != null) {
            loc.getWorld().spawnParticle(particle, particle == Particle.CLOUD ? loc.clone().subtract(0, .3, 0) : loc,
                count, data);
        }
    }

    public void setParticle(String s) {
        try {
            particle = Particle.valueOf(s);
        } catch (Exception ignored) {
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRBG(int r, int g, int b) {
        data = null;
        switch (particle) {
            case DUST:
                data = new Particle.DustOptions(Color.fromRGB(r, g, b), count);
                break;
            case ENTITY_EFFECT: {
                data = Color.fromRGB(r, g, b);
                break;
            }
            default:
                break;
        }
    }
}
