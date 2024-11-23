package com.Fennek.swearJar.commands.subcommands;

import com.Fennek.swearJar.ClassManager;
import com.Fennek.swearJar.SwearJar;
import com.Fennek.swearJar.commands.SubCommand;
import org.bukkit.entity.Player;

public class FilterCommand extends SubCommand {
    
    private final ClassManager classManager;

    public FilterCommand(ClassManager classManager) {
        this.classManager = classManager;
    }


    @Override
    public String getName() {
        return "filter";
    }

    @Override
    public String getDescription() {
        return "Allows user to add/remove word from the list or view the current list";
    }

    @Override
    public String getSyntax() {
        return "/swearjar filter [add:remove:list] <word>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(player.hasPermission("swearjar.admin.filter")){
            if (args.length == 1 || args[1].equalsIgnoreCase("list")) {
                if (classManager.getSwearWords().isEmpty()) {
                    player.sendMessage("There are no words in the filter.");
                } else {
                    player.sendMessage("Filtered words: " + String.join(", ", classManager.getSwearWords()));
                }
            }

            if (args.length < 3) {
                player.sendMessage("Usage: /swearjar filter [add:remove] <word>");
            }
            String action = args[1].toLowerCase();
            String word = args[2].toLowerCase();

            if (action.equals("add")) {
                if (classManager.getSwearWords().contains(word)) {
                    player.sendMessage("Word is already in the filter.");
                } else {
                    classManager.getSwearWords().add(word);
                    SwearJar.getPlugin().getConfig().set("words", classManager.getSwearWords());
                    SwearJar.getPlugin().saveConfig();
                    player.sendMessage("Added '" + word + "' to the filter.");
                }
            } else if (action.equals("remove")) {
                if (!classManager.getSwearWords().contains(word)) {
                    player.sendMessage("Word is not in the filter.");
                } else {
                    classManager.getSwearWords().remove(word);
                    SwearJar.getPlugin().getConfig().set("words", classManager.getSwearWords());
                    SwearJar.getPlugin().saveConfig();
                    player.sendMessage("Removed '" + word + "' from the filter.");
                }
            } else {
                player.sendMessage("Invalid action! Use 'add', 'remove', or 'list'.");
            }
        }
    }
}
