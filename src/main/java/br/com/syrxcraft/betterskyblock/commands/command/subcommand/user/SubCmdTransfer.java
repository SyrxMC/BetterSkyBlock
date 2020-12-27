package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.FancyText;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/12/2020
 */
@HasSubCommand
public class SubCmdTransfer implements ISubCommand {
    @cSubCommand(name = "transfer", targetCommand = "island")
    public boolean execute(CommandSender sender, String command, String label, String[] args) {
        if (!sender.hasPermission(PermissionNodes.COMMAND_TRANSFER)) {
            return CommandManager.noPermission(sender);
        }
        if (args[0] != null && !args[0].isEmpty() || args[1] != null && !args[1].isEmpty()) {
            FancyText.sendTo(sender, new FancyText("§5§l ▶ §6/" + label + " transfer <oldOwner> <newOwner>", "§bTransfere a ilha de um jogador para outro jogador!\n", "/" + label + " transfer ", true));
            return true;
        }
        BetterSkyBlock instance = BetterSkyBlock.getInstance();
        
        PlayerData oldOwner = GriefDefender.getCore().getPlayerData(instance.getIslandWorld().getUID(), Bukkit.getPlayerUniqueId(args[0])).orElse(null);
        PlayerData newOwner = GriefDefender.getCore().getPlayerData(instance.getIslandWorld().getUID(), Bukkit.getPlayerUniqueId(args[1])).orElse(null);

        if (oldOwner == null) {
            sender.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + args[0] + "] !");
            return true;
        }

        if (newOwner == null) {
            sender.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + args[1] + "] !");
            return true;
        }

        Island oldOwnerIsland = instance.getDataStore().getIsland(oldOwner.getUniqueId());
        Island newOwnerIsland = instance.getDataStore().getIsland(newOwner.getUniqueId());

        if (oldOwnerIsland == null) {
            sender.sendMessage("§4§l ▶ §e" + oldOwner.getName() + "§c não possui uma ilha nesse servidor!");
            return true;
        }

        if (newOwnerIsland != null) {
            sender.sendMessage("§4§l ▶ §e" + newOwner.getName() + "§e já possui uma ilha nesse servidor! Delete a ilha dele antes de dar uma nova!");
            return true;
        }

        if (!oldOwnerIsland.ready) {
            sender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha! Transferencia bloqueada!");
            return true;
        }

        try {
            instance.getDataStore().removeIsland(oldOwnerIsland);
            instance.getDataStore().transferIsland(oldOwnerIsland, newOwner.getUniqueId());
            Island newIsland = new Island(oldOwnerIsland.getIslandId(), newOwner.getUniqueId(), oldOwnerIsland.getClaim(), oldOwnerIsland.getPermissionHolder(), oldOwnerIsland.getSpawn());
            instance.getDataStore().addIsland(newIsland);
        } catch (Exception e) {
            sender.sendMessage("§c§l ▶ §cFalha ao transferir ilha...§oMesangem: " + e.getMessage());
            instance.getLoggerHelper().info("Failed to transfer island from [" + oldOwner.getName() + "] to [" + newOwner.getName());
            e.printStackTrace();
        }

        sender.sendMessage("§2§l ▶ §aIlha transferida com sucesso do jogador [§e" + oldOwner.getName() + "§a] para o jogador [§e" + newOwner.getName() + "§a]!");
        return true;
    }
}