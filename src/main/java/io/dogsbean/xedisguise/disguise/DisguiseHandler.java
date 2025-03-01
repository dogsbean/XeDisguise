package io.dogsbean.xedisguise.disguise;

import io.dogsbean.xedisguise.XeDisguise;
import io.dogsbean.xedisguise.utils.Skin;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutRespawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
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

    public void fetchName(Player player, String disguiseName, boolean undisguise) {
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
                refreshAsPlayer(player, undisguise);
            } else {
                refreshAsPlayer(player, undisguise);
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
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
    }

    public void refreshAsPlayer(final Player player, boolean undisguise) {
        if (player == null || !player.isOnline()) {
            return;
        }

        final Location location = player.getLocation();
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();

        PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo();
        removePacket.action = 4;
        removePacket.username = player.getPlayerListName();
        removePacket.player = ep.getProfile();
        ep.playerConnection.sendPacket(removePacket);

        if (undisguise) {
            player.sendMessage(ChatColor.YELLOW + ChatColor.ITALIC.toString() + ChatColor.BOLD + "Resetting the skin... please wait.");
        } else {
            player.sendMessage(ChatColor.YELLOW + ChatColor.ITALIC.toString() + ChatColor.BOLD + "Applying the skin... please wait.");
        }

        player.setMetadata("applytask", new FixedMetadataValue(plugin, true));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                ep.playerConnection.sendPacket(
                        new PacketPlayOutRespawn(
                                ep.dimension,
                                ep.getWorld().difficulty,
                                ep.world.getWorldData().getType(),
                                ep.playerInteractManager.getGameMode()
                        )
                );
                player.teleport(location);

                PacketPlayOutPlayerInfo addPacket = new PacketPlayOutPlayerInfo();
                addPacket.action = 0;
                addPacket.username = player.getPlayerListName();
                addPacket.player = ep.getProfile();
                addPacket.ping = ep.ping;
                addPacket.gamemode = ep.playerInteractManager.getGameMode().getId();
                ep.playerConnection.sendPacket(addPacket);

                player.updateInventory();
                for (final Player serverPlayer : Bukkit.getOnlinePlayers()) {
                    serverPlayer.hidePlayer(player);
                    serverPlayer.showPlayer(player);
                }

                if (!undisguise) {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Disguised!");
                    player.sendMessage(ChatColor.YELLOW + "You can undisguise by using the '/undisguise' command.");
                    player.sendMessage("");
                } else {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Undisguised!");
                    player.sendMessage(ChatColor.YELLOW + "You can disguise again by using the '/disguise' command.");
                    player.sendMessage("");
                }

                player.removeMetadata("applytask", plugin);
            }
        }.runTaskLater(plugin.getInstance(), 50L);
    }
}
