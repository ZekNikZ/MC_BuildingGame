package dev.mattrm.mc.buildinggame.commands;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import dev.mattrm.mc.buildinggame.BuildingRegion;
import dev.mattrm.mc.buildinggame.GameConfig;
import dev.mattrm.mc.buildinggame.WorldEditEventQueue;
import dev.mattrm.mc.buildinggame.worldedit.FillRegionEvent;
import dev.mattrm.mc.gametools.teams.GameTeam;
import dev.mattrm.mc.gametools.teams.TeamService;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
    name = "setfloor",
    desc = "Chooses the floor block",
    usage = "/setfloor <block>"
))
public class SetFloorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
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
        region.setFloor(BlockType.REGISTRY.get(args[0]));

        return true;
    }
}
