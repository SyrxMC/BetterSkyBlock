package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@HasSubCommand
public class SubCmdReset implements ISubCommand {

    @CSubCommand(name = "reset", targetCommand = "island2")
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

        if (!island.ready) {
            commandSender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha!");
            return false;
        }

        String conf = BetterSkyBlock.getInstance().getCommandManager().getConfirmations().remove(player.getName());

        if (conf == null || !conf.equals("reset")) {
            commandSender.sendMessage("§4§l ▶ §c§lCUIDADO: §cSua ilha inteira será APAGADA!\n§cSe você tem certeza disso, use \"/"+label+" reset\" novamente!");
            BetterSkyBlock.getInstance().getCommandManager().getConfirmations().put(player.getName(), "reset");
            return false;
        }

        //TODO: Implement a new cooldown system ?
//        Cooldown cooldown = Cooldown.getOrCreateCooldown("GPPSkyBlock-ISRESET",player.getName());
//        if (cooldown.isInCooldown()){
//            cooldown.warnPlayer(sender);
//            return true;
//        }
//        cooldown.setPermaCooldown(true);
//        cooldown.startWith(259200);//3 Dias
        island.reset();
        return true;
    }
}
