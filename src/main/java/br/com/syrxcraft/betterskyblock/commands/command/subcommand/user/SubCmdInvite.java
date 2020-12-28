package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import br.com.syrxcraft.betterskyblock.utils.Cooldown;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.TimeUtils;
import com.griefdefender.api.claim.TrustTypes;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@HasSubCommand
public class SubCmdInvite implements ISubCommand {

    @cSubCommand(name = "invite", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if(!(commandSender instanceof Player))
            return false;

        if (!commandSender.hasPermission(PermissionNodes.COMMAND_INVITE)){
            return CommandManager.noPermission(commandSender);
        }

        Player player = (Player) commandSender;

        Island island = IslandUtils.getPlayerIsland(player);

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/" + command + " spawn\"");
            return false;
        }

        OfflinePlayer p;

        if(args.length >= 1 && args[0] != null) {
            p = Bukkit.getOfflinePlayer(args[0]);
        }else {
            player.sendMessage("§4§l ▶ §cFalta argumentos, use /is invite <player> para convidar um jogador para sua ilha.");
            return false;
        }


        if(p == null){
            player.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + args[0] + "] !");
            return false;
        }

        if(Cooldown.isInCooldown(player, "COMMANDS_INVITE")){
            player.sendMessage("§3Você precisa esperar " + TimeUtils.formatSec((int) Cooldown.getCooldownTimeSec(player, "COMMANDS_INVITE"))  + " para executar este comando.");
            return false;
        }

        if(island.getPermissionHolder().getEffectivePermission(p.getUniqueId()).intPermission() >= PermissionType.MEMBER.intPermission()){
            player.sendMessage("§6§l ▶ §eO jogador §6§l"+ p.getName() + "§r§e já possui permissão em sua ilha.");
            return false;
        }

        island.permissionHolder.updatePermission(p.getUniqueId(), PermissionType.MEMBER);
        island.update();
        island.getClaim().addUserTrust(p.getUniqueId(), TrustTypes.BUILDER);

        player.sendMessage("§6§l ▶ §eO jogador §6§l"+ p.getName() + "§r§e foi convidado para sua ilha com sucesso!");
        Cooldown.setCooldown(player, 10L, "COMMANDS_INVITE");

        return true;
    }
}
