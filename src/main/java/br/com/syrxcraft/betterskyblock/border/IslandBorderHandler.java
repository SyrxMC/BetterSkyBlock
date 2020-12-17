package br.com.syrxcraft.betterskyblock.border;

import br.com.syrxcraft.betterskyblock.events.IslandEnterEvent;
import br.com.syrxcraft.betterskyblock.events.IslandExitEvent;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class IslandBorderHandler implements Listener {

    @EventHandler
    public void onIslandEnter(IslandEnterEvent event){
        if(event.getIsland() != null && event.getPlayer() != null){

            Player player = event.getPlayer();
            Island island = event.getIsland();

            IslandBorder.setBorder(player, island.getCenter().getBlockX(), island.getCenter().getBlockZ(), (island.getRadius() * 2) + 1);
        }
    }

    @EventHandler
    public void onIslandExit(IslandExitEvent event){
        if(event.getPlayer() != null){

            if(event.isCancelled()) return;

            Player player = event.getPlayer();
            IslandBorder.removeBorder(player);

        }
    }
}
