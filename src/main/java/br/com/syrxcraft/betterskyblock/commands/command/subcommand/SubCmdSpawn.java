package br.com.syrxcraft.betterskyblock.commands.command.subcommand;

import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import org.bukkit.command.CommandSender;

@HasSubCommand
public class SubCmdSpawn implements ISubCommand {

    @CSubCommand(name = "spawn", targetCommand = "island2")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {
        commandSender.sendMessage("SubCommand");
        return true;
    }

}
