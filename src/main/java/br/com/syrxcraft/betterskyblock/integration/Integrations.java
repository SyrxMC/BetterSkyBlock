package br.com.syrxcraft.betterskyblock.integration;

import br.com.syrxcraft.betterskyblock.integration.integrations.BossShopProIntegration;
import br.com.syrxcraft.betterskyblock.integration.integrations.PlaceHolderAPIIntegration;

public enum Integrations {

    PlaceHolderAPI(PlaceHolderAPIIntegration.class, "PlaceHolderAPI"),
    BossShopPro(BossShopProIntegration.class, "BossShopPro");

    private final Class<?> clazz;

    private boolean isEnabled;
    private String pluginName;

    Integrations(Class<?> clazz, String pluginName){
        this.clazz = clazz;
        this.pluginName = pluginName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getPluginName() {
        return pluginName;
    }
}
