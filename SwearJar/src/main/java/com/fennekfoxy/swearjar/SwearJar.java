package com.fennekfoxy.swearjar;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SwearJar extends JavaPlugin implements Listener {

    private static Economy econ = null;
    private List<String> swearWords;
    private double fineAmount;
    private double totalFinesCollected = 0.0;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            this.getLogger().severe("Vault is not installed! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load config
        saveDefaultConfig();
        loadConfigValues();

        // Load total fines collected from the config
        totalFinesCollected = getConfig().getDouble("total_fines_collected", 0.0);

        // Register the chat listener
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("SwearJar enabled!");
    }


    @Override
    public void onDisable() {
        // Save the total fines collected to the config
        getConfig().set("total_fines_collected", totalFinesCollected);
        saveConfig();

        this.getLogger().info("SwearJar disabled!");
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault not found! Economy features will be disabled.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("No economy provider found! Economy features will be disabled.");
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        swearWords = config.getStringList("words");
        fineAmount = config.getDouble("amount");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check if player has the bypass permission
        if (player.hasPermission("swearjar.bypass")) {
            return; // Skip if the player has permission to bypass
        }

        String message = event.getMessage().toLowerCase();
        double playerBalance = econ.getBalance(player);

        for (String word : swearWords) {
            if (message.contains(word.toLowerCase())) {
                double amountTaken;

                // Deduct money
                if (econ.has(player, fineAmount)) {
                    econ.withdrawPlayer(player, fineAmount);
                    amountTaken = fineAmount;
                    player.sendMessage("You used a bad word! You've been fined $" + fineAmount);
                } else {
                    econ.withdrawPlayer(player, playerBalance);
                    amountTaken = playerBalance;
                    player.sendMessage("You used a bad word, but you don't have enough money to pay the fine. So we took all of your money!");
                }

                // Only total fines if fines are enabled in the config
                if (getConfig().getBoolean("total_fines_collected_enabled")) {
                    totalFinesCollected += amountTaken;
                }
                break;
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("swearjar.admin"))) {
            sender.sendMessage("You don't have permission to execute this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /swearjar filter [add:remove:list] <word> | /swearjar cost <value> | /swearjar fines [show:empty:toggle]");
            return true;
        }

        if (args[0].equalsIgnoreCase("filter")) {
            if (args.length == 1 || args[1].equalsIgnoreCase("list")) {
                if (swearWords.isEmpty()) {
                    sender.sendMessage("There are no words in the filter.");
                } else {
                    sender.sendMessage("Filtered words: " + String.join(", ", swearWords));
                }
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage("Usage: /swearjar filter [add:remove] <word>");
                return true;
            }
            String action = args[1].toLowerCase();
            String word = args[2].toLowerCase();

            if (action.equals("add")) {
                if (swearWords.contains(word)) {
                    sender.sendMessage("Word is already in the filter.");
                } else {
                    swearWords.add(word);
                    getConfig().set("words", swearWords);
                    saveConfig();
                    sender.sendMessage("Added '" + word + "' to the filter.");
                }
            } else if (action.equals("remove")) {
                if (!swearWords.contains(word)) {
                    sender.sendMessage("Word is not in the filter.");
                } else {
                    swearWords.remove(word);
                    getConfig().set("words", swearWords);
                    saveConfig();
                    sender.sendMessage("Removed '" + word + "' from the filter.");
                }
            } else {
                sender.sendMessage("Invalid action! Use 'add', 'remove', or 'list'.");
            }

        } else if (args[0].equalsIgnoreCase("cost")) {
            if (args.length < 2) {
                sender.sendMessage("The current fine cost is $" + fineAmount);
                return true;
            }
            try {
                double newCost = Double.parseDouble(args[1]);
                fineAmount = newCost;
                getConfig().set("amount", fineAmount);
                saveConfig();
                sender.sendMessage("Swear fine cost updated to $" + fineAmount);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid cost value. Please enter a valid number.");
            }
        } else if (args[0].equalsIgnoreCase("fines")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /swearjar fines [show:empty:toggle]");
                return true;
            }

            if (args[1].equalsIgnoreCase("show")) {
                // Show the total fines collected
                sender.sendMessage("Total fines collected: $" + totalFinesCollected);
            } else if (args[1].equalsIgnoreCase("empty")) {
                // Reset the total fines to 0
                totalFinesCollected = 0.0;
                getConfig().set("total_fines_collected", totalFinesCollected);
                saveConfig();
                sender.sendMessage("Total fines collected has been reset to $0.");
            } else if (args[1].equalsIgnoreCase("toggle")) {
                // Toggle the total_fines_collected_enabled config value
                boolean finesEnabled = getConfig().getBoolean("total_fines_collected_enabled");
                finesEnabled = !finesEnabled; // Toggle the value
                getConfig().set("total_fines_collected_enabled", finesEnabled);
                saveConfig();
                sender.sendMessage("Fines collection has been " + (finesEnabled ? "enabled" : "disabled") + ".");
            } else {
                sender.sendMessage("Unknown subcommand. Use: /swearjar fines [show:empty:toggle]");
            }

        } else {
            sender.sendMessage("Unknown command. Use: /swearjar filter [add:remove:list], /swearjar cost, or /swearjar fines [show:empty:toggle]");
        }

        return true;
    }
}
