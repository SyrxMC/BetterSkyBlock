package br.com.syrxcraft.betterskyblock.core.data.provider;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.data.provider.providers.Providers;
import br.com.syrxcraft.betterskyblock.core.islands.Island;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataProvider implements IDataProvider{

    @Override
    public boolean onLoad(BetterSkyBlock instance) {
        return internalProvider.onLoad(instance);
    }

    @Override
    public boolean onStop(BetterSkyBlock instance) {
        return internalProvider.onStop(instance);
    }

    @Override
    public Map<UUID, Island> loadData() {
        return internalProvider.loadData();
    }

    @Override
    public void saveData(Set<Island> islands) {
        callAsync(() -> internalProvider.saveData(islands));
    }

    @Override
    public void saveData(Map<UUID, Island> islands) {
        callAsync(() -> internalProvider.saveData(islands));
    }

    @Override
    public void saveIsland(Island island) {
        callAsync(() -> internalProvider.saveIsland(island));
    }

    @Override
    public void removeIsland(Island island) {
        callAsync(() -> internalProvider.removeIsland(island));
    }

    @Override
    public boolean isAsync() {
        return internalProvider.isAsync();
    }

    private IDataProvider internalProvider;
    private final Providers provider;

    public DataProvider(Providers provider){
        this.provider = provider;

        try {
            Object instance = provider.getTargetClass().newInstance();

            if (IDataProvider.class.isAssignableFrom(instance.getClass()))
                internalProvider = (IDataProvider) instance;

        } catch (IllegalAccessException | InstantiationException ignored) {}

        if(internalProvider == null) throw new RuntimeException("Invalid data provider.");

    }

    public Providers getProvider() {
        return provider;
    }
}
