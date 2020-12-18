package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.commands.manager.cCommandHunterTarget;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CommandHunterEvents implements Listener {

    private final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", 16);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){

        if(event.isCancelled()) return;

        String[] data = PATTERN_ON_SPACE.split(event.getMessage().substring(1), 2);

        if(data.length == 0) return;

        String command = data[0].toLowerCase();
        String[] args = Arrays.copyOfRange(data, 1, data.length);

        if(command.contains(":")){

            String realCommand = command.substring(command.indexOf(':'));

            if(!realCommand.isEmpty()) command = realCommand;

        }

        for (Method method : this.getClass().getDeclaredMethods()) {

            method.setAccessible(true);

            if(method.isAnnotationPresent(cCommandHunterTarget.class)){
                cCommandHunterTarget cCommandHunterTarget = method.getAnnotation(cCommandHunterTarget.class);
                for (String s: cCommandHunterTarget.target()) {
                    if(s.equalsIgnoreCase(command)){

                        try {
                            Object object = method.invoke(new CommandHunterEvents(),event.getPlayer(), command, args);

                            if(object != null){
                                if((boolean)object){
                                    event.setMessage(" ");
                                    event.setCancelled(true);
                                }
                            }

                        } catch (IllegalAccessException | InvocationTargetException e) {
                            BetterSkyBlock.getInstance().getLoggerHelper().error("Fail to call " + s + " to " + method.getName());
                        }
                    }
                }
            }
        }
    }

    @cCommandHunterTarget(target = {
            "abandon",
            "abandonclaim",
            "abandontop",
            "abandonall"
    })
    private boolean onAbandon(Player player, String label, String ... args){

        Island island = IslandUtils.isOnIsland(player);

        if(island != null){
            if(island.getOwnerId().equals(player.getUniqueId())){
                player.sendMessage(ChatColor.RED + "Se você quer deletar a sua ilha use o comando \"/is delete\"!");
                return true;
            }
        }

        return false;
    }

}
