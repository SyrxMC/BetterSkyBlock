package br.com.syrxcraft.betterskyblock.commands.command.subcommand.admin;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

@HasSubCommand
public class SubCmdDelete implements ISubCommand {

    @cSubCommand(name = "delete", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!commandSender.hasPermission(PermissionNodes.COMMAND_DELETE_OTHER)) {
            return CommandManager.noPermission(commandSender);
        }
        if (!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        UUID uuid;
        if (args.length == 0) {
            uuid = player.getUniqueId();
        } else uuid = Bukkit.getPlayerUniqueId(args[0]);
        PlayerData playerData = GriefDefender.getCore().getPlayerData(BetterSkyBlock.getInstance().getIslandWorld().getUID(), uuid).orElse(null);

        if (playerData == null) {
            commandSender.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + args[0] + "] !");
            return true;
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(uuid);

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §e" + Bukkit.getPlayer(uuid).getName() + "§c não possui uma ilha nesse servidor!");
            return true;
        }

        try {
            island.delete();
        } catch (SQLException exception) {
            commandSender.sendMessage("§4§l ▶ §cEssa ilha não pode ser deletada!");
            BetterSkyBlock.getInstance().getLoggerHelper().error(exception.getMessage());
            return true;
        }

        commandSender.sendMessage("§2§l ▶ §aIlha deletada com sucesso!");
        return true;
    }
}
