package br.com.syrxcraft.betterskyblock.border;

import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.core.events.IslandEnterEvent;
import br.com.syrxcraft.betterskyblock.core.events.IslandExitEvent;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class IslandBorderHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIslandEnter(IslandEnterEvent event){

        IslandBorder.removeBorder(event.getPlayer());

        if(event.getIsland() != null && event.getPlayer() != null){

            Player player = event.getPlayer();
            Island island = event.getIsland();

            if(!player.hasPermission(PermissionNodes.OPTIONS_NO_BORDER))
                IslandBorder.setBorder(player, island.getCenter().getBlockX(), island.getCenter().getBlockZ(), (island.getRadius() * 2) + 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIslandExit(IslandExitEvent event){
        if(event.getPlayer() != null){

            if(event.isCancelled()) return;

            Player player = event.getPlayer();
            IslandBorder.removeBorder(player);

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(event.getPlayer().isOnline()){
            IslandBorder.removeBorder(event.getPlayer());
        }
    }
}
