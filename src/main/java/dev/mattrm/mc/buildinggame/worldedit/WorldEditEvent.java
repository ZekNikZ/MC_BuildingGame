package dev.mattrm.mc.buildinggame.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;

public abstract class WorldEditEvent {
    protected final Region region;

    protected WorldEditEvent(Region region) {
        this.region = region;
    }

    public final void apply() {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(this.region.getWorld())){
            this.apply(editSession);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    protected abstract void apply(EditSession editSession) throws MaxChangedBlocksException;
}
