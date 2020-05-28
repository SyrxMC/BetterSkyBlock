package br.com.syrxcraft.betterskyblock.data.providers;

public enum Providers {

    MySQL(MySQLDataProvider.class);

    Class<?> targetClass;

    Providers(Class<?> targetClass){
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public static Providers getFromName(String name){

        for(Providers providers : values()){
            if(providers.name().equalsIgnoreCase(name)) return providers;
        }

        return null;
    }

}
