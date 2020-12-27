package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.GriefDefenderUtils;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustType;
import com.griefdefender.api.permission.flag.Flags;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

@HasSubCommand
public class SubCmdPrivate implements ISubCommand {
    @cSubCommand(name = "private", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        if(!player.hasPermission(PermissionNodes.COMMAND_PRIVATE)){
            return CommandManager.noPermission(player);
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/" + command + " spawn\"");
            return false;
        }

        Claim claim = island.getClaim();
        claim.setFlagPermission(Flags.ENTER_CLAIM, Tristate.FALSE, new HashSet<>());

        System.out.println(claim.getPlayers());

        for(UUID uuid : claim.getPlayers()){

            Player p = Bukkit.getPlayer(uuid);

            if (p != null){

                TrustType trustType = GriefDefenderUtils.getPlayerTrustType(p, claim);

                if(claim.getOwnerUniqueId().equals(uuid))
                    continue;

                if(!GriefDefenderUtils.getPlayerFlagPermission(p, claim, Flags.ENTER_CLAIM, trustType)){
                    player.sendMessage("§4§l ▶ §c Você não tem permissão para entrar nessa ilha!");
                    player.teleport(BetterSkyBlock.getInstance().getSpawn());
                }
            }
        }

        commandSender.sendMessage("§6§l ▶ §eSua ilha está §c§lPrivada!");
        commandSender.sendMessage("§7§oOu seja, apenas jogadores com §n/trust §7podem entrar nela!");
        return true;
    }
}
