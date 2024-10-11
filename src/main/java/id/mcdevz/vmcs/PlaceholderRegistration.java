package id.mcdevz.vmcs;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderRegistration extends PlaceholderExpansion {

    private final RupiahCommand rupiahCommand;

    public PlaceholderRegistration(RupiahCommand rupiahCommand) {
        this.rupiahCommand = rupiahCommand;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vmcs";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MCDevz";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // %vmcs_rupiah%
        if (identifier.equals("rupiah")) {
            int rupiah = rupiahCommand.getRupiah(player.getUniqueId()); // Menggunakan UUID
            return String.valueOf(rupiah);
        }

        // %vmcs_rupiah_formated%
        if (identifier.equals("rupiah_formated")) {
            int rupiah = rupiahCommand.getRupiah(player.getUniqueId()); // Menggunakan UUID
            return rupiahCommand.getFormattedRupiah(rupiah);
        }

        return null; // Placeholder not found
    }

    // Metode untuk memperbarui placeholder secara otomatis
    public void updatePlaceholders(Player player) {
        if (player != null) {
            onPlaceholderRequest(player, "rupiah");
            onPlaceholderRequest(player, "rupiah_formated");
        }
    }
}