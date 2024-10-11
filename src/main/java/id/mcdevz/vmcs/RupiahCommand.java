package id.mcdevz.vmcs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RupiahCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    public final Map<UUID, Integer> playerRupiahMap = new HashMap<>();
    private final File dataFolder;

    public RupiahCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        loadPlayerData();  // Load data when the plugin starts
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "/rupiah help for help commands");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();

            switch (args[0].toLowerCase()) {
                case "help":
                    helprupiah(sender);
                    break;
                case "add":
                    if (sender.hasPermission("vmcs.rupiah.add")) {
                        if (args.length == 3) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);
                            if (targetPlayer != null) {
                                handleAdd(sender, targetPlayer.getUniqueId(), args[2]);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player not found.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /rupiah add <player_name> <jumlah>");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    }
                    break;
                case "remove":
                    if (sender.hasPermission("vmcs.rupiah.remove")) {
                        if (args.length == 3) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);
                            if (targetPlayer != null) {
                                handleRemove(sender, targetPlayer.getUniqueId(), args[2]);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player not found.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /rupiah remove <player_name> <jumlah>");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    }
                    break;
                case "cek":
                    if (sender.hasPermission("vmcs.rupiah.cek")) {
                        if (args.length == 2) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);
                            if (targetPlayer != null) {
                                handleCek(sender, targetPlayer.getUniqueId());
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player not found.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /rupiah cek <player_name>");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    }
                    break;
                case "reset":
                    if (sender.hasPermission("vmcs.rupiah.reset")) {
                        if (args.length == 2) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);
                            if (targetPlayer != null) {
                                handleReset(sender, targetPlayer.getUniqueId());
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player not found.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /rupiah reset <player_name>");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    }
                    break;
                case "reload":
                    if (sender.hasPermission("vmcs.rupiah.reload")) {
                        handleReload(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    }
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown command. Type /rupiah help for a list of commands.");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
        }
        return true;
    }

    private void helprupiah(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Rupiah Commands:");
        sender.sendMessage(ChatColor.YELLOW + "/rupiah help - Show help");
        sender.sendMessage(ChatColor.YELLOW + "/rupiah add <player_name> <jumlah> - Add rupiah to players");
        sender.sendMessage(ChatColor.YELLOW + "/rupiah cek <player_name> - Check player's rupiah");
        sender.sendMessage(ChatColor.YELLOW + "/rupiah remove <player_name> <jumlah> - Remove rupiah from players");
        sender.sendMessage(ChatColor.YELLOW + "/rupiah reset <player_name> - Reset rupiah for players");
        sender.sendMessage(ChatColor.YELLOW + "/rupiah reload - Reload player data and placeholders");
    }

    private void handleAdd(CommandSender sender, UUID playerId, String amountStr) {
        int amount = Integer.parseInt(amountStr);
        int currentRupiah = playerRupiahMap.getOrDefault(playerId, 0);
        playerRupiahMap.put(playerId, currentRupiah + amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " rupiah to " + Bukkit.getPlayer(playerId).getName());
        savePlayerData(playerId);
        updatePlaceholder(playerId);
    }

    private void handleRemove(CommandSender sender, UUID playerId, String amountStr) {
        int amount = Integer.parseInt(amountStr);
        int currentRupiah = playerRupiahMap.getOrDefault(playerId, 0);
        if (currentRupiah - amount < 0) {
            sender.sendMessage(ChatColor.RED + "Cannot remove that much rupiah. Player only has " + currentRupiah);
        } else {
            playerRupiahMap.put(playerId, currentRupiah - amount);
            sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " rupiah from " + Bukkit.getPlayer(playerId).getName());
            savePlayerData(playerId);
            updatePlaceholder(playerId);
        }
    }

    private void handleCek(CommandSender sender, UUID playerId) {
        int currentRupiah = playerRupiahMap.getOrDefault(playerId, 0);
        sender.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(playerId).getName() + " has " + currentRupiah + " rupiah.");
    }

    private void handleReset(CommandSender sender, UUID playerId) {
        playerRupiahMap.put(playerId, 0);
        sender.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(playerId).getName() + "'s rupiah has been reset to 0.");
        savePlayerData(playerId);
        updatePlaceholder(playerId);
    }

    private void handleReload(CommandSender sender) {
        loadPlayerData();
        sender.sendMessage(ChatColor.GREEN + "Player data and placeholders have been reloaded.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlaceholder(player.getUniqueId());
        }
    }

    public void updatePlaceholder(UUID playerId) {
        PlaceholderRegistration placeholderExpansion = new PlaceholderRegistration(this);
        placeholderExpansion.onPlaceholderRequest(Bukkit.getPlayer(playerId), "rupiah");
        placeholderExpansion.onPlaceholderRequest(Bukkit.getPlayer(playerId), "rupiah_formated");
    }

    public void savePlayerData(UUID playerId) {
        File playerFile = new File(dataFolder, playerId.toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        playerConfig.set("rupiah", playerRupiahMap.getOrDefault(playerId, 0));

        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerData() {
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                UUID playerId = UUID.fromString(file.getName().replace(".yml", ""));
                FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
                int rupiah = playerConfig.getInt("rupiah", 0);
                playerRupiahMap.put(playerId, rupiah);
            }
        }
    }

    public int getRupiah(UUID playerId) {
        return playerRupiahMap.getOrDefault(playerId, 0);
    }

    public String getFormattedRupiah(int amount) {
        if (amount < 1000) return amount + " p";
        if (amount < 1000000) return amount / 1000 + " k";
        if (amount < 1000000000) return amount / 1000000 + " jt";
        return amount / 1000000000 + " m";
    }
}