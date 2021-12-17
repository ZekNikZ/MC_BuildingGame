package dev.mattrm.mc.buildinggame.commands;

import com.sk89q.worldedit.world.block.BlockType;
import dev.mattrm.mc.buildinggame.BuildingRegion;
import dev.mattrm.mc.buildinggame.GameConfig;
import dev.mattrm.mc.gametools.teams.GameTeam;
import dev.mattrm.mc.gametools.teams.TeamService;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.Wall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

import java.util.Locale;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
    name = "setwall",
    desc = "Chooses the floor block",
    usage = "/setfloor <block> [wall]"
))
public class SetWallCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 && args.length != 2) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        // Block type
        BlockType blockType = BlockType.REGISTRY.get(args[0]);
        if (blockType == null || !(blockType.getMaterial().isLiquid() || blockType.getMaterial().isSolid())) {
            sender.sendMessage(ChatColor.RED + "Invalid block type.");
            return true;
        }

        // Team
        GameTeam team = TeamService.getInstance().getPlayerTeam((Player) sender);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a team.");
            return true;
        }

        // Enqueue event
        BuildingRegion region = GameConfig.getInstance().getBuildingRegions().get(team.getId());
        if (args.length == 2) {
            region.setWall(BuildingRegion.Wall.valueOf(args[1].toUpperCase()), BlockType.REGISTRY.get(args[0]));
        } else {
            region.setWall(BlockType.REGISTRY.get(args[0]));
        }

        return true;
    }
}
