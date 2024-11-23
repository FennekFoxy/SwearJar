package com.Fennek.swearJar.events;

import com.Fennek.swearJar.ClassManager;
import com.Fennek.swearJar.SwearJar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {

    private final ClassManager classManager;


    public ChatListener(ClassManager classManager) {
        this.classManager = classManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check if player has the bypass permission
        if (player.hasPermission("swearjar.bypass")) {
            return; // Skip if the player has permission to bypass
        }

        String message = event.getMessage().toLowerCase();
        double playerBalance = classManager.getEcon().getBalance(player);
        List<String> swearWords = classManager.getSwearWords();
        Double fineAmount = classManager.getFineAmount();
        Double totalFinesCollected = classManager.getTotalFinesCollected();

        for (String word : swearWords) {
            if (message.contains(word.toLowerCase())) {
                double amountTaken;

                // Deduct money
                if (classManager.getEcon().has(player, fineAmount)) {
                    classManager.getEcon().withdrawPlayer(player, fineAmount);
                    amountTaken = fineAmount;
                    player.sendMessage("You used a bad word! You've been fined $" + fineAmount);
                } else {
                    classManager.getEcon().withdrawPlayer(player, playerBalance);
                    amountTaken = playerBalance;
                    player.sendMessage("You used a bad word, but you don't have enough money to pay the fine. So we took all of your money!");
                }

                // Only total fines if fines are enabled in the config
                if (SwearJar.getPlugin().getConfig().getBoolean("total_fines_collected_enabled")) {
                    totalFinesCollected += amountTaken;
                }
            }
        }
    }
}
