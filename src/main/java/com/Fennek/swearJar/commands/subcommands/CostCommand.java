package com.Fennek.swearJar.commands.subcommands;

import com.Fennek.swearJar.ClassManager;
import com.Fennek.swearJar.SwearJar;
import com.Fennek.swearJar.commands.SubCommand;
import org.bukkit.entity.Player;

public class CostCommand extends SubCommand {

    private final ClassManager classManager;

    public CostCommand(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String getName() {
        return "cost";
    }

    @Override
    public String getDescription() {
        return "Allows user to view and change the current punishment cost";
    }

    @Override
    public String getSyntax() {
        return "/swearjar cost <value>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(player.hasPermission("swearjar.admin.cost")){
            if (args.length < 2) {
                player.sendMessage("The current fine cost is $" + classManager.getFineAmount());
            }
            try {
                double newCost = Double.parseDouble(args[1]);
                classManager.setFineAmount(newCost);
                SwearJar.getPlugin().getConfig().set("amount", classManager.getFineAmount());
                SwearJar.getPlugin().saveConfig();
                player.sendMessage("Swear fine cost updated to $" + classManager.getFineAmount());
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid cost value. Please enter a valid number.");
            }
        }
    }
}
