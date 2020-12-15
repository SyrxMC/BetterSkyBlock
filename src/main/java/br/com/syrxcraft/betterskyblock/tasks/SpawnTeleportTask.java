package br.com.syrxcraft.betterskyblock.tasks;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnTeleportTask extends BukkitRunnable {

	private int countdown, errors;
	private final Player player;
	private final Island island;
	private final Location location;
	
	private SpawnTeleportTask(Player player, Island island, int countdown) {
		this.player = player;
		this.island = island;
		this.location = player.getLocation();
		this.countdown = countdown;
	}

	@Override
	public void run() {

		if (!player.isOnline()) {
			this.cancel();
			return;
		}
		
		try {

			if (!island.getSpawn().getChunk().load()) {
				return;
			}

		} catch (Exception e1) {

			return;

		} finally {

			errors++;

			if (errors > 50) {

				player.sendMessage(ChatColor.RED + "Teleport cancelado");
				cancel();
				return;

			}
		}

		try {
			if (this.location.distanceSquared(location)>0) {
				player.sendMessage(ChatColor.RED + "Teleport cancelado");
				this.cancel();
				return;
			}
		} catch (IllegalStateException e) {
			player.sendMessage(ChatColor.RED + "Teleport cancelado");
			this.cancel();
			return;
		}
		
		if (countdown<=0) {
			player.teleport(island.getSpawn());
			if (countdown<-4) {
				this.cancel();
				return;
			}
			
		}
		
		countdown--;
	}
	
	public static void teleportTask(Player player, Island island, int countdown) {
		new SpawnTeleportTask(player, island, countdown * 4).runTaskTimer(BetterSkyBlock.getInstance(), 0L, 5L);
	}
}
