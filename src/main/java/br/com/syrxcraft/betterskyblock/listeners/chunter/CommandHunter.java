package br.com.syrxcraft.betterskyblock.listeners.chunter;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandHunter implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){

        if(event.isCancelled()) return;

        String[] data = event.getMessage().split(" ", 2);

        if(data.length < 2) data = new String[]{ data[0], ""};

        String cmd = data[0];
        String args = data[1];

        if(cmd.startsWith("/")) cmd = cmd.substring(1);

        if(cmd.contains(":")) cmd = cmd.split(":")[1];

        System.out.println("CMD: " + cmd + " , Data: " + args);

        for (Method method : this.getClass().getDeclaredMethods()) {

            method.setAccessible(true);

            if(method.isAnnotationPresent(CHunterTarget.class)){
                CHunterTarget cHunterTarget = method.getAnnotation(CHunterTarget.class);
                for (String s: cHunterTarget.target()) {
                    if(s.equalsIgnoreCase(cmd)){
                        try {
                            method.invoke(new CommandHunter(), event.getPlayer(), args.split(" "));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            BetterSkyBlock.getInstance().getLoggerHelper().info("Fail to target " + s + " to " + method.getName());
                        }
                    }
                }
            }
        }
    }
    /////

    @CHunterTarget(target = {
            "abandon",
            "abandonall",
            "abandonallclaims",
            "abandonclaim",
            "abandontop"
    })
    private boolean onAbandon(Player player, String ... args){
        return true;
    }

}
