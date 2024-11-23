package com.Fennek.swearJar;

import com.Fennek.swearJar.events.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SwearJar extends JavaPlugin {

    private static SwearJar plugin;
    private final ClassManager classManager;

    public SwearJar(ClassManager classManager) {
        this.classManager = classManager;
    }

    public static SwearJar getPlugin() {
        return plugin;
    }


    @Override
    public void onEnable() {
        plugin = this;
        if (!classManager.setupEconomy()) {
            this.getLogger().severe("Vault is not installed! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load config
        saveDefaultConfig();
        classManager.loadConfigValues();

        // Load total fines collected from the config
        Double totalFinesCollected = getConfig().getDouble("total_fines_collected", 0.0);

        // Register the chat listener
        getServer().getPluginManager().registerEvents(new ChatListener(classManager), this);
        this.getLogger().info("SwearJar enabled!");
    }

    @Override
    public void onDisable() {
        // Save the total fines collected to the config
        getConfig().set("total_fines_collected", classManager.getTotalFinesCollected());
        saveConfig();

        this.getLogger().info("SwearJar disabled!");
    }
}
