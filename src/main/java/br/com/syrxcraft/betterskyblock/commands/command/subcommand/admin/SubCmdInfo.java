package br.com.syrxcraft.betterskyblock.commands.command.subcommand.admin;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.api.BetterSkyBlockAPI;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.FancyText;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import com.griefdefender.claim.GDClaim;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/12/2020
 */
@HasSubCommand
public class SubCmdInfo implements ISubCommand {

    @cSubCommand(name = "info", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {
        if (!commandSender.hasPermission(PermissionNodes.COMMAND_INFO)) {
            return CommandManager.noPermission(commandSender);
        }
        UUID uuid;

        Player player = (Player) commandSender;
        if (args.length == 0) {
            uuid = player.getUniqueId();
        } else {
            uuid = Bukkit.getPlayerUniqueId(args[0]);
        }
        PlayerData playerData = GriefDefender.getCore().getPlayerData(BetterSkyBlock.getInstance().getIslandWorld().getUID(), uuid).orElse(null);

        if (playerData == null) {
            commandSender.sendMessage("§c§l ▶ §cNão existem nenhum jogador chamado [" + args[0] + "] !");
            return true;
        }
        BetterSkyBlock instance = BetterSkyBlock.getInstance();
        BetterSkyBlockAPI api = instance.getBetterSkyBlockAPI();

        Island island = api.getPlayerIsland(uuid);

        if (island == null) {
            commandSender.sendMessage("§c§l ▶ §e" + Bukkit.getPlayer(uuid).getName() + "§c não possui uma ilha nesse servidor!");
            return true;
        }

        GDClaim claim = (GDClaim) island.getClaim();
        player.sendMessage("§7(§m---- §3Ilha de " + island.getOwnerName() + "§7§m----§r§f)");
        FancyText.sendTo(player, new FancyText("§fCriada em: §b" + Utils.getFormatedDate(claim.getData().getDateCreated())));
        FancyText.sendTo(player, new FancyText("Expira: " + (claim.getData().allowExpiration() ? "§aSim" : "§cNão expira")));
        FancyText.sendTo(player, new FancyText("Dono: §b" + claim.getOwnerName()));
        FancyText.sendTo(player, new FancyText("UUID: §b" + claim.getUniqueId()));
        FancyText.sendTo(player, new FancyText("Jogadores: §b" + claim.getPlayers().size(), String.join("\n", Utils.getPlayersNameByUUID(claim.getPlayers()))));
        FancyText.sendTo(player, new FancyText("Estado: " + (api.isIslandPublic(island) ? "§aPública" : "§cPrivada")));
        FancyText.sendTo(player, new FancyText("Bioma: §b" + island.getSpawn().getBlock().getBiome().name()));
        FancyText.sendTo(player, new FancyText("Spawn: §5[TELEPORTAR]", "Clique para teleportar", "/is spawn " + island.getOwnerName(), false));
        return false;
    }
}