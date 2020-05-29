package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.manager.CSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;

@HasSubCommand
public class SubCmdBiomeList implements ISubCommand {

    @CSubCommand(name = "biomelist", targetCommand = "island2")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        StringBuilder sb = new StringBuilder(ChatColor.GOLD + "Biome list: " + ChatColor.AQUA);

        if (BetterSkyBlock.getInstance().config().getAllowedBiomes().isEmpty()) {
            sb.append(ChatColor.RED + "Nenhum");
        } else {
            for (Biome biome : BetterSkyBlock.getInstance().config().getAllowedBiomes()) {

                if(!commandSender.hasPermission(PermissionNodes.OPTIONS_SET_BIOME_BASE + "." + biome.toString().toLowerCase())) {
                    continue;
                }

                sb.append(Utils.fromSnakeToCamelCase(biome.toString()));
                sb.append(", ");
            }
        }

        commandSender.sendMessage(sb.substring(0, sb.length() - 2));
        return true;
    }
}
