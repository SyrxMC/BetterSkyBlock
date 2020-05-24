package br.com.syrxcraft.betterskyblock.listeners;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

	private BetterSkyBlock instance;
	
	public EventListener(BetterSkyBlock instance) {
		this.instance = instance;
	}

	/*
	@EventHandler(priority = EventPriority.MONITOR)
	void onPlayerJoin(PlayerClaimDeleteEventnt event) {
		final Player player = event.getPlayer();
		if (player == null || !player.isOnline() || player.getName() == null) {
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!player.isOnline()) {
					return;
				}

				if (instance.config().autoSpawn && !player.hasPlayedBefore()) {
					Island island = instance.getDataStore().getIsland(player.getUniqueId());
					if (island==null) {
						try {
							island = instance.getDataStore().createIsland(player.getUniqueId());
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				}


				if (isIslandWorld(player.getLocation().getWorld()) && GriefPreventionPlus.getInstance().getDataStore().getClaimAt(player.getLocation()) == null) {
					player.teleport(GPPSkyBlock.getInstance().getSpawn());
				}

			}
		}.runTaskLater(instance, 20L);
	}

	@EventHandler(ignoreCancelled=true)
	void onClaimExit(ClaimExitEvent event) {
		if (event.getPlayer().hasPermission("gppskyblock.override") || event.getPlayer().hasPermission("gppskyblock.leaveisland")) {
			return;
		}

		if (isIslandWorld(event.getFrom().getWorld()) && isIslandWorld(event.getTo().getWorld())) {
			event.getPlayer().sendMessage(ChatColor.RED+"Você não pode voar para fora de sua ilha!");
			Island island = getIsland(event.getClaim());
			if (island!=null) {
				event.getPlayer().teleport(island.getSpawn());
			} else {
				event.setCancelled(true);
			}
			return;
		}
	}
	*/

}
