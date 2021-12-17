package dev.mattrm.mc.buildinggame;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.*;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.mattrm.mc.buildinggame.worldedit.FillRegionEvent;
import org.bukkit.Bukkit;

import static dev.mattrm.mc.buildinggame.BuildingGame.LOGGER;

public class BuildingRegion {
    public enum Wall {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    private final String id;
    private final BlockVector3 min;
    private final BlockVector3 max;
    private final ProtectedRegion region;

    public BuildingRegion(String id, BlockVector3 min, BlockVector3 max) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.region = new ProtectedCuboidRegion(this.id, min, max);
        this.region.setFlag(Flags.TELE_LOC, new Location(
            new BukkitWorld(Bukkit.getWorlds().get(0)),
            min.getX() + (max.getX() - min.getX()) / 2f,
            min.getY() + (max.getY() - min.getY()) / 4f,
            min.getZ() + (max.getZ() - min.getZ()) / 2f
        ));
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

    public BlockVector3 getMin() {
        return min;
    }

    public BlockVector3 getMax() {
        return max;
    }

    public void setFloor(BlockType blockType) {
        WorldEditEventQueue.enqueue(new FillRegionEvent(
            Bukkit.getWorlds().get(0),
            this.getMin(),
            this.getMax().clampY(1, 3),
            blockType
        ));
    }

    public void reset() {
        WorldEditEventQueue.enqueue(new FillRegionEvent(
            Bukkit.getWorlds().get(0),
            this.getMin(),
            this.getMax(),
            BlockTypes.AIR
        ));

        this.setFloor(BlockTypes.GRASS_BLOCK);
        this.setWall(BlockTypes.CYAN_CONCRETE);
    }

    public void setWall(Wall wall, BlockType blockType) {
        BlockVector3 min = this.getMin().add(BlockVector3.ONE.multiply(-1));
        switch (wall) {
            case NORTH -> WorldEditEventQueue.enqueue(new FillRegionEvent(
                Bukkit.getWorlds().get(0),
                min,
                min.add(BlockVector3.at(31, 100, 0)),
                blockType
            ));
            case SOUTH -> WorldEditEventQueue.enqueue(new FillRegionEvent(
                Bukkit.getWorlds().get(0),
                min.add(BlockVector3.at(0, 0, 31)),
                min.add(BlockVector3.at(31, 100, 31)),
                blockType
            ));
            case WEST -> WorldEditEventQueue.enqueue(new FillRegionEvent(
                Bukkit.getWorlds().get(0),
                min,
                min.add(BlockVector3.at(0, 100, 31)),
                blockType
            ));
            case EAST -> WorldEditEventQueue.enqueue(new FillRegionEvent(
                Bukkit.getWorlds().get(0),
                min.add(BlockVector3.at(31, 0, 0)),
                min.add(BlockVector3.at(31, 100, 31)),
                blockType
            ));
        }
    }

    public void setWall(BlockType blockType) {
        for (Wall wall : Wall.values()) {
            setWall(wall, blockType);
        }
    }
}
