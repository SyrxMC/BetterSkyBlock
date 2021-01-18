package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionsUtils;
import br.com.syrxcraft.betterskyblock.utils.GriefDefenderUtils;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
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

        Island currentIs = IslandUtils.getCurrentIsland(player);
        if(currentIs != null && currentIs.getPermissionHolder().getEffectivePermission(player.getUniqueId()) == PermissionType.ADMINISTRATOR){
            island = currentIs;
        }

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/" + command + " spawn\"");
            return false;
        }

        Claim claim = island.getClaim();
        claim.setFlagPermission(Flags.ENTER_CLAIM, Tristate.FALSE, new HashSet<>());

        for(UUID uuid : claim.getPlayers()){

            Player p = Bukkit.getPlayer(uuid);

            if (p != null){

                if(!PermissionsUtils.canEnter(island.getPermissionHolder().getEffectivePermission(uuid)) && !p.hasPermission(PermissionNodes.OPTIONS_CAN_ENTER)){
                    p.sendMessage("§4§l ▶ §c Você não tem permissão para entrar nessa ilha!");
                    p.teleport(BetterSkyBlock.getInstance().getSpawn());
                }
            }
        }

        commandSender.sendMessage("§6§l ▶ §eSua ilha está §c§lPrivada!");
        commandSender.sendMessage("§7§oOu seja, apenas jogadores com §n/trust §7podem entrar nela!");
        return true;
    }
}
