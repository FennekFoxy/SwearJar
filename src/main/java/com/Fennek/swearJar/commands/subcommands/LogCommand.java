package com.Fennek.swearJar.commands.subcommands;

import com.Fennek.swearJar.ClassManager;
import com.Fennek.swearJar.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LogCommand extends SubCommand {

    private final ClassManager classManager;

    public LogCommand(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public String getDescription() {
        return "clears log";
    }

    @Override
    public String getSyntax() {
        return "/swearjar log clear";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(player.hasPermission("swearjar.admin.log")){
            if (args.length < 2) {
                player.sendMessage(ChatColor.GREEN + getSyntax());
            }

            if (args[1].equalsIgnoreCase("clear")) {
                classManager.clearLogFile(player);
            } else {
                player.sendMessage("Unknown subcommand. Use: /swearjar log [clear]");
            }
        }
    }
}
