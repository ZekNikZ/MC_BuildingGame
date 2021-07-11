package dev.mattrm.mc.buildinggame;

import dev.mattrm.mc.gametools.Service;

public class BuildingRegions extends Service {
    private static final BuildingRegions INSTANCE = new BuildingRegions();

    public static BuildingRegions getInstance() {
        return INSTANCE;
    }

    @Override
    protected void setupService() {
        GameConfig.getInstance().getBuildingRegions().values()
            .forEach(BuildingRegion::register);
    }
}
