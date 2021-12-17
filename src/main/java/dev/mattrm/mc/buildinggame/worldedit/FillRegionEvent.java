package dev.mattrm.mc.buildinggame.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.World;

public class FillRegionEvent extends CuboidRegionEvent {
    private final BlockType material;

    public FillRegionEvent(World world, BlockVector3 p1, BlockVector3 p2, BlockType material) {
        super(world, p1, p2);
        this.material = material;
    }

    public FillRegionEvent(com.sk89q.worldedit.world.World world, BlockVector3 p1, BlockVector3 p2, BlockType material) {
        super(world, p1, p2);
        this.material = material;
    }

    @Override
    protected void apply(EditSession editSession) throws MaxChangedBlocksException {
        editSession.setBlocks(this.region, this.material.getDefaultState());
    }
}
