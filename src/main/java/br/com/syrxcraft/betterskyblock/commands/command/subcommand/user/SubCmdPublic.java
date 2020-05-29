package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.islands.Island;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.permission.flag.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

@HasSubCommand
public class SubCmdPublic implements ISubCommand {

    @CSubCommand(name = "public", targetCommand = "island2")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/"+label+" spawn\"");
            return false;
        }

        Claim claim = island.getClaim();
        claim.setFlagPermission(Flags.ENTER_CLAIM, Tristate.TRUE, new HashSet<>());

        commandSender.sendMessage("§6§l ▶ §eSua ilha está §a§lPublica!");
        commandSender.sendMessage("§7§oOu seja, qualquer um pode entrar nela!");
        return true;
    }

}
