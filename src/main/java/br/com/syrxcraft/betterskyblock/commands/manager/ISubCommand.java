package br.com.syrxcraft.betterskyblock.commands.manager;

import org.bukkit.command.CommandSender;

public interface ISubCommand{

    boolean execute(CommandSender commandSender, String command, String label, String[] args);

}
