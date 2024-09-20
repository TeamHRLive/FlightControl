/*
 * This file is part of FlightControl, which is licensed under the MIT License.
 * Copyright (c) 2024 George Fang
 */

package org.spazzinq.flightcontrol.multiversion.legacy;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.spazzinq.flightcontrol.multiversion.FlightParticle;

public class ParticleOldAPI implements FlightParticle {
       private Particle particle = Particle.CLOUD;
    private Particle.DustOptions o;
    private int amount = 4;
    private double extra, x, y, z;

    public void spawn(Location loc) {
        if (loc.getWorld() != null) {
            loc.getWorld().spawnParticle(particle, particle == Particle.CLOUD ? loc.clone().subtract(0, .3, 0) : loc,
                amount, x, y, z, extra, o, true);
        }
    }

    public void setParticle(String s) {
        try {
            particle = Particle.valueOf(s);
        } catch (Exception ignored) {
        }

        switch (particle) {
            case REDSTONE, SPELL_MOB, SPELL_MOB_AMBIENT, NOTE -> extra = 1;
            default -> extra = 0;
        }
    }

    public void setCount(int amount) {
        this.amount = amount;
    }

    public void setRBG(int r, int g, int b) {
        x = 0;
        y = 0;
        z = 0;
        o = null;
        switch (particle) {
            case REDSTONE:
                o = new Particle.DustOptions(Color.fromRGB(r, g, b), amount);
                break;
            case SPELL_MOB, SPELL_MOB_AMBIENT: {
                x = r / 255d;
                y = g / 255d;
                z = b / 255d;
                break;
            }
            case NOTE:
                x = r / 24.0;
                break;
            default:
                break;
        }
    }
}