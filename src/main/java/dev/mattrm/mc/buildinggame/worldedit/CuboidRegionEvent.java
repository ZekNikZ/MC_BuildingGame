package dev.mattrm.mc.buildinggame.worldedit;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;

public abstract class CuboidRegionEvent extends WorldEditEvent {
    protected CuboidRegionEvent(org.bukkit.World world, BlockVector3 p1, BlockVector3 p2) {
        this(new BukkitWorld(world), p1, p2);
    }
    protected CuboidRegionEvent(World world, BlockVector3 p1, BlockVector3 p2) {
        super(new CuboidRegion(world, p1, p2));
    }
}
