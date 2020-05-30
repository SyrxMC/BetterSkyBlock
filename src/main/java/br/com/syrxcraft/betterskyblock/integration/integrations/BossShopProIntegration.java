package br.com.syrxcraft.betterskyblock.integration.integrations;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.integration.IIntegration;
import org.black_ixx.bossshop.BossShop;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.events.BSDisplayItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

//TODO: Rewrite
public class BossShopProIntegration implements IIntegration, Listener {

    private static BossShop bs;
    private static boolean isEnabled = false;

    @Override
    public String targetPlugin() {
        return "BossShopPro";
    }

    @Override
    public String targetVersion() {
        return "*";
    }

    @Override
    public boolean load() {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("BossShopPro");

        if(plugin == null){
            return false;
        }

        bs = (BossShop) plugin;
        isEnabled  = true;

        Bukkit.getPluginManager().registerEvents(this, BetterSkyBlock.getInstance());

        return true;
    }

    @EventHandler
    void onBossShopInventoryOpen(BSDisplayItemEvent event) {

        if (event.getShopItem().getName().equalsIgnoreCase("IslandLock")){

//            if (PlaceHolderIntegration.playersIslandIsPublic(event.getPlayer())) {
//                ItemStack itemStack = event.getShop().getItem("IslandLockPublic").getItem();
//                event.getShopItem().setItem(itemStack,true);
//            }else {
//                ItemStack itemStack = event.getShop().getItem("IslandLockPrivate").getItem();
//                event.getShopItem().setItem(itemStack,true);
//            }

        }
    }

    public static void openShop(Player player, String shopName){
        BSShop shop = bs.getAPI().getShop(shopName);
        bs.getAPI().openShop(player, shop);
    }

    public static boolean isEnabled(){
        return isEnabled;
    }

}
