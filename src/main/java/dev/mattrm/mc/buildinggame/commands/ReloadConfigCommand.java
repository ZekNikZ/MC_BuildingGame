package dev.mattrm.mc.buildinggame.commands;

import dev.mattrm.mc.buildinggame.BuildingGame;
import dev.mattrm.mc.buildinggame.GameConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
    name = "reloadconfig",
    desc = "Reload the game config",
    usage = "/reloadconfig",
    permission = "buildinggame.reloadconfig"
))
@Permissions(@Permission(
    name = "buildinggame.reloadconfig",
    defaultValue = PermissionDefault.OP
))
public class ReloadConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GameConfig.getInstance().reload();
        return true;
    }
}
