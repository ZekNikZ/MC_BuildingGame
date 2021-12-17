package dev.mattrm.mc.buildinggame;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import dev.mattrm.mc.gametools.Service;
import dev.mattrm.mc.gametools.event.TeamChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.Objects;

import static dev.mattrm.mc.buildinggame.BuildingGame.LOGGER;

public class BuildingRegions extends Service {
    private static final BuildingRegions INSTANCE = new BuildingRegions();

    public static BuildingRegions getInstance() {
        return INSTANCE;
    }

    @Override
    protected void setupService() {
        GameConfig.getInstance().getBuildingRegions().values()
            .forEach(BuildingRegion::register);
        this.buildMode();
    }

    public void buildMode() {
        RegionManager regions = WorldGuard.getInstance().getPlatform()
            .getRegionContainer()
            .get(new BukkitWorld(Bukkit.getWorlds().get(0)));

        if (regions == null) return;

        GameConfig.getInstance().getBuildingRegions().keySet().stream()
            .map(regions::getRegion)
            .filter(Objects::nonNull)
            .forEach(region -> {
                region.setFlag(Flags.GAME_MODE, GameMode.REGISTRY.get("creative"));
                region.setFlag(Flags.GAME_MODE.getRegionGroupFlag(), RegionGroup.ALL);
                region.setFlag(Flags.EXIT, StateFlag.State.DENY);
                region.setFlag(Flags.EXIT.getRegionGroupFlag(), RegionGroup.ALL);
                LOGGER.info("Set region " + region.getId() + " to build mode");
            });
    }

    public void voteMode() {
        RegionManager regions = WorldGuard.getInstance().getPlatform()
            .getRegionContainer()
            .get(new BukkitWorld(Bukkit.getWorlds().get(0)));

        if (regions == null) return;

        GameConfig.getInstance().getBuildingRegions().keySet().stream()
            .map(regions::getRegion)
            .filter(Objects::nonNull)
            .forEach(region -> {
                region.setFlag(Flags.GAME_MODE, GameMode.REGISTRY.get("adventure"));
                region.setFlag(Flags.GAME_MODE.getRegionGroupFlag(), RegionGroup.ALL);
                region.setFlag(Flags.EXIT, StateFlag.State.DENY);
                region.setFlag(Flags.EXIT.getRegionGroupFlag(), RegionGroup.ALL);
                LOGGER.info("Set region " + region.getId() + " to vote mode");
            });
    }

    @EventHandler
    private void onTeamChange(TeamChangeEvent event) {
        RegionManager regions = WorldGuard.getInstance().getPlatform()
            .getRegionContainer()
            .get(new BukkitWorld(Bukkit.getWorlds().get(0)));

        if (regions == null) return;

        if (event.getOldTeam() != null) {
            regions.getRegion(event.getOldTeam().getId()).getMembers().removePlayer(event.getPlayerUUID());
        }

        regions.getRegion(event.getNewTeam().getId()).getMembers().addPlayer(event.getPlayerUUID());
    }
}
