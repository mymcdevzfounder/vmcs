package id.mcdevz.vmcs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.plugin.java.JavaPlugin;

public class mainactivity extends JavaPlugin {

    private static mainactivity instance;

    @Override
    public void onEnable() {
        instance = this; // Menyimpan instance dari plugin

        // Register the command executor for /rupiah
        this.getCommand("rupiah").setExecutor(new RupiahCommand(this));

        // Check if PlaceholderAPI is installed, and register placeholders
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderRegistration(new RupiahCommand(this)).register();
            getLogger().info("PlaceholderAPI found and placeholders registered.");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }

        getLogger().info("Rupiah Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VelouraShop Plugin Disabled!");
    }

    // Tambahkan metode getInstance() untuk mengatasi error
    public static mainactivity getInstance() {
        return instance; // Mengembalikan instance dari plugin
    }
}