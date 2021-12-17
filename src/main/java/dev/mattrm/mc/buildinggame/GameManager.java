package dev.mattrm.mc.buildinggame;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.mattrm.mc.gametools.Service;
import dev.mattrm.mc.gametools.event.PlayerInventoryChangeEvent;
import dev.mattrm.mc.gametools.scoreboards.GameScoreboard;
import dev.mattrm.mc.gametools.scoreboards.ScoreboardService;
import dev.mattrm.mc.gametools.scoreboards.ValueEntry;
import dev.mattrm.mc.gametools.scoreboards.impl.StringValueEntry;
import dev.mattrm.mc.gametools.scoreboards.impl.TimerEntry;
import dev.mattrm.mc.gametools.teams.GameTeam;
import dev.mattrm.mc.gametools.teams.TeamService;
import dev.mattrm.mc.gametools.timer.GameTimer;
import dev.mattrm.mc.gametools.util.ISB;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager extends Service {
    private static final GameManager INSTANCE = new GameManager();

    public static GameManager getInstance() {
        return INSTANCE;
    }

    private StringValueEntry theme;
    private GameTimer timer;
    private boolean isVoting = false;
    private final Map<UUID, Integer> currentVotes = new HashMap<>();
    private final Map<String, Integer> voteTallies = new HashMap<>();
    private String currentTeam;

    @Override
    protected void setupService() {
        setup();
    }

    private void setup() {
        // Create timer
        this.timer = new GameTimer(this.plugin, 10, 7 * 60 * 1000, this::timerStopped);

        // Create scoreboard
        GameScoreboard scoreboard = ScoreboardService.getInstance().createNewScoreboard("Build Battle");
        this.theme = new StringValueEntry(scoreboard, "Theme: ", ValueEntry.ValuePos.SUFFIX, "N/A");
        scoreboard.addEntry(this.theme);
        scoreboard.addEntry(new TimerEntry(scoreboard, this.timer, "Time: ", ValueEntry.ValuePos.SUFFIX, null));
        ScoreboardService.getInstance().setGlobalScoreboard(scoreboard);
    }

    public void startGame() {
//        // Pick random theme
//        List<String> themes = GameConfig.getInstance().getThemes();
//        this.theme.setValue(themes.get(new Random().nextInt(themes.size())));

        // Reset build zones
        GameConfig.getInstance().getBuildingRegions().values().forEach(BuildingRegion::reset);

        // Setup build mode
        BuildingRegions.getInstance().buildMode();

        // Kill mobs
        Bukkit.getWorlds().get(0).getEntities().stream()
            .filter(entity -> !(entity instanceof Player))
            .forEach(Entity::remove);

        // Countdown timer
        ChatUtils.broadcast("The theme is " + ChatColor.AQUA + ChatColor.BOLD + this.theme.getValue() + ChatColor.RESET + ". You will get 20 seconds to plan before building.");
        ChatUtils.broadcast("Resetting previous builds...");
        new BukkitRunnable() {
            private int secAlert = 22;

            @Override
            public void run() {
                if (this.secAlert == 11) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        GameTeam team = TeamService.getInstance().getPlayerTeam(player);
                        if (team == null) return;

                        RegionManager regions = WorldGuard.getInstance().getPlatform()
                            .getRegionContainer()
                            .get(new BukkitWorld(Bukkit.getWorlds().get(0)));
                        if (regions == null) return;

                        ProtectedRegion region = regions.getRegion(team.getId());
                        if (region == null) return;

                        Location flag = region.getFlag(Flags.TELE_LOC);
                        if (flag == null) return;

                        player.setGameMode(GameMode.CREATIVE);
                        player.teleport(new org.bukkit.Location(Bukkit.getWorlds().get(0), flag.getX(), flag.getY(), flag.getZ()));
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.getInventory().clear();
                        player.setHealth(20);
                        player.setFoodLevel(20);
                    });
                } else if (this.secAlert <= 10 && this.secAlert > 0) {
                    ChatUtils.broadcast("The game will begin in " + secAlert + " seconds.");
                } else if (this.secAlert <= 0) {
                    this.cancel();
                    handleGameStart();
                }
                --this.secAlert;
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }

    private void handleGameStart() {
        this.timer.start();
        ChatUtils.broadcast("The game has begun. Good luck!\n"
            + ChatColor.AQUA + " /setfloor <block>" + ChatColor.RESET + " - sets the floor to the specified block id\n"
            + ChatColor.GRAY + "   Example: /setfloor water\n"
            + ChatColor.GRAY + "   Example: /setfloor grass_block\n"
            + ChatColor.AQUA + " /setwall <block> [direction]" + ChatColor.RESET + " - sets the specified wall\n"
            + ChatColor.GRAY + "   Example: /setwall red_concrete\n"
            + ChatColor.GRAY + "   Example: /setwall iron_block north\n"
        );
    }

    private void timerStopped() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();
        });
        BuildingRegions.getInstance().voteMode();
        ChatUtils.broadcast("Game over! Voting will commence in 20 seconds.");
        new BukkitRunnable() {
            private int secAlert = 20;

            @Override
            public void run() {
                if (this.secAlert <= 5 && this.secAlert > 0) {
                    ChatUtils.broadcast("Voting will begin in " + secAlert + " seconds.");
                } else if (this.secAlert <= 0) {
                    this.cancel();
                    handleVotingStart();
                }
                --this.secAlert;
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }

    private void handleVotingStart() {
        this.isVoting = true;

        List<GameTeam> teams = TeamService.getInstance().getTeams().stream()
            .filter(team -> !TeamService.getInstance().getOnlineTeamMembers(team.getId()).isEmpty())
            .collect(Collectors.toList());
        Collections.shuffle(teams);

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.getInventory().setItem(0, ISB.material(Material.BROWN_WOOL).name("" + ChatColor.DARK_RED + ChatColor.BOLD + "Super Poop").build());
            player.getInventory().setItem(1, ISB.material(Material.RED_WOOL).name("" + ChatColor.RED + ChatColor.BOLD + "Poop").build());
            player.getInventory().setItem(2, ISB.material(Material.LIME_WOOL).name("" + ChatColor.GREEN + ChatColor.BOLD + "OK").build());
            player.getInventory().setItem(3, ISB.material(Material.GREEN_WOOL).name("" + ChatColor.DARK_GREEN + ChatColor.BOLD + "Good").build());
            player.getInventory().setItem(4, ISB.material(Material.PURPLE_WOOL).name("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Epic").build());
            player.getInventory().setItem(5, ISB.material(Material.ORANGE_WOOL).name("" + ChatColor.GOLD + ChatColor.BOLD + "Legendary").build());
        });

        int delaySecs = 30;
        int delay = delaySecs * 20;
        this.currentVotes.clear();
        this.voteTallies.clear();

        new BukkitRunnable() {
            int current = 0;

            @Override
            public void run() {
                // Record previous
                if (current > 0) {
                    voteTallies.put(
                        teams.get(current - 1).getId(),
                        currentVotes.values().stream().mapToInt(x -> x).sum()
                    );
                }

                // Was that the last team?
                if (current >= teams.size()) {
                    this.cancel();
                    handleDoneVoting();
                    return;
                }

                // Setup
                currentVotes.clear();

                GameTeam team = teams.get(current);
                currentTeam = team.getId();

                Bukkit.getOnlinePlayers().forEach(player -> {
                    RegionManager regions = WorldGuard.getInstance().getPlatform()
                        .getRegionContainer()
                        .get(new BukkitWorld(Bukkit.getWorlds().get(0)));
                    if (regions == null) return;

                    ProtectedRegion region = regions.getRegion(team.getId());
                    if (region == null) return;

                    Location flag = region.getFlag(Flags.TELE_LOC);
                    if (flag == null) return;

                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(new org.bukkit.Location(Bukkit.getWorlds().get(0), flag.getX(), flag.getY(), flag.getZ()));
                    player.setAllowFlight(true);
                    player.setFlying(true);
                });

                ChatUtils.broadcast("You are now viewing " + team.getFormatCode() + ChatColor.BOLD + team.getName() + ChatColor.RESET + "'s build. Make sure to vote!");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    ChatUtils.broadcast("Moving on in 15 seconds. Make sure to vote!");
                }, delay - 15 * 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    ChatUtils.broadcast("Moving on in 5 seconds. Make sure to vote!");
                }, delay - 100);

                ++current;
            }
        }.runTaskTimer(this.plugin, 0, delay);
    }

    private void handleDoneVoting() {
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());
        this.isVoting = false;

        // Print results
        ChatUtils.broadcast("Voting results: \n" +
            this.voteTallies.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(value -> -value)))
                .map(entry -> {
                    GameTeam team = TeamService.getInstance().getTeam(entry.getKey());
                    return " " + team.getFormatCode() + ChatColor.BOLD + team.getName() + ChatColor.RESET + ": " + ChatColor.GOLD + entry.getValue();
                })
                .collect(Collectors.joining("\n"))
        );

        // Reset theme
        this.theme.setValue("N/A");
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onClickItem(PlayerInteractEvent event) {
        if (!this.isVoting()) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getItem() == null) return;

        GameTeam team = TeamService.getInstance().getPlayerTeam(event.getPlayer());
        if (team != null && team.getId().equals(currentTeam)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't vote for yourself!");
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        int voteVal = switch (event.getItem().getType()) {
            case BROWN_WOOL -> 1;
            case RED_WOOL -> 2;
            case LIME_WOOL -> 3;
            case GREEN_WOOL -> 5;
            case PURPLE_WOOL -> 7;
            case ORANGE_WOOL -> 9;
            default -> 0;
        };

        if (voteVal != 0) {
            recordVote(event.getPlayer().getUniqueId(), voteVal);
            ChatUtils.sendMessage(event.getPlayer(), "You voted: " + event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
        }
    }

    @EventHandler
    private void onInventoryChange(PlayerInventoryChangeEvent event) {
        if (this.isVoting) event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void silenceMobs(EntitySpawnEvent event) {
        event.getEntity().setSilent(true);
    }

    private void recordVote(UUID uniqueId, int voteVal) {
        this.currentVotes.put(uniqueId, voteVal);
    }

    public boolean isVoting() {
        return this.isVoting;
    }

    public void setTheme(String theme) {
        this.theme.setValue(theme);
    }

    public String getTheme() {
        return this.theme.getValue();
    }
}
