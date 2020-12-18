package br.com.syrxcraft.betterskyblock.islands.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.Chronometer;
import br.com.syrxcraft.betterskyblock.utils.LoggerHelper;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import br.com.syrxcraft.betterskyblock.tasks.SpawnTeleportTask;
import com.flowpowered.math.vector.Vector3i;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.LegacyWorldData;
import com.sk89q.worldedit.world.registry.WorldData;

public class ResetIslandTask extends BukkitRunnable {

	private final LoggerHelper loggerHelper = BetterSkyBlock.getInstance().getLoggerHelper();

	private final File schematic;
	private final Island island;
	private final String ownerName;

	private int x, z, lz, gx, gz;

	private Stage currentStage;
	private Stage lastStage;

	private final List<Chunk> islandChunks = new ArrayList<>();

	private int INDEX = 0;
	private final Chronometer chronometer = new Chronometer();

	public ResetIslandTask(Island island, File schematic) {

		this.island 		= island;
		this.schematic 		= schematic;
		this.currentStage	= Stage.REGEN;
		this.ownerName 		= island.getOwnerName();

		initCoords();

		if (island.isOwnerOnline())
			island.getPlayer().sendMessage(ChatColor.GREEN + "Por favor espere enquanto a sua ilha está sendo criada!");


		loggerHelper.info("Generating " + ownerName + " island at " + Utils.toString(island.getCenter()));
	}

	private void initCoords() {

		Vector3i LCorner = island.getClaim().getLesserBoundaryCorner();
		Vector3i GCorner = island.getClaim().getGreaterBoundaryCorner();

		z = lz	= LCorner.getZ() >> 4;
		x 		= LCorner.getX() >> 4;
		gz 		= GCorner.getZ() >> 4;
		gx 		= GCorner.getX() >> 4;

	}

	@Override
	public void run() {

		chronometer.start();

		if(lastStage != currentStage){
			lastStage = currentStage;

			loggerHelper.info("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName +"}");
		}

		switch(currentStage) {

			case REGEN: {

				for (int i = 0; i < 8; i++) {
					if (x <= gx) {
						if (z <= gz) {

							try {

								Chunk chunk = Utils.worldFromUUID(island.getClaim().getWorldUniqueId()).getChunkAt(x, z);
								chunk.load(true);

								for (Entity entity : chunk.getEntities()) {

									if (entity instanceof Player){

										Player player = ((Player)entity);

										player.teleport(BetterSkyBlock.getInstance().getSpawn());
										player.sendMessage("§cUma ilha estava sendo resetada enquanto você estava próximo!"); // TODO: Lang System

										continue;
									}

									entity.remove();
								}

								for (BlockState blockState : chunk.getTileEntities()) {

									blockState.setType(Material.AIR);
									blockState.setData(new MaterialData(Material.AIR));
									blockState.update(true,true);

								}

								chunk.getWorld().regenerateChunk(chunk.getX(),chunk.getZ());
								chunk.unload(true);

								islandChunks.add(chunk);

							} catch (Exception e) {

								Player player = island.getPlayer();

								if (player != null) {
									player.sendMessage(ChatColor.RED + "An error occurred while generating a new island: regen error."); //Todo: Lang
								}

								loggerHelper.error("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName + "} An error occurred while generating this island."); //Todo: Lang

								e.printStackTrace();

								this.cancel();
								return;
							}

							z++;

						} else {
							this.z = lz;
							x++;
						}

					} else {

						currentStage = Stage.CLEAR_THEM_ALL;
						return;

					}
				}
				return;
			}

			case CLEAR_THEM_ALL: {

				for (int INDEX_PLUS_8 = INDEX + 8; INDEX < INDEX_PLUS_8 && INDEX < islandChunks.size(); INDEX++){

					Chunk chunk = islandChunks.get(INDEX);
					chunk.load(true);

					final World world = chunk.getWorld();

					int cx = chunk.getX() << 4;
					int cz = chunk.getZ() << 4;

					boolean foundAnyBlock = false;

					for (int x = cx; x < cx + 16; x++) {
						for (int z = cz; z < cz + 16; z++) {
							for (int y = 0; y < 128; y++) {

								Block block;

								if ((block = world.getBlockAt(x, y, z)).getType() != Material.AIR) {
									block.setType(Material.AIR);
									foundAnyBlock = true;
								}

							}
						}
					}

					if (foundAnyBlock){
						chunk.unload(true);
						chunk.load();
					}

				}

				if (INDEX == islandChunks.size()){
					this.currentStage = Stage.SCHEMATIC;
				}

				return;
			}

			case SCHEMATIC: {

				loggerHelper.info("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName + "} Island regeneration finished successfully.");

				try {

					FileInputStream fis = new FileInputStream(schematic);
					BufferedInputStream bis = new BufferedInputStream(fis);

					ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);

					// create clipboard
					WorldData worldData = LegacyWorldData.getInstance();
					Clipboard clipboard = reader.read(worldData);
					fis.close();

					ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, worldData);

					EditSession editSession = WorldEdit
							.getInstance()
							.getEditSessionFactory()
							.getEditSession(Utils.fromBukkitToWorldEditWorld(Utils.worldFromUUID(island.getClaim().getWorldUniqueId())), 1000000);

					try {
						island.setSpawn(island.getCenter());
					} catch (SQLException e) {
						e.printStackTrace();
					}

					island.getSpawn().getChunk().load();

					Operation operation = clipboardHolder
							.createPaste(editSession, LegacyWorldData.getInstance())
							.to(Utils.toVector(island.getSpawn()))
							.ignoreAirBlocks(true)
							.build();

					Operations.completeLegacy(operation);

					loggerHelper.info("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName + "} Island schematic load finished successfully.");

					if (BetterSkyBlock.getInstance().config().getDefaultBiome() != null) {

						Biome biome = BetterSkyBlock.getInstance().config().getDefaultBiome();

						island.setIslandBiome(biome);

						loggerHelper.info("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName + "} Island biome set to default value ( " + biome + " )");

					}

					currentStage = Stage.UNLOAD_CHUNKS;
					initCoords();

				} catch (MaxChangedBlocksException | IOException e) {

					Player player = island.getPlayer();

					if (player != null) {
						player.sendMessage(ChatColor.RED + "An error occurred while generating a new island: schematic load error"); //Todo: Lang
					}

					loggerHelper.error("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName + "} An error occurred while loading the schematic."); //Todo: Lang

					this.cancel();
					e.printStackTrace();
				}

				return;
			}

			case UNLOAD_CHUNKS: {

				islandChunks.forEach(Chunk::unload);

				this.currentStage = Stage.COMPLETED;
				return;
			}

			case COMPLETED: {

				island.ready = true;

				chronometer.stop();
				loggerHelper.info("( " + chronometer.elapsedTime() +  " ms ) [ResetIslandTask] ~ {Current Phase Process: " + currentStage + " Owner: " + ownerName + "} Finished Successfully !");


				Player player = island.getPlayer();

				if (player != null) {
					player.sendMessage(ChatColor.GREEN + "A sua ilha foi gerada com sucesso! Você será teletransportado em " + BetterSkyBlock.getInstance().config().getTpCountdown() + " segundos."); //Todo: Lang
					SpawnTeleportTask.teleportTask(island.getPlayer(), island, BetterSkyBlock.getInstance().config().getTpCountdown());
				}

				this.cancel();
			}
		}
	}

	enum Stage {
		REGEN, CLEAR_THEM_ALL, SCHEMATIC, UNLOAD_CHUNKS, COMPLETED
	}

}
