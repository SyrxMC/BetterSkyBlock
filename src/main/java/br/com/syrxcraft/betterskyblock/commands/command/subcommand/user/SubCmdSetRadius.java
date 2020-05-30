package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import org.bukkit.command.CommandSender;

@HasSubCommand
public class SubCmdSetRadius implements ISubCommand {

    @CSubCommand(name = "setradius", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!commandSender.hasPermission(PermissionNodes.COMMAND_SET_RADIUS)) {
            return CommandManager.noPermission(commandSender);
        }

//        if (argumentos.get(1).isEmpty() || argumentos.get(2).isEmpty()){
//            FancyText.sendTo((Player) sender, new FancyText("§6§l ▶ §e/" + label + " setraio <Player> <Raio>","§bAltera o tamanho do raio da ilha!","/" + label + " setraio",true));
//            return true;
//        }
//
//        PlayerData playerData = GriefDefender.getCore().getPlayerData(BetterSkyBlock.getInstance().getIslandWorld().getUID(), Bukkit.getPlayerUniqueId(argumentos.get(1))).orElse(null);
//
//
//        if (playerData == null){
//            sender.sendMessage("§4§l ▶ §cNão existem nenhum jogador chamado [" + argumentos.get(1) + "] !");
//            return true;
//        }
//
//        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(Bukkit.getPlayerUniqueId(argumentos.get(1)));
//
//        if (island == null) {
//            sender.sendMessage("§4§l ▶ §e" + Bukkit.getPlayer(argumentos.get(1)).getName() + "§c não possui uma ilha nesse servidor!");
//            return true;
//        }
//
//        if (!island.ready) {
//            sender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha!");
//            return true;
//        }
//
//        Integer newRadius = Integer.valueOf(argumentos.get(2));
//
//        if (newRadius == null ){
//            sender.sendMessage("§4§l ▶ §c[" + argumentos.get(2) + "] deve ser um número inteiro positivo menor que 254!");
//            return true;
//        }
//
//        if (newRadius > 254 || newRadius < 2){
//            sender.sendMessage("§4§l ▶ §cO novo tamanho da ilha deve ser menor que 254!");
//            return true;
//        }
//
//        island.setRadius(newRadius);
//        sender.sendMessage("§3§l ▶ §aO novo raio da ilha foi definido para " + newRadius + " blocos de distancia!");
//        return true;

        return false;
    }
}
