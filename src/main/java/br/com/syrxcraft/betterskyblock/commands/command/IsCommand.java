package br.com.syrxcraft.betterskyblock.commands.command;

import br.com.syrxcraft.betterskyblock.commands.manager.CCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ICommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CCommand(label = "island2", aliases = {"is2", "isl2", "bsis2"})
public class IsCommand implements ICommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage("Command");
        System.out.println(label);

        if(label.equalsIgnoreCase("is2")){
            return deliverSubCommand(sender, command, "spawn", args);
        }

        return false;
    }

}
