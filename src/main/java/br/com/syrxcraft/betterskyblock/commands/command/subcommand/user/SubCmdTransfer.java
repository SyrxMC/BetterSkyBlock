package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.core.data.DataStore;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import br.com.syrxcraft.betterskyblock.utils.FancyText;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args[0] == null) {
            FancyText.sendTo(player, new FancyText("§5§l ▶ §6/" + command + " transfer <newOwner>", "§bTransfere a ilha de um jogador para outro jogador!\n", "/" + command + " transfer ", true));
            return true;
        }
        
        Island currentIs = IslandUtils.getCurrentIsland(player);
        Island island = IslandUtils.getPlayerIsland(player);
        if(currentIs != null && currentIs.getPermissionHolder().getEffectivePermission(player.getUniqueId()) == PermissionType.OWNER){
            island = currentIs;
        }

        if (island == null) {
            player.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/" + command + " spawn\"");
            return false;
        }

        World islandWorld = BetterSkyBlock.getInstance().getIslandWorld();
        DataStore datastore = BetterSkyBlock.getInstance().getDataStore();
        
        Player target = Bukkit.getPlayer(args[0]);
        PlayerData newOwner = GriefDefender.getCore().getPlayerData(islandWorld.getUID(), target.getUniqueId()).orElse(null);

        if (newOwner == null || target == null) {
            sender.sendMessage("§4§l ▶ §cNão existe nenhum jogador chamado [" + args[1] + "] !");
            return false;
        }
        Island newOwnerIsland = datastore.getIsland(newOwner.getUniqueId());

        if (newOwnerIsland != null) {
            sender.sendMessage("§4§l ▶ §e" + newOwner.getName() + "§e já possui uma ilha nesse servidor! Delete a ilha dele antes de dar uma nova!");
            return true;
        }

        if (!island.ready) {
            sender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha! Transferência bloqueada!");
            return true;
        }
        String conf = BetterSkyBlock.getInstance().getCommandManager().getConfirmations().remove(sender.getName());

        if (conf == null || !conf.equals("transfer")) {
            sender.sendMessage("§4§l ▶ §c§lCUIDADO: §cSua ilha inteira será transferida!\n§cSe você tem certeza disso, use \"/" + command + " transfer\" novamente!");
            BetterSkyBlock.getInstance().getCommandManager().getConfirmations().put(sender.getName(), "transfer");
            return false;
        }
        try {
            datastore.removeIsland(island);
            datastore.transferIslandClaim(island, newOwner.getUniqueId());
            island.getPermissionHolder().updatePermission(player.getUniqueId(), PermissionType.MEMBER);
            island.getPermissionHolder().updatePermission(newOwner.getUniqueId(), PermissionType.OWNER);
            Island newIsland = new Island(island.getIslandId(), newOwner.getUniqueId(), island.getClaim(), island.getPermissionHolder(), island.getSpawn());
            datastore.addIslandAndQueueUpdate(newIsland);
        } catch (Exception e) {
            sender.sendMessage("§c§l ▶ §cFalha ao transferir ilha...§oMesangem: " + e.getMessage());
            BetterSkyBlock.getInstance().getLoggerHelper().info("Failed to transfer island from [" + player.getName() + "] to [" + newOwner.getName());
            e.printStackTrace();
        }

        sender.sendMessage("§2§l ▶ §aIlha transferida com sucesso do jogador [§e" + player.getName() + "§a] para o jogador [§e" + newOwner.getName() + "§a]!");
        target.sendMessage("§2§l ▶ §aIlha transferida com sucesso do jogador [§e" + player.getName() + "§a] para o jogador [§e" + newOwner.getName() + "§a]!");
        return true;
    }
}