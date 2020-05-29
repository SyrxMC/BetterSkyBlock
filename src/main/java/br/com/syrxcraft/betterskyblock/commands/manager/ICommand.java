package br.com.syrxcraft.betterskyblock.commands.manager;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface ICommand extends CommandExecutor {

    default boolean deliverSubCommand(CommandSender commandSender, Command command, String label, String[] args){
        return BetterSkyBlock.getInstance().getCommandManager().deliverSubCommand(commandSender, command.getName(), label, args);
    }

}
