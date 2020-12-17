package br.com.syrxcraft.betterskyblock.border;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.WorldBorder;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class IslandBorder {

    public static void REGISTER(Plugin plugin){
        Bukkit.getPluginManager().registerEvents(new IslandBorderHandler(), plugin);
    }

    private static final HashMap<UUID, WorldBorder> borderCache = new HashMap<>();

    private static void dispatchPacket(Player player, double x, double z, double radius){

        BorderMode borderMode = BetterSkyBlock.getInstance().config().getBorderMode();

        if(borderMode != BorderMode.OFF){

            CraftPlayer craftPlayer = (CraftPlayer) player;
            PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;

            WorldBorder border = new WorldBorder();

            border.setCenter(x,z);
            border.setSize(radius);
            border.setWarningDistance(0);
            border.world = (WorldServer) (craftPlayer.getHandle()).world;

            switch (borderMode){

                case RED:{
                    border.transitionSizeBetween(radius, radius - 1.0D, 20000000L);
                    break;
                }

                case GREEN:{
                    border.transitionSizeBetween(radius - 0.2D, radius, 20000000L);
                    break;
                }

            }

            playerConnection.sendPacket(new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
            playerConnection.sendPacket(new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
            playerConnection.sendPacket(new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
            playerConnection.sendPacket(new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));

            if(borderMode != BorderMode.BLUE){
                playerConnection.sendPacket(new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
            }

            borderCache.put(player.getUniqueId(), border);

        }

    }

    public static boolean hasBorder(Player player){
        return borderCache.containsKey(player.getUniqueId());
    }

    public static void setBorder(Player player, double x, double z, double radius){
        new BukkitRunnable(){
            @Override
            public void run() {
                dispatchPacket(player, x, z, radius);
            }
        }.runTaskLater(BetterSkyBlock.getInstance(), 1);
    }

    public static void removeBorder(Player player) {

        if(!hasBorder(player)) return;

        setBorder(player, 0D, 0D, 2.147483647E9D);
        borderCache.remove(player.getUniqueId());
    }

    public enum BorderMode{

        OFF, RED, GREEN, BLUE;

        public static BorderMode fromString(String value){

            for(BorderMode borderMode : values()){
                if(borderMode.name().equalsIgnoreCase(value)) return borderMode;
            }

            return OFF;
        }

    }


}
