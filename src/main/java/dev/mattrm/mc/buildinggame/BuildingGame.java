package dev.mattrm.mc.buildinggame;

import dev.mattrm.mc.buildinggame.commands.CommandRegistry;
import dev.mattrm.mc.gametools.Service;
import dev.mattrm.mc.gametools.teams.TeamService;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.util.logging.Logger;

@Plugin(name = "BuildingGame", version = "1.0")
@Author("ZekNikZ")
@SoftDependency("GameToolsLibrary")
@Dependency("WorldEdit")
@Dependency("WorldGuard")
@ApiVersion(ApiVersion.Target.v1_17)
public final class BuildingGame extends JavaPlugin {
    public static Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = this.getLogger();

        // Setup default config
        this.saveDefaultConfig();

        // Register services
        this.registerServices();

        // Register commands
        new CommandRegistry().registerCommands(this);

        TeamService.getInstance().setupDefaultTeams();
    }

    private void registerServices() {
        Service[] services = new Service[]{
            GameConfig.getInstance(),
            BuildingRegions.getInstance(),
            WorldEditEventQueue.getInstance(),
            GameManager.getInstance()
        };

        PluginManager pluginManager = this.getServer().getPluginManager();
        for (Service service : services) {
            service.setup(this);
            pluginManager.registerEvents(service, this);
        }

        LOGGER.info("Setup services.");
    }
}
