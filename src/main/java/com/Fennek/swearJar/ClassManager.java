package com.Fennek.swearJar;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class ClassManager {

    private static Economy econ = null;
    private List<String> swearWords = new ArrayList<>();
    private double fineAmount;
    private double totalFinesCollected = 0.0;

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
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

    public void loadConfigValues() {
        FileConfiguration config = SwearJar.getPlugin().getConfig();
        swearWords = config.getStringList("words");
        fineAmount = config.getDouble("amount");
    }

    public Economy getEcon(){
        return econ;
    }

    public void logMessage(String playerName, String message) {
        File logFile = new File(SwearJar.getPlugin().getDataFolder(), "messages.log");
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(playerName + " - " + message + " (" + new Date() + ")\n");
        } catch (IOException e) {
            getLogger().severe("Could not write to messages.log: " + e.getMessage());
        }
    }

    public void clearLogFile(Player player) {
        File logFile = new File(SwearJar.getPlugin().getDataFolder(), "messages.log");
        if (logFile.exists()) {
            if (logFile.delete()) {
                player.sendMessage("The log file has been cleared.");
            } else {
                player.sendMessage("Failed to clear the log file.");
            }
        } else {
            player.sendMessage("There is no log file to clear.");
        }
    }

    public List<String> getSwearWords(){
        return new ArrayList<>(swearWords);
    }

    public double getTotalFinesCollected(){
        return totalFinesCollected;
    }

    public double getFineAmount(){
        return fineAmount;
    }

    public void setFineAmount(double fine) {
        fineAmount = fine;
    }

    public void setTotalFinesCollected(double fines){
        totalFinesCollected = fines;
    }
}
