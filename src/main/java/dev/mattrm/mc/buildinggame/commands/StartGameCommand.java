package dev.mattrm.mc.buildinggame.commands;

import dev.mattrm.mc.buildinggame.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
    name = "startgame",
    desc = "Start the game",
    usage = "/startgame",
    permission = "buildinggame.startgame"
))
@Permissions(@Permission(
    name = "buildinggame.startgame",
    defaultValue = PermissionDefault.OP
))
public class StartGameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (GameManager.getInstance().getTheme().equals("N/A")) {
            sender.sendMessage(ChatColor.RED + "You must set a theme first.");
            return true;
        }

        GameManager.getInstance().startGame();

        return true;
    }
}
