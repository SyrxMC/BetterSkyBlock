package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@HasSubCommand
public class SubCmdSetBiome implements ISubCommand {

    @CSubCommand(name = "setbiome", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        System.out.println(Arrays.asList(args));

        if (!commandSender.hasPermission(PermissionNodes.OPTIONS_SET_BIOME_BASE)){
            return CommandManager.noPermission(commandSender);
        }

        Player player = null;

        if (commandSender.hasPermission(PermissionNodes.COMMAND_SET_BIOME_OTHER)){

            if(args.length >= 2 && args[1] != null && !args[1].isEmpty()){

                Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(Bukkit.getPlayerUniqueId(args[1]));
                player = Bukkit.getPlayer(args[1]);

                if (island == null){
                    commandSender.sendMessage("§4§l ▶ §cO jogador [" + args[1] + "] não possui uma ilha!");
                    return true;
                }
            }

        }

        if (player == null && (commandSender instanceof Player) ){
            player = (Player) commandSender;
        }

        Biome biome = Utils.matchAllowedBiome(args[0]);

        if (biome == null) {
            commandSender.sendMessage("§4§l ▶ §cNão existe nenhum bioma chamado §e" + args[0] + ". Use /" + command + " biomelist");
            return true;
        }

        if (!commandSender.hasPermission(PermissionNodes.OPTIONS_SET_BIOME_ALL) && !commandSender.hasPermission(PermissionNodes.OPTIONS_SET_BIOME_BASE + "." + biome.toString().toLowerCase())){
            commandSender.sendMessage("§4§l ▶ §cVocê não pode utilizar esse bioma. §e" + args[0] + ". Use /" + command + " biomelist");
            return true;
        }

        if(player == null){
            commandSender.sendMessage("§4§l ▶ §cNenhum jogador encontrado.");
            return true;
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §e" + Bukkit.getPlayer(player.getUniqueId()).getName() + "§c não possui uma ilha nesse servidor!");
            return false;
        }

        if (!island.ready) {
            commandSender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha!");
            return false;
        }

        island.setIslandBiome(biome);

        commandSender.sendMessage("§3§l ▶ §aBioma alterado! Você vai precisar deslogar e logar para ver a diferença!");
        return true;
    }
}
