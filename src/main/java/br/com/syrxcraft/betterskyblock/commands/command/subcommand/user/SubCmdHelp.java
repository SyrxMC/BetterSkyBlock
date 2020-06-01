package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.finalcraft.evernifecore.fancytext.FancyText;
import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.integration.Integrations;
import br.com.syrxcraft.betterskyblock.integration.integrations.BossShopProIntegration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



@HasSubCommand
public class SubCmdHelp implements ISubCommand {

    @CSubCommand(name = "help", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if(commandSender instanceof Player && BetterSkyBlock.getInstance().config().useBossShopForMenu() && BetterSkyBlock.getInstance().getIntegrationManager().isIntegrationLoaded(Integrations.BossShopPro)){
            BossShopProIntegration.openShop((Player) commandSender, "islands");
            return true;
        }

        if(commandSender instanceof Player){

            Player player = (Player) commandSender;

            player.sendMessage("§3§m------------§3(  §b§lBetterSkyBlock§b  §3)§m------------");

            FancyText.sendTo(player, new FancyText("§3§l ▶ §a/" + command + " help","§bMostra essa mensagem!","/" + command + " help",true));
            FancyText.sendTo(player, new FancyText("§3§l ▶ §a/" + command + " spawn [Player]","§bTeleporta para a sua ilha ou a de algum jogador!","/" + command + " spawn",true));
            FancyText.sendTo(player, new FancyText("§3§l ▶ §a/" + command + " setspawn","§bAltera a localização do Spawn da sua ilha!","/" + command + " setspawn",true));
            FancyText.sendTo(player, new FancyText("§3§l ▶ §a/" + command + " private","§bDeixa a sua ilha Privada!","/" + command + " private",true));
            FancyText.sendTo(player, new FancyText("§3§l ▶ §a/" + command + " public","§bDeixa a sua ilha Pública!","/" + command + " public",true));
            FancyText.sendTo(player, new FancyText("§3§l ▶ §a/" + command + " reset","§bReseta a sua ilha!(Apaga ela inteira!!!)","/" + command + " reset",true));

//            if (player.hasPermission(PermissionNodes.ADMIN) && player.hasPermission(PermissionNodes.COMMAND_SETRADIUS))
//                FancyText.sendTo(player, new FancyText("§6§l ▶ §e/" + command + " setraio <Player> <Raio>","§bAltera o tamanho do raio da ilha!","/" + command + " setraio",true));

            if (player.hasPermission(PermissionNodes.OPTIONS_SET_BIOME_BASE)) {
                FancyText.sendTo(player, new FancyText("§3§l ▶ §b/" + command + " biomelist","§bMostra os possíveis biomas!","/" + command + " biomelist",true));
                if (player.hasPermission(PermissionNodes.COMMAND_SET_BIOME_OTHER)){
                    FancyText.sendTo(player, new FancyText("§3§l ▶ §b/" + command + " setbiome <Bioma> [Player]","§bDefine o bioma de toda a sua ilha!","/" + command + " setbiome ",true));
                }else {
                    FancyText.sendTo(player, new FancyText("§3§l ▶ §b/" + command + " setbiome <Bioma>","§bDefine o bioma de toda a sua ilha!","/" + command + " setbiome ",true));
                }
            }

            if (player.hasPermission(PermissionNodes.ADMIN)){
                FancyText.sendTo(player, new FancyText("§5§l ▶ §6/" + command + " delete <Player>","§bDeleta a ilha de algum jogador!\n\nNá pratica nao deleta fisicamente, apenas remove o claim!\nFazendo com que ele tenha que criar uma nova ilha em outro lugar.","/" + command + " biomelist",true));
            }

            player.sendMessage("");
            player.sendMessage("§3§oPasse o mouse em cima dos comandos para ver a descrição!");
            return true;

        }

        commandSender.sendMessage("You need to be a player to execute this command.");
        return true;
    }
}
