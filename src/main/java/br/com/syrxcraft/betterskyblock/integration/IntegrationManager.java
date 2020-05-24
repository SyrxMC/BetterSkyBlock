package br.com.syrxcraft.betterskyblock.integration;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.integration.integrations.BossShopProIntegration;
import br.com.syrxcraft.betterskyblock.integration.integrations.PlaceHolderAPIIntegration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class IntegrationManager implements Listener {

    private BetterSkyBlock betterSkyBlock;

    private enum Integrations {

        PlaceHolderAPI(PlaceHolderAPIIntegration.class),
        BossShopPro(BossShopProIntegration.class);

        private final Class<?> clazz;

        Integrations(Class<?> clazz){
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }

    public IntegrationManager(BetterSkyBlock betterSkyBlock){
        this.betterSkyBlock = betterSkyBlock;
        Bukkit.getPluginManager().registerEvents(this, betterSkyBlock);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent enableEvent){

        for(Integrations integrations : Integrations.values()){
            
            try{
                Object instance  = integrations.getClazz().newInstance();
                
                if(instance.getClass().isAssignableFrom(IIntegration.class)){
                    IIntegration iIntegration = (IIntegration) instance;
                    
                    if(iIntegration.targetPlugin().equals(enableEvent.getPlugin().getName())){
                        if(!iIntegration.targetVersion().equals("*")){
                            if(!enableEvent.getPlugin().getDescription().getVersion().equals(iIntegration.targetVersion())){
                                betterSkyBlock.getLoggerHelper().info("The integration " + iIntegration.targetPlugin() + "cannot be loaded cause requires [" + iIntegration.targetVersion() + "] and was founded [" + enableEvent.getPlugin().getDescription().getVersion()+"]");
                                continue;
                            }
                        }

                        if(iIntegration.load()){
                            betterSkyBlock.getLoggerHelper().info("Integration [ " + iIntegration.targetPlugin() + "-" + iIntegration.targetVersion() + "] was successfully Loaded.");
                        }else {
                            betterSkyBlock.getLoggerHelper().info("Integration [ " + iIntegration.targetPlugin() + "-" + iIntegration.targetVersion() + "] cannot be Loaded.");
                        }
                    }
                }
            } catch (IllegalAccessException | InstantiationException ignored) { }
        }
    }

}
