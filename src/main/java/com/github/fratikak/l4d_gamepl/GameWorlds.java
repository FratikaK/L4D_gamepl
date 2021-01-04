package com.github.fratikak.l4d_gamepl;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameWorlds {

    public void setTeleportStage(String worldName, Player target) {

        switch (worldName) {
            case "venice":
                Location loc = target.getLocation();
                loc.setX(1403);
                loc.setY(58);
                loc.setZ(1027);
                target.teleport(loc);
                break;
        }
    }
}
