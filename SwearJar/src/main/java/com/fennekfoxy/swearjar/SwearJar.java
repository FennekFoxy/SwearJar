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

        // Register the chat listener
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("SwearJar enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SwearJar disabled!");
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
                // Deduct money
                if (econ.has(player, fineAmount)) {
                    econ.withdrawPlayer(player, fineAmount);
                    player.sendMessage("You used a bad word! You've been fined $" + fineAmount);
                } else {
                    econ.withdrawPlayer(player, playerBalance);
                    player.sendMessage("You used a bad word, but you don't have enough money to pay the fine. So we took all of your money!");
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
            sender.sendMessage("Usage: /swearjar filter [add:remove:list] <word> | /swearjar cost <value>");
            return true;
        }

        if (args[0].equalsIgnoreCase("filter")) {
            if (args.length == 1 || args[1].equalsIgnoreCase("list")) {
                // Handle the "list" command to display all filtered words
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
                sender.sendMessage("Usage: /swearjar cost <value>");
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

        } else {
            sender.sendMessage("Unknown command. Use: /swearjar filter [add:remove:list] or /swearjar cost");
        }

        return true;
    }
}