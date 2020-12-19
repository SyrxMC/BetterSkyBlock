package br.com.syrxcraft.betterskyblock.tasks;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.Chronometer;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.LoggerHelper;
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
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ResetIslandThread extends Thread{

    private final LoggerHelper loggerHelper = BetterSkyBlock.getInstance().getLoggerHelper();

    private final ResetIslandThread INSTANCE;

    private final Island    island;
    private final Claim     claim;
    private final World     world;
    private final Player    player;
    private final File      schematic;

    private final List<Supplier<Chunk>>    chunkSuppliers  = new ArrayList<>();
    private final List<Runnable>           chunkUnloaders  = new ArrayList<>();
    private final List<Runnable>           biomeTasks      = new ArrayList<>();

    private final Chronometer chronometer = new Chronometer();

    public ResetIslandThread(Island island, File schematic) {

        INSTANCE = this;

        this.island     = island;
        this.claim      = island.getClaim();
        this.world      = IslandUtils.getIslandWorld(island);
        this.player     = island.getPlayer();
        this.schematic  = schematic;

        setName("[ResetIslandThread] - " + claim.getUniqueId() + " - " + island.getOwnerName());
        setDaemon(true);

        start();
    }


    private void fillSuppliers(){
        chunkSuppliers.clear();

        Vector3i[] corners = Utils.orderPositions(new Vector3i[]{
                claim.getLesserBoundaryCorner(),
                claim.getGreaterBoundaryCorner()
        });

        int lowerX = corners[0].getX() >> 4;
        int lowerZ = corners[0].getZ() >> 4;

        int upperX = corners[1].getX() >> 4;
        int upperZ = corners[1].getZ() >> 4;

        Biome biome = BetterSkyBlock.getInstance().config().getDefaultBiome();

        for (; lowerX <= upperX; lowerX++) {
            for (int z = lowerZ; z <= upperZ; z++) {

                final int chunkX = lowerX;
                final int chunkZ = z;

                chunkSuppliers.add(() -> world.getChunkAt(chunkX, chunkZ));

                chunkUnloaders.add(() -> world.unloadChunk(chunkX, chunkZ));

                biomeTasks.add(() -> world.setBiome(chunkX, chunkZ, biome));

            }
        }
    }

    private void regenChunk(Chunk chunk){

        for (Entity entity : chunk.getEntities()) {

            if (entity instanceof Player){

                Player player = ((Player)entity);

                player.teleport(BetterSkyBlock.getInstance().getSpawn());
                player.sendMessage("§cUma ilha estava sendo resetada enquanto você estava próximo!"); // TODO: Lang System

                continue;
            }

            entity.remove();
        }

        chunk.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());

        new BukkitRunnable(){

            @Override
            public void run() {
                chunk.unload(true);
            }

        }.runTaskLater(BetterSkyBlock.getInstance(),1);
    }

    private void pasteSchematic(){

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

        } catch (MaxChangedBlocksException | IOException e) {

            Player player = island.getPlayer();

            if (player != null) {
                player.sendMessage(ChatColor.RED + "An error occurred while generating a new island: schematic load error"); //Todo: Lang
            }

            e.printStackTrace();

        }

        unpauseProcess();
    }

    private void changeBiomes(){

        try {
            biomeTasks.forEach(Runnable::run);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void bringOwnerBack(){
        if (player != null && player.isOnline()) {
            player.sendMessage(ChatColor.GREEN+"A sua ilha foi gerada com sucesso! Você será teletransportado em " + BetterSkyBlock.getInstance().config().getTpCountdown() + " segundos.");
            SpawnTeleportTask.teleportTask(player, island, BetterSkyBlock.getInstance().config().getTpCountdown());
        }
    }

    boolean canWeContinue = true;

    private void pauseProcessUntilWeCanContinue() throws InterruptedException{

        canWeContinue = false;

        while (!canWeContinue()){
            Thread.sleep(100);
        }

    }

    private void unpauseProcess(){
        canWeContinue = true;
    }

    private boolean canWeContinue(){
        return canWeContinue;
    }

    private void runSync(Runnable runnable){
        new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            };
        }.runTaskLater(BetterSkyBlock.getInstance(),1);
    }


    private final AtomicInteger failedChunks = new AtomicInteger(0);

    @Override
    public void run() {

        try {

            chronometer.start();

            String owner = island.getOwnerName();

            loggerHelper.info("(" + chronometer.elapsedTime() +  "ms) [Reset Island Thread] {Island: " + owner + "} Starting island chunks count.");

            island.teleportEveryoneToSpawn();
            fillSuppliers();

            int totalChunks = chunkSuppliers.size();

            loggerHelper.info("(" + chronometer.elapsedTime() +  "ms) [Reset Island Thread] {Island: " + owner + "} Starting regeneration of " + totalChunks + " Chunks!");

            int contador = 0;
            int delay = 1;

            for (int i = 0; i < totalChunks; i++) {

                if (contador >= 9){
                    contador = 0;
                    delay++;
                }

                contador++;
                final Supplier<Chunk> chunkSupplier = chunkSuppliers.get(i);
                final boolean isLastChunk = (i == (chunkSuppliers.size() - 1));

                new BukkitRunnable(){
                    @Override
                    public void run() {

                        chunkSupplier.get();//Load the chunk

                        new BukkitRunnable(){

                            @Override
                            public void run() {

                                try {
                                    final Chunk bChunk = chunkSupplier.get();
                                    regenChunk(bChunk);
                                }catch (Exception e){
                                    synchronized (failedChunks){
                                        failedChunks.incrementAndGet();
                                    }
                                }

                            }
                        }.runTaskLater(BetterSkyBlock.getInstance(),1);
                    }

                }.runTaskLater(BetterSkyBlock.getInstance(),delay);

                if (isLastChunk){
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            INSTANCE.unpauseProcess();
                        }
                    }.runTaskLater(BetterSkyBlock.getInstance(),delay + 5);
                }
            }

            pauseProcessUntilWeCanContinue();//Pause the Threads!

            loggerHelper.info("(" + chronometer.elapsedTime() +  "ms) [Reset Island Thread] {Island: " + owner + "} Regen finished. Pasting the island schematic.");

            runSync(this::pasteSchematic);
            pauseProcessUntilWeCanContinue();//Pause the Threads!

            loggerHelper.info("(" + chronometer.elapsedTime() +  " ms) [Reset Island Thread] {Island: " + owner + "} Pasting finished!");
            loggerHelper.info("(" + chronometer.elapsedTime() +  " ms) [Reset Island Thread] {Island: " + owner + "} Process finished successfully.");

            island.ready = true;

            runSync(() -> {
                for (Runnable bukkitChunkUnloader : chunkUnloaders) {
                    bukkitChunkUnloader.run();
                }
            });

            new BukkitRunnable(){
                @Override
                public void run() {
                    changeBiomes();
                }
            }.runTaskLater(BetterSkyBlock.getInstance(),1);

            bringOwnerBack();

            if (failedChunks.get() > 0) {
                loggerHelper.error("(" + chronometer.elapsedTime() +  " ms) [Reset Island Thread] ~ {Island: " + owner + "} Apparently " + failedChunks.get() + " chunks fail on island regeneration.");
            }
        }catch (Exception e){

            e.printStackTrace();

            loggerHelper.info("§cErro ao tentar contar as chunks do seu Claim!"); // TODO: send to player
            loggerHelper.info("§c§o" + e.getMessage());

        }
    }
}