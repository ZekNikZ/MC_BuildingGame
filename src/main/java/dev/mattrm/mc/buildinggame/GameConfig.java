package dev.mattrm.mc.buildinggame;

import com.sk89q.worldedit.math.BlockVector3;
import dev.mattrm.mc.gametools.Service;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameConfig extends Service {
    private static final GameConfig INSTANCE = new GameConfig();

    public static GameConfig getInstance() {
        return INSTANCE;
    }

    private final List<String> themes = new ArrayList<>();
    private final Map<String, BuildingRegion> buildingRegions =
        new HashMap<>();

    @Override
    protected void setupService() {
        this.loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = this.plugin.getConfig();
        this.themes.clear();
        this.themes.addAll(config.getStringList("themes"));
        this.buildingRegions.values().forEach(BuildingRegion::unregister);
        this.buildingRegions.clear();
        ConfigurationSection regions = config.getConfigurationSection(
            "regions");
        if (regions != null) {
            regions.getKeys(false).forEach(id -> {
                ConfigurationSection region =
                    regions.getConfigurationSection(id);
                if (region == null) return;
                this.buildingRegions.put(id, new BuildingRegion(
                    id,
                    BlockVector3.at(
                        region.getInt("min_x"),
                        region.getInt("min_y"),
                        region.getInt("min_z")
                    ).add(BlockVector3.ONE),
                    BlockVector3.at(
                        region.getInt("max_x"),
                        region.getInt("max_y"),
                        region.getInt("max_z")
                    ).add(BlockVector3.at(-1, 0, -1))
                ));
            });
        }
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.loadConfig();
    }

    public List<String> getThemes() {
        return this.themes;
    }

    public Map<String, BuildingRegion> getBuildingRegions() {
        return this.buildingRegions;
    }
}
