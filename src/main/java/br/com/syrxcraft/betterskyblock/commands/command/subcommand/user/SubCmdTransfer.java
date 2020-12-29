package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import br.com.syrxcraft.betterskyblock.utils.FancyText;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import org.bukkit.Bukkit;
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
        if (args[0] == null && args[1] == null) {
            FancyText.sendTo(sender, new FancyText("§5§l ▶ §6/" + command + " transfer <oldOwner> <newOwner>", "§bTransfere a ilha de um jogador para outro jogador!\n", "/" + command + " transfer ", true));
            return true;
        }

        BetterSkyBlock instance = BetterSkyBlock.getInstance();
        UUID targetUUID = Bukkit.getPlayerUniqueId(args[1]);
        PlayerData oldOwner = GriefDefender.getCore().getPlayerData(instance.getIslandWorld().getUID(), Bukkit.getPlayerUniqueId(args[0])).orElse(null);
        PlayerData newOwner = GriefDefender.getCore().getPlayerData(instance.getIslandWorld().getUID(), targetUUID).orElse(null);

        if (oldOwner == null) {
            sender.sendMessage("§4§l ▶ §cNão existe nenhum jogador chamado [" + args[0] + "] !");
            return false;
        }
        Player target = Bukkit.getPlayer(targetUUID);
        if (newOwner == null || target == null) {
            sender.sendMessage("§4§l ▶ §cNão existe nenhum jogador chamado [" + args[1] + "] !");
            return false;
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
            sender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha! Transferência bloqueada!");
            return true;
        }
        String conf = BetterSkyBlock.getInstance().getCommandManager().getConfirmations().remove(sender.getName());

        if (conf == null || !conf.equals("transfer")) {
            sender.sendMessage("§4§l ▶ §c§lCUIDADO: §cSua ilha inteira será transferida!\n§cSe você tem certeza disso, use \"/"+command+" transfer\" novamente!");
            BetterSkyBlock.getInstance().getCommandManager().getConfirmations().put(sender.getName(), "transfer");
            return false;
        }
        try {
            instance.getDataStore().removeIsland(oldOwnerIsland);
            instance.getDataStore().transferIslandClaim(oldOwnerIsland, newOwner.getUniqueId());
            oldOwnerIsland.getPermissionHolder().updatePermission(oldOwner.getUniqueId(), PermissionType.MEMBER);
            oldOwnerIsland.getPermissionHolder().updatePermission(newOwner.getUniqueId(), PermissionType.OWNER);
            Island newIsland = new Island(oldOwnerIsland.getIslandId(), newOwner.getUniqueId(), oldOwnerIsland.getClaim(), oldOwnerIsland.getPermissionHolder(), oldOwnerIsland.getSpawn());
            instance.getDataStore().addIslandAndQueueUpdate(newIsland);
        } catch (Exception e) {
            sender.sendMessage("§c§l ▶ §cFalha ao transferir ilha...§oMesangem: " + e.getMessage());
            instance.getLoggerHelper().info("Failed to transfer island from [" + oldOwner.getName() + "] to [" + newOwner.getName());
            e.printStackTrace();
        }

        sender.sendMessage("§2§l ▶ §aIlha transferida com sucesso do jogador [§e" + oldOwner.getName() + "§a] para o jogador [§e" + newOwner.getName() + "§a]!");
        target.sendMessage("§2§l ▶ §aIlha transferida com sucesso do jogador [§e" + oldOwner.getName() + "§a] para o jogador [§e" + newOwner.getName() + "§a]!");
        return true;
    }
}