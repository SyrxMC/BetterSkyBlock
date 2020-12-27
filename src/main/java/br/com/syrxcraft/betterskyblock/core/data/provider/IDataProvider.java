package br.com.syrxcraft.betterskyblock.core.data.provider;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IDataProvider {

    boolean onLoad(BetterSkyBlock instance);

    boolean onStop(BetterSkyBlock instance);

    Map<UUID, Island> loadData();

    void saveData(Set<Island> islands);

    void saveData(Map<UUID, Island> islands);

    void saveIsland(Island island);

    void removeIsland(Island island);

    boolean isAsync();

    default void callAsync(Runnable runnable){

        if(isAsync()){
            Bukkit.getScheduler().runTaskAsynchronously(BetterSkyBlock.getInstance(), runnable);
        }else {
            runnable.run();
        }

    }

}
