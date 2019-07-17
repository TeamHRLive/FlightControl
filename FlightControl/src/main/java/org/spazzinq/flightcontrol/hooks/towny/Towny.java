/*
 * This file is part of FlightControl-parent, which is licensed under the MIT License
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

package org.spazzinq.flightcontrol.hooks.towny;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.entity.Player;

public final class Towny extends BaseTowny {
    @Override
    public boolean ownTown(Player p) {
        Resident r;
        try { r = TownyUniverse.getDataSource().getResident(p.getName()); } catch (NotRegisteredException e) { return false; }
        if (r.hasTown() && !TownyUniverse.isWilderness(p.getLocation().getBlock()))
            try { if (r.getTown().equals(TownyUniverse.getTownBlock(p.getLocation()).getTown())) return true;
            } catch (NotRegisteredException e) { e.printStackTrace(); }
        return false;
    }
    @Override public boolean wartime() { return TownyUniverse.isWarTime(); }
    @Override public boolean isHooked() { return true; }
}
