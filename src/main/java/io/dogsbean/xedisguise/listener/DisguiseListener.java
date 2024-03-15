package io.dogsbean.xedisguise.listener;

import io.dogsbean.xedisguise.XeDisguise;
import io.dogsbean.xedisguise.XePermissions;
import io.dogsbean.xedisguise.disguise.Disguise;
import io.dogsbean.xedisguise.disguise.DisguiseHandler;
import io.dogsbean.xedisguise.event.DisguiseEvent;
import io.dogsbean.xedisguise.event.UnDisguiseEvent;
import io.dogsbean.xedisguise.event.UnDisguiseForceEvent;
import io.dogsbean.xedisguise.utils.Skin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class DisguiseListener implements Listener {

    private final XeDisguise plugin;
    public DisguiseListener(XeDisguise plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisguise(DisguiseEvent e) {
        Player player = e.getPlayer();
        String originalName = e.getOriginalName();
        String disguiseName = e.getDisguisedName();
        for (Player onlinePlayers: Bukkit.getOnlinePlayers()) {
            if (onlinePlayers.hasPermission(XePermissions.CAN_SEE_OTHERS_DISGUISE)) {
                onlinePlayers.sendMessage(ChatColor.BLUE + "[XeDisguise] " + ChatColor.GREEN + originalName + ChatColor.YELLOW + " has disguised as " + ChatColor.GREEN + disguiseName + ChatColor.YELLOW + ".");
            }
        }
        DisguiseHandler disguiseHandler = new DisguiseHandler(plugin);;
        player.setCustomName(player.getName());
        player.setPlayerListName(disguiseName);
        player.setDisplayName(disguiseName);
        Skin.getSkinByName(disguiseName).thenAccept(skin -> {
            if (skin != null && !skin.equals(Skin.DEFAULT_SKIN)) {
                disguiseHandler.setSkin(player, skin);
            }
        });

        disguiseHandler.fetchName(player, disguiseName);
        Disguise disguise = new Disguise(player, originalName, disguiseName);
        plugin.getDisguiseManager().addDisguise(player, disguise);
    }

    @EventHandler
    public void onUnDisguise(UnDisguiseEvent e) {
        Player player = e.getPlayer();
        String originalName = e.getDisguise().getOriginalName();
        String disguisedName = e.getDisguise().getDisguisedName();
        for (Player onlinePlayers: Bukkit.getOnlinePlayers()) {
            if (onlinePlayers.hasPermission(XePermissions.CAN_SEE_OTHERS_DISGUISE)) {
                onlinePlayers.sendMessage(ChatColor.BLUE + "[XeDisguise] " + ChatColor.GREEN + originalName + ChatColor.GRAY + ChatColor.ITALIC + " (who disguised as " + ChatColor.RESET + ChatColor.GREEN + disguisedName + ChatColor.GRAY + ChatColor.ITALIC + ") just undisguised.");
            }
        }
        DisguiseHandler disguiseHandler = new DisguiseHandler(plugin);

        plugin.getDisguiseManager().removeDisguise(player);

        player.setCustomName(originalName);
        player.setPlayerListName(originalName);
        player.setDisplayName(originalName);

        Skin.getSkinByName(originalName).thenAccept(skin -> {
            if (skin != null && !skin.equals(Skin.DEFAULT_SKIN)) {
                disguiseHandler.setSkin(player, skin);
            }
        });

        disguiseHandler.fetchName(player, originalName);
    }

    @EventHandler
    public void onUnDisguised(UnDisguiseForceEvent e) {
        Disguise disguise = e.getDisguise();
        String originalName = disguise.getOriginalName();
        String disguisedName = disguise.getDisguisedName();
        for (Player onlinePlayers: Bukkit.getOnlinePlayers()) {
            if (onlinePlayers.hasPermission(XePermissions.CAN_SEE_OTHERS_DISGUISE)) {
                onlinePlayers.sendMessage(ChatColor.BLUE + "[XeDisguise] " + ChatColor.GREEN + originalName + ChatColor.GRAY + ChatColor.ITALIC + " (who disguised as " + ChatColor.RESET + ChatColor.GREEN + disguisedName + ChatColor.GRAY + ChatColor.ITALIC + ") just undisguised." + ChatColor.RESET + ChatColor.RED + " (DISCONNECTED)");
            }
        }
        Player originalPlayer = Bukkit.getPlayer(originalName);
        if (originalPlayer != null) {
            DisguiseHandler disguiseHandler = new DisguiseHandler(plugin);

            // 1. 플레이어의 디스가이즈 정보를 제거
            plugin.getDisguiseManager().removeDisguise(originalPlayer);

            // 2. 플레이어의 이름을 원래대로 설정
            originalPlayer.setCustomName(originalName);
            originalPlayer.setPlayerListName(originalName);
            originalPlayer.setDisplayName(originalName);

            // 3. 플레이어의 스킨을 원래대로 설정
            Skin.getSkinByName(originalName).thenAccept(skin -> {
                if (skin != null && !skin.equals(Skin.DEFAULT_SKIN)) {
                    disguiseHandler.setSkin(originalPlayer, skin);
                }
            });

            disguiseHandler.fetchName(originalPlayer, originalName);
            originalPlayer.kickPlayer(ChatColor.RED + "You were kicked for un-disguising.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (plugin.getDisguiseManager().isDisguised(player)) {
            UnDisguiseForceEvent unDisguiseForceEvent = new UnDisguiseForceEvent(player, plugin.getDisguiseManager().getDisguise(player));
            Bukkit.getPluginManager().callEvent(unDisguiseForceEvent);
            plugin.getDisguiseManager().removeDisguise(player);
        }
    }
}
