package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static br.com.syrxcraft.betterskyblock.islands.IslandUtils.isIslandWorld;

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
    void onPlayerTeleport(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && isIslandWorld(event.getFrom().getWorld())) {
            Location loc = event.getPortalTravelAgent().findPortal(new Location(event.getTo().getWorld(), 0, 64, 0));
            if (loc!=null) {
                event.setTo(loc);
                event.useTravelAgent(false);
            }
        }
    }
}
