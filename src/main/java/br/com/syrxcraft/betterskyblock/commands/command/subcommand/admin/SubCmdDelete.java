package br.com.syrxcraft.betterskyblock.commands.command.subcommand.admin;

import br.com.finalcraft.evernifecore.FCBukkitUtil;
import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.islands.Island;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import com.griefdefender.event.GDRemoveClaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@HasSubCommand
public class SubCmdDelete implements ISubCommand {

    @CSubCommand(name = "delete", targetCommand = "island2")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!commandSender.hasPermission(PermissionNodes.COMMAND_DELETE_OTHER)){
            return true;
        }

        PlayerData playerData = GriefDefender.getCore().getPlayerData(BetterSkyBlock.getInstance().getIslandWorld().getUID(), Bukkit.getPlayerUniqueId(args[0])).orElse(null);


        if (playerData == null){
            commandSender.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + args[0] + "] !");
            return true;
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(Bukkit.getPlayerUniqueId(args[0]));

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §e" + Bukkit.getPlayer(args[0]).getName()  + "§c não possui uma ilha nesse servidor!");
            return true;
        }

        GDRemoveClaimEvent.Delete event = new GDRemoveClaimEvent.Delete(island.getClaim());
        event.shouldRestore(false);

        GriefDefender.getEventManager().post(event);

        if (event.cancelled()) {
            commandSender.sendMessage("§4§l ▶ §cEssa ilha não pode ser deletada!");
            return true;
        }

        commandSender.sendMessage("§2§l ▶ §aIlha deletada com sucesso!");
        return true;
    }
}
