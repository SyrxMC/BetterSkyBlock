package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

@HasSubCommand
public class SubCmdSetSpawn implements ISubCommand {

    @CSubCommand(name = "setspawn", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        if(!player.hasPermission(PermissionNodes.COMMAND_SET_SPAWN)){
            return CommandManager.noPermission(player);
        }

        player.sendMessage(ChatColor.RED + "ATENÇAO: Certifique-se de usar blocos inteiros para o spawn de sua ilha! Não use escadas ou lajes!");

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null) {
            player.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/"+label+" spawn\"");
            return false;
        }

        if (!island.getClaim().getPlayers().contains(player.getUniqueId())){
            player.sendMessage("§4§l ▶ §cVocê precisa estar dentro da sa ilha para usar esse comando!");
            return false;
        }

        if (!island.ready) {
            player.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha!");
            return false;
        }

        try {
            island.setSpawn(player.getLocation().add(0, 1.5, 0));
            player.sendMessage("§3§l ▶ §aSpawn da ilha definido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + " An error occurred while creating the island: data store issue.");
            return false;
        }
        return true;
    }
}
