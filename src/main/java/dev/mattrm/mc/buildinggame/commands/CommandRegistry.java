package dev.mattrm.mc.buildinggame.commands;

import dev.mattrm.mc.gametools.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistry implements CommandGroup {
    @Override
    public void registerCommands(JavaPlugin plugin) {
        plugin.getCommand("setfloor").setExecutor(new SetFloorCommand());
        plugin.getCommand("setwall").setExecutor(new SetWallCommand());
        plugin.getCommand("startgame").setExecutor(new StartGameCommand());
        plugin.getCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
        plugin.getCommand("settheme").setExecutor(new SetThemeCommand());
    }
}
