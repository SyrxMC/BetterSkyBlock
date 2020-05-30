package br.com.syrxcraft.betterskyblock.commands.command;

import br.com.syrxcraft.betterskyblock.commands.manager.CCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ICommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CCommand(label = "island", aliases = {"is", "isl", "bsis"})
public class IsCommand implements ICommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0){
            return deliverSubCommand(sender, command, "help", args);
        }

        switch (args[0].toLowerCase()){
            case "": case "?": case "help":
                return deliverSubCommand(sender, command, "help", args);
            case "tp": case "home": case "spawn":
                return deliverSubCommand(sender, command, "spawn", args);
            case "setspawn":
                return deliverSubCommand(sender, command, "setspawn", args);
            case "biomelist":
                return deliverSubCommand(sender, command, "biomelist", args);
            case "setbiome":
                return deliverSubCommand(sender, command, "setbiome", args);
            case "private":
                return deliverSubCommand(sender, command, "private", args);
            case "public":
                return deliverSubCommand(sender, command, "public", args);
            case "reset":
                return deliverSubCommand(sender, command, "reset", args);
            case "delete":
                return deliverSubCommand(sender, command, "delete", args);
            case "setradius":
                return deliverSubCommand(sender, command, "setradius", args);
            case "reload":
                return deliverSubCommand(sender, command, "reload", args);
            default:{
                sender.sendMessage("§cErro de parametros, por favor use /" + label + " help");
                return false;
            }

        }
    }

}
