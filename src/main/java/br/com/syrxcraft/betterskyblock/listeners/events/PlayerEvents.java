package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.core.api.BetterSkyBlockAPI;
import br.com.syrxcraft.betterskyblock.core.events.IslandEnterEvent;
import br.com.syrxcraft.betterskyblock.core.events.IslandExitEvent;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionsUtils;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static br.com.syrxcraft.betterskyblock.utils.IslandUtils.isIslandWorld;

public class PlayerEvents implements Listener {

    @EventHandler
    void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getPlayer().hasPermission(PermissionNodes.OPTIONS_OVERRIDE) &&
                isIslandWorld(event.getTo().getWorld()) &&
                !isIslandWorld(event.getFrom().getWorld()) &&
                !event.getTo().equals(Bukkit.getWorld(BetterSkyBlock.getInstance().config().getWorldName()).getSpawnLocation())) {

            Claim claim = GriefDefender.getCore().getClaimManager(event.getTo().getWorld().getUID()).getClaimAt(new Vector3i(event.getTo().getX(),event.getTo().getY(),event.getTo().getZ()));

            if (claim==null) {
                event.getPlayer().teleport(Bukkit.getWorld(BetterSkyBlock.getInstance().config().getWorldName()).getSpawnLocation()); // TODO
            }

        }
    }

    @EventHandler
    void onPlayerEnterIsland(IslandEnterEvent event){
        if(!event.isCancelled()){
            if(!BetterSkyBlockAPI.getInstance().isIslandPublic(event.getIsland()) && !PermissionsUtils.canEnter(event.getIsland().permissionHolder.getEffectivePermission(event.getPlayer())) && !event.getPlayer().hasPermission(PermissionNodes.OPTIONS_CAN_ENTER)){
                event.setCancelled(true);
                event.getPlayer().sendMessage("§4§l ▶ §c Você não tem permissão para entrar nessa ilha!");
            }
        }
    }

    @EventHandler
    void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && isIslandWorld(event.getTo().getWorld())) {
            Location loc = event.getPortalTravelAgent().findPortal(new Location(event.getTo().getWorld(), 0, 64, 0));
            if (loc!=null) {
                event.setTo(loc);
                event.useTravelAgent(false);
            }
        }
    }

    @EventHandler
    void onPlayerIslandExit(IslandExitEvent event){

        if(event.getIsland() != null){
            IslandUtils.CACHED_PLAYER_POSITIONS.put(event.getPlayer().getUniqueId(), event.getIsland().getClaim());
        }

        if(event.isOnWilderness()){

            Player player = event.getPlayer();

            if(!isIslandWorld(Utils.worldFromUUID(event.getNewClaim().getWorldUniqueId()))){
                return;
            }

            if(player.getLocation().getBlockY() <= 1) return;

            if (player.hasPermission(PermissionNodes.OPTIONS_OVERRIDE) || player.hasPermission(PermissionNodes.OPTIONS_LEAVE_ISLAND)) {
                return;
            }

            if(!player.getLocation().getWorld().getUID().equals(BetterSkyBlock.getInstance().getIslandWorld().getUID())){
                return;
            }

            player.sendActionBar(ChatColor.RED + "Você não pode voar para fora de uma ilha."); //TODO: implement lang system
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1,1);
            event.setCancelled(true);
        }

    }

    @EventHandler
    void onPlayerMoveEvent(PlayerMoveEvent event){

        Location location = event.getTo();

        if(IslandUtils.isIslandWorld(location.getWorld())){
            if(location.getBlockY() <= 1){

                Claim claim = IslandUtils.CACHED_PLAYER_POSITIONS.getOrDefault(event.getPlayer().getUniqueId(), null);

                if(claim != null && claim.contains(event.getFrom().getBlockX(), 1, event.getFrom().getBlockZ())){
                    if(!claim.contains(event.getTo().getBlockX(), 1, event.getTo().getBlockZ())){
                        event.getPlayer().sendActionBar(ChatColor.RED + "Você não pode voar para fora de uma ilha."); //TODO: implement lang system
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BLOCK, 1,1);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
