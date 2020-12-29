package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
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
import com.griefdefender.event.GDCauseStackManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@HasSubCommand
public class SubCmdKick implements ISubCommand {

    @cSubCommand(name = "kick", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if(!(commandSender instanceof Player))
            return false;

        if (!commandSender.hasPermission(PermissionNodes.COMMAND_KICK)){
            return CommandManager.noPermission(commandSender);
        }

        Player player = (Player) commandSender;

        Island island = IslandUtils.getPlayerIsland(player);

        Island currentIs = IslandUtils.getCurrentIsland(player);
        if(currentIs != null && currentIs.getPermissionHolder().getEffectivePermission(player.getUniqueId()) == PermissionType.ADMINISTRATOR){
            island = currentIs;
        }

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/" + command + " spawn\"");
            return false;
        }

        OfflinePlayer p;

        if(args.length >= 1 && args[0] != null) {
            p = Bukkit.getOfflinePlayer(args[0]);
        }else {
            player.sendMessage("§4§l ▶ §cFalta argumentos, use /is kick <player> para expulsar um jogador de sua ilha.");
            return false;
        }

        if(p == null){
            player.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + args[0] + "] !");
            return false;
        }

        if(island.getPermissionHolder().getEffectivePermission(p.getUniqueId()) == PermissionType.NONE){
            player.sendMessage("§4§l ▶ §cO jogador §4§l" + p.getName() + "§r§c não é Membro de sua ilha!");
            return false;
        }

        if(Cooldown.isInCooldown(player, "COMMANDS_KICK")){
            player.sendMessage("§3Você precisa esperar " + TimeUtils.formatSec((int) Cooldown.getCooldownTimeSec(player, "COMMANDS_KICK"))  + " para executar este comando.");
            return false;
        }

        GDCauseStackManager.getInstance().pushCause(BetterSkyBlock.getInstance());

        if(island.getPermissionHolder().getEffectivePermission(p.getUniqueId()).intPermission() >= island.getPermissionHolder().getEffectivePermission(player).intPermission()){
            player.sendMessage("§4§l ▶ §cVocê não tem permissões suficientes para expulsar o jogador §4§l" + p.getName() + "§r§c!");
            return false;
        }

        switch (island.getPermissionHolder().getEffectivePermission(p.getUniqueId())){
            case ADMINISTRATOR:{
                island.getClaim().removeUserTrust(p.getUniqueId(), TrustTypes.MANAGER);
            }

            case MEMBER:{
                island.getClaim().removeUserTrust(p.getUniqueId(), TrustTypes.BUILDER);
            }

        }
        GDCauseStackManager.getInstance().popCause();

        island.getPermissionHolder().updatePermission(p.getUniqueId(), PermissionType.NONE);
        island.update();

        player.sendMessage("§4§l ▶ §cO jogador §4§l" + p.getName() + "§r§c foi expulso com sucesso!");

        if(p.isOnline()){
            p.getPlayer().sendMessage("§4§l ▶ §cVocê foi expulso da ilha de §4§l" + island.getOwnerName() + "§r§c!");
        }

        Cooldown.setCooldown(player, 10L, "COMMANDS_KICK");
        return true;
    }
}
