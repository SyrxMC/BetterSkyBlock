package br.com.syrxcraft.betterskyblock.data.provider;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;

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

    default boolean isAsync(){
        return true;
    }

}
