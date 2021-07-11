package dev.mattrm.mc.buildinggame;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;

import static dev.mattrm.mc.buildinggame.BuildingGame.LOGGER;

public class BuildingRegion {
    private final String id;
    private final ProtectedRegion region;

    public BuildingRegion(String id, BlockVector3 min, BlockVector3 max) {
        this.id = id;
        this.region = new ProtectedCuboidRegion(this.id, min, max);
    }

    public void register() {
        RegionManager regions = WorldGuard.getInstance().getPlatform()
            .getRegionContainer()
            .get(new BukkitWorld(Bukkit.getWorlds().get(0)));

        if (regions != null) {
            regions.removeRegion(this.id);
            regions.addRegion(this.region);
            LOGGER.info("Set up region " + this.id + ".");
        }
    }

    public void unregister() {
        RegionManager regions = WorldGuard.getInstance().getPlatform()
            .getRegionContainer()
            .get(new BukkitWorld(Bukkit.getWorlds().get(0)));

        if (regions != null) {
            regions.removeRegion(this.id);
        }
    }
}
