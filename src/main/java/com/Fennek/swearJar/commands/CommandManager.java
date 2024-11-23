package com.Fennek.swearJar.commands;

import com.Fennek.swearJar.ClassManager;
import com.Fennek.swearJar.commands.subcommands.FilterCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private List<SubCommand> subcommands = new ArrayList<>();
    private ClassManager classManager;


    public CommandManager(){
        this.classManager = classManager;
        subcommands.add(new FilterCommand(classManager));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length > 0){
                for(int i = 0; i < getSubcommands().size(); i++){
                    if(args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                        getSubcommands().get(i).perform(player, args);
                    }
                }
            }else if (args.length == 0 || args[1].equalsIgnoreCase("help")){
                player.sendMessage("--------------------------------------");
                for(int i = 0; i < getSubcommands().size(); i++){
                    player.sendMessage(ChatColor.GREEN + getSubcommands().get(i).getSyntax() + " - " + getSubcommands().get(i).getDescription());
                }
                player.sendMessage("--------------------------------------");
            }
        }
        return true;
    }
    public List<SubCommand> getSubcommands(){
        return subcommands;
    }
}

