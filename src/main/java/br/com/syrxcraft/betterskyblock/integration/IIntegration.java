package br.com.syrxcraft.betterskyblock.integration;

public interface IIntegration {

    String targetPlugin();
    String targetVersion();

    boolean load();

}
