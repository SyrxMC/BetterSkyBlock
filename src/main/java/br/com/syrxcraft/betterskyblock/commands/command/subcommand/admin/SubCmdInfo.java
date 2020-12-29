package br.com.syrxcraft.betterskyblock.commands.command.subcommand.admin;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.core.api.BetterSkyBlockAPI;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import br.com.syrxcraft.betterskyblock.utils.FancyText;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.data.PlayerData;
import com.griefdefender.claim.GDClaim;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
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
            return false;
        }

        BetterSkyBlockAPI api = BetterSkyBlockAPI.getInstance();
        Island island = IslandUtils.getCurrentIsland(player);

        if (island == null) {
            commandSender.sendMessage("§c§l ▶ §e" + Bukkit.getPlayer(uuid).getName() + "§c não possui uma ilha nesse servidor!");
            return false;
        }

        GDClaim claim = (GDClaim) island.getClaim();
        player.sendMessage("§6§m------------§6(  §e§lIlha de " + island.getOwnerName() + "§e  §6)§m------------");

        FancyText.sendTo(player, new FancyText(" §6▶ Criada em: §b"       + Utils.getFormatedDate(claim.getData().getDateCreated())));
        FancyText.sendTo(player, new FancyText(" §6▶ Expira: "            + (claim.getData().allowExpiration() ? "§cSim" : "§aNão expira")));
        FancyText.sendTo(player, new FancyText(" §6▶ Dono: §b"       + claim.getOwnerName(),(island.isOwnerOnline()? "§aOnline" : "§cOffline")));
        FancyText.sendTo(player, new FancyText(" §6▶ UUID: §b"            + island.getIslandId()));
        FancyText.sendTo(player, new FancyText(" §6▶ Estado: "            + (api.isIslandPublic(island) ? "§aPública" : "§cPrivada")));
        FancyText.sendTo(player, new FancyText(" §6▶ Bioma: §b"           + island.getSpawn().getBlock().getBiome().name()));

        Map<UUID, PermissionType> map = island.getPermissionHolder().getPermissions();

        FancyText.sendTo(player, new FancyText(" §6▶ Jogadores: §b"  + map.size(), String.join("\n", Utils.getPlayersNameByUUID(map.keySet()))));
        FancyText.sendTo(player, new FancyText(" §6▶ Spawn: §5[§dTELEPORTAR§5]", "Clique para se teleportar", "/is spawn " + island.getOwnerName(), false));

        player.sendMessage(" ");
        return true;
    }
}