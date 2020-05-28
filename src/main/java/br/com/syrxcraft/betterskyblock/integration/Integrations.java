package br.com.syrxcraft.betterskyblock.integration;

import br.com.syrxcraft.betterskyblock.integration.integrations.BossShopProIntegration;
import br.com.syrxcraft.betterskyblock.integration.integrations.PlaceHolderAPIIntegration;

public enum Integrations {

    PlaceHolderAPI(PlaceHolderAPIIntegration.class),
    BossShopPro(BossShopProIntegration.class);

    private final Class<?> clazz;

    private boolean isEnabled;

    Integrations(Class<?> clazz){
        this.clazz = clazz;
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

}
