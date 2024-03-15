package io.dogsbean.xedisguise.disguise;

import io.dogsbean.xedisguise.XeDisguise;
import io.dogsbean.xedisguise.utils.Skin;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PacketPlayOutRespawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class DisguiseHandler {

    private final XeDisguise plugin;

    public DisguiseHandler(XeDisguise plugin) {
        this.plugin = plugin;
    }

    public void fetchName(Player player, String disguiseName) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            boolean gameProfileExists = false;
            try {
                Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {
            }
            try {
                Class.forName("com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {
            }
            if (!gameProfileExists) {
                Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(entityPlayer, disguiseName);
            } else {
                Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                Field ff = profile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(profile, disguiseName);
            }
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                refreshPlayer(player);
            } else {
                refreshPlayer(player);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void setSkin(Player player, Skin skin) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        GameProfile gameProfile = entityPlayer.getProfile();
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
    }

    public void refreshPlayer(Player player) {
        Location loc = player.getLocation();
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(player.getLocation().getPitch());
        EntityPlayer ep = ((CraftPlayer) player).getHandle();

        new BukkitRunnable() {
            @Override
            public void run() {
                ep.playerConnection.sendPacket(new PacketPlayOutRespawn(ep.getWorld().worldProvider.dimension,
                        ep.getWorld().difficulty, ep.getWorld().worldData.getType(),
                        EnumGamemode.getById(player.getGameMode().getValue())));
                player.teleport(loc);
                player.updateInventory();
            }
        }.runTaskLater(plugin.getInstance(), 5);
    }
}
