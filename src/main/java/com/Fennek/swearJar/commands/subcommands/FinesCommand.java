package com.Fennek.swearJar.commands.subcommands;

import com.Fennek.swearJar.ClassManager;
import com.Fennek.swearJar.SwearJar;
import com.Fennek.swearJar.commands.SubCommand;
import org.bukkit.entity.Player;

public class FinesCommand extends SubCommand {

    private final ClassManager classManager;

    public FinesCommand(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String getName() {
        return "fines";
    }

    @Override
    public String getDescription() {
        return "Allows user to view, empty, or toggle the total number of fines collected";
    }

    @Override
    public String getSyntax() {
        return "/swearjar fines [show:empty:toggle]";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(player.hasPermission("swearjar.admin.fines")){
            if (args.length < 2) {
                player.sendMessage("Usage: /swearjar fines [show:empty:toggle]");
            }
            if (args[1].equalsIgnoreCase("show")) {
                // Show the total fines collected
                player.sendMessage("Total fines collected: $" + classManager.getTotalFinesCollected());
            } else if (args[1].equalsIgnoreCase("empty")) {
                // Reset the total fines to 0
                classManager.setTotalFinesCollected(0.0);
                SwearJar.getPlugin().getConfig().set("total_fines_collected", classManager.getTotalFinesCollected());
                SwearJar.getPlugin().saveConfig();
                player.sendMessage("Total fines collected has been reset to $0.");
            } else if (args[1].equalsIgnoreCase("toggle")) {
                // Toggle the total_fines_collected_enabled config value
                boolean finesEnabled = SwearJar.getPlugin().getConfig().getBoolean("total_fines_collected_enabled");
                finesEnabled = !finesEnabled; // Toggle the value
                SwearJar.getPlugin().getConfig().set("total_fines_collected_enabled", finesEnabled);
                SwearJar.getPlugin().saveConfig();
                player.sendMessage("Fines collection has been " + (finesEnabled ? "enabled" : "disabled") + ".");
            } else {
                player.sendMessage("Unknown subcommand. Use: /swearjar fines [show:empty:toggle]");
            }
        }

    }
}
