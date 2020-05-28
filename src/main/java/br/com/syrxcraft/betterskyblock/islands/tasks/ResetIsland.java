package br.com.syrxcraft.betterskyblock.islands.tasks;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.tasks.SpawnTeleportTask;
import br.com.syrxcraft.betterskyblock.utils.Chronometer;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.claim.Claim;
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
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResetIsland extends BukkitRunnable {

    enum Stage {
        PREPARING,
        REGEN,
        CLEAR_THEM_ALL,
        SCHEMATIC,
        UNLOAD_CHUNKS,
        COMPLETED;
    }

    private final File schematic;
    private final Island island;

    private int lowerX;
    private int lowerZ;

    private int upperX;
    private int upperZ;

    private Chronometer consumedTime;

    private Stage currentStage;

    private boolean paused = false;

    private final LinkedList<Chunk> chunks = new LinkedList<>();
    private ExecutorService executorService;

    public ResetIsland(Island island, File schematic){

        this.island = island;
        this.schematic = schematic;
        this.currentStage = Stage.PREPARING;
        this.consumedTime = new Chronometer();

    }

    public void pause() throws InterruptedException {

        paused = true;
        consumedTime.pause();

        while (isPaused()){
            Thread.sleep(100);
        }

        consumedTime.start();
    }

    public void unpause(){
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    private void initCords(Claim claim){

        System.out.println("Init");

        lowerX = claim.getLesserBoundaryCorner().getX() >> 4;
        lowerZ = claim.getLesserBoundaryCorner().getZ() >> 4;

        upperX = claim.getGreaterBoundaryCorner().getX() >> 4;
        upperZ = claim.getGreaterBoundaryCorner().getZ() >> 4;

        System.out.println(lowerX + "|" + lowerZ + "|" + upperX + "|" + upperZ);

    }

    Runnable regenChunkTask(World world, int x, int y){

        return new Thread(() -> {
            Utils.bukkitSync(new Thread(() -> {
                try {

                    Chunk chunk = PaperLib.getChunkAtAsync(world, x, y, true, true).get();

                    chunks.add(chunk);

                    Utils.bukkitSync(
                            new Thread(() -> {



                                for(Entity entity : chunk.getEntities()){

                                    if (entity instanceof Player){
                                        //TODO: brunoxkk0 25/05/2020 ~ Send Player to spawn.
                                        ((Player)entity).kickPlayer("§cUma ilha estava sendo resetada enquanto você estava próximo!");
                                    }else {
                                        entity.remove();
                                    }

                                }

                                for (BlockState blockState : chunk.getTileEntities()) {

                                    blockState.setType(Material.AIR);
                                    blockState.setData(new MaterialData(Material.AIR));
                                    blockState.update(true,true);

                                }

                                chunk.getWorld().regenerateChunk(chunk.getX(),chunk.getZ());
                                chunk.unload(true);

                                BetterSkyBlock.getInstance().getLoggerHelper().info("Chunk - " + chunk.getX() + "|" + chunk.getZ() + " finished.");
                            })
                    );

                } catch (InterruptedException | ExecutionException e) {
                    BetterSkyBlock.getInstance().getLoggerHelper().error(e.getMessage());
                }
            }));
        });
    }

    Runnable clearThemAllTask(Chunk chunk){
        return new Thread(() -> {
            Utils.bukkitSync(new Thread(() -> {

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
            }));
        });
    }

    @Override
    public void run() {

        consumedTime.start();

        BetterSkyBlock.getInstance().getLoggerHelper().info("[" + (consumedTime.elapsedTime()) +  "MS] ResetIslandTask - PhaseProcess " + currentStage);

        switch (currentStage){

            case PREPARING:{
                initCords(island.getClaim());
                currentStage = Stage.REGEN;
                return;
            }

            case REGEN:{

                if(executorService == null){

                    executorService = Executors.newFixedThreadPool(1);

                    World world = Utils.worldFromUUID(island.getClaim().getWorldUniqueId());

                    for(int cx = lowerX; cx <= upperX; cx++){
                        for(int cz = lowerZ; cz <= upperZ; cz++){
                            executorService.submit(regenChunkTask(world, cx, cz));
                            //System.out.println("Chunk backlog: X=" + cx + ", Y=" + cz);
                            BetterSkyBlock.getInstance().getLoggerHelper().info("Chunk backlog: X=" + cx + ", Y=" + cz);
                        }
                    }

//                    for(Vector3i vector3i : island.getClaim().getChunkPositions()){
//                            executorService.submit(regenChunkTask(world, vector3i.getX(), vector3i.getZ()));
//                            BetterSkyBlock.getInstance().getLoggerHelper().info("Chunk backlog: X=" + vector3i.getX() + ", Y=" + vector3i.getZ());
//                    }

                    executorService.shutdown();

                    return;

                }

                if(executorService.isShutdown()) {
                    currentStage = Stage.CLEAR_THEM_ALL;
                    executorService = null;
                }

                return;
            }

            case CLEAR_THEM_ALL:{

                if(executorService == null) {

                    executorService = Executors.newFixedThreadPool(1);

                    if(!chunks.isEmpty()){
                        chunks.forEach(chunk -> executorService.submit(clearThemAllTask(chunk)));
                    }

                    executorService.shutdown();

                    return;
                }

                if(executorService.isTerminated()) {
                    currentStage = Stage.SCHEMATIC;
                    executorService = null;
                }

                return;

            }

            case SCHEMATIC:{

                BetterSkyBlock.getInstance().getLoggerHelper().info(island.getOwnerName() + " island regeneration complete.");
                Utils.bukkitSync(new Thread(() -> {
                    try {

                        // read schematic file
                        FileInputStream fis = new FileInputStream(schematic);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);

                        // create clipboard
                        WorldData worldData = LegacyWorldData.getInstance();
                        Clipboard clipboard = reader.read(worldData);
                        fis.close();

                        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, worldData);
                        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(Utils.fromBukkitToWorldEditWorld(Utils.worldFromUUID(island.getClaim().getWorldUniqueId())), 1000000);

                        try {
                            island.setSpawn(island.getCenter());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        island.getSpawn().getChunk().load();

                        Operation operation = clipboardHolder.createPaste(editSession, LegacyWorldData.getInstance()).to(Utils.toVector(island.getSpawn())).ignoreAirBlocks(true).build();
                        Operations.completeLegacy(operation);

                        BetterSkyBlock.getInstance().getLoggerHelper().info(island.getOwnerName() + " island schematic load complete.");


                        if (BetterSkyBlock.getInstance().config().getDefaultBiome() != null) {
                            island.setIslandBiome(BetterSkyBlock.getInstance().config().getDefaultBiome());
                            BetterSkyBlock.getInstance().getLoggerHelper().info(island.getOwnerName() + " island biome set to default biome (" + BetterSkyBlock.getInstance().config().getDefaultBiome().toString() + ")");
                        }


                        currentStage = Stage.UNLOAD_CHUNKS;

                    } catch (MaxChangedBlocksException | IOException e) {

                        if (island.isOwnerOnline()) {
                            island.getPlayer().sendMessage(ChatColor.RED + "An error occurred while generating a new island: schematic load error.");
                        }

                        this.cancel();
                        e.printStackTrace();
                    }
                }));

                return;
            }

            case UNLOAD_CHUNKS: {

                Utils.bukkitSync(new Thread(() -> {
                    chunks.forEach(Chunk::unload);
                }));

                currentStage = Stage.COMPLETED;

                return;
            }

            case COMPLETED: {

                island.ready = true;

                consumedTime.stop();

                BetterSkyBlock.getInstance().getLoggerHelper().info("Took " + consumedTime.elapsedTime() + "MS to load " + island.getOwnerName() + " island.");

                if (island.isOwnerOnline()) {
                    island.getPlayer().sendMessage(ChatColor.GREEN+"A sua ilha foi gerada com sucesso! Você será teletransportado em " + BetterSkyBlock.getInstance().config().getTpCountdown() + " segundos.");
                    SpawnTeleportTask.teleportTask(island.getPlayer(), island, BetterSkyBlock.getInstance().config().getTpCountdown());
                }

                this.cancel();
                return;
            }

        }
    }
}
