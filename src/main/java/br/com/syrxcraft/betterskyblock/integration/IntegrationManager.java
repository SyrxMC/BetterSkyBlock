package br.com.syrxcraft.betterskyblock.integration;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.integration.integrations.BossShopProIntegration;
import br.com.syrxcraft.betterskyblock.integration.integrations.PlaceHolderAPIIntegration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IntegrationManager implements Listener {

    private final BetterSkyBlock betterSkyBlock;

    public IntegrationManager(BetterSkyBlock betterSkyBlock) {
        this.betterSkyBlock = betterSkyBlock;

        new BukkitRunnable() {
            @Override
            public void run() {
                loadIntegrations();
            }
        }.runTaskLater(betterSkyBlock, 1L);

    }

    void loadIntegrations(){
        for(Integrations integrations : Integrations.values()){

            try{
                Object instance  = integrations.getClazz().newInstance();

                IIntegration iIntegration = (IIntegration) instance;

                if(Bukkit.getPluginManager().isPluginEnabled(iIntegration.targetPlugin())){

                    if(!iIntegration.targetVersion().equals("*")){

                        Plugin plugin;

                        if(!(plugin = Bukkit.getPluginManager().getPlugin(iIntegration.targetPlugin())).getDescription().getVersion().equals(iIntegration.targetVersion())){
                            betterSkyBlock.getLoggerHelper().info("The integration " + iIntegration.targetPlugin() + "cannot be loaded cause requires [" + iIntegration.targetVersion() + "] and was founded [" + plugin.getDescription().getVersion()+"]");
                            continue;
                        }

                    }

                    if(iIntegration.load()){
                        betterSkyBlock.getLoggerHelper().info("Integration [ " + iIntegration.targetPlugin() + "-" + Bukkit.getPluginManager().getPlugin(iIntegration.targetPlugin()).getDescription().getVersion() + "] was successfully Loaded.");
                    }else {
                        betterSkyBlock.getLoggerHelper().info("Integration [ " + iIntegration.targetPlugin() + "-" + iIntegration.targetVersion() + "] cannot be Loaded.");
                    }
                }

            } catch (IllegalAccessException | InstantiationException ex) {
                BetterSkyBlock.getInstance().getLoggerHelper().info(ex.getLocalizedMessage());
            }

        }
    }

    public boolean isIntegrationLoaded(Integrations integrations){
        return integrations.isEnabled();
    }
}

