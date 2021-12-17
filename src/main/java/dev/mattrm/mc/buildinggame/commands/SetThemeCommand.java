package dev.mattrm.mc.buildinggame.commands;

import dev.mattrm.mc.buildinggame.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
    name = "settheme",
    desc = "Sets the theme of the game",
    usage = "/settheme",
    permission = "buildinggame.settheme"
))
@Permissions(@Permission(
    name = "buildinggame.settheme",
    defaultValue = PermissionDefault.OP
))
public class SetThemeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        GameManager.getInstance().setTheme(args[0]);

        return true;
    }
}
