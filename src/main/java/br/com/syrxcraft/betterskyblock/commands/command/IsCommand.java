package br.com.syrxcraft.betterskyblock.commands.command;

import br.com.syrxcraft.betterskyblock.commands.manager.ICommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@cCommand(label = "island", aliases = {"is", "isl", "bsis"})
public class IsCommand implements ICommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            return deliverSubCommand(sender, command, "help", args);
        }

        switch (args[0].toLowerCase()) {
            case "":
            case "?":
            case "help":
                return deliverSubCommand(sender, command, "help", args);
            case "tp":
            case "home":
            case "spawn":
                return deliverSubCommand(sender, command, "spawn", args);
            case "setspawn":
                return deliverSubCommand(sender, command, "setspawn", args);
            case "biomelist":
                return deliverSubCommand(sender, command, "biomelist", args);
            case "setbiome":
                return deliverSubCommand(sender, command, "setbiome", args);
            case "private":
            case "privada":
                return deliverSubCommand(sender, command, "private", args);
            case "public":
            case "publica":
            case "pública":
                return deliverSubCommand(sender, command, "public", args);
            case "reset":
                return deliverSubCommand(sender, command, "reset", args);
            case "delete":
                return deliverSubCommand(sender, command, "delete", args);
            case "reload":
                return deliverSubCommand(sender, command, "reload", args);
            case "info":
                return deliverSubCommand(sender, command, "info", args);
            case "invite":
                return deliverSubCommand(sender, command, "invite", args);
            case "kick":
                return deliverSubCommand(sender, command, "kick", args);
            case "member":
                return deliverSubCommand(sender, command, "member", args);
            case "entry":
                return deliverSubCommand(sender, command, "entry", args);
            case "manager":
                return deliverSubCommand(sender, command, "manager", args);
            case "transfer":
            case "transferir":
                return deliverSubCommand(sender, command, "transfer", args);
            default: {
                sender.sendMessage("§cErro de parametros, por favor use /" + label + " help");
                return false;
            }

        }
    }

}
