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
        DisguiseHandler disguiseHandler = new DisguiseHandler(plugin);;
        player.setCustomName(player.getName());
        player.setPlayerListName(disguiseName);
        player.setDisplayName(disguiseName);
        Skin.getSkinByName(e.getSkin()).thenAccept(skin -> {
            if (skin != null && !skin.equals(Skin.DEFAULT_SKIN)) {
                disguiseHandler.setSkin(player, skin);
            }
        });

        disguiseHandler.fetchName(player, disguiseName, false);
        Disguise disguise = new Disguise(player, originalName, disguiseName);
        plugin.getDisguiseManager().addDisguise(player, disguise);
    }

    @EventHandler
    public void onUnDisguise(UnDisguiseEvent e) {
        Player player = e.getPlayer();
        String originalName = e.getDisguise().getOriginalName();
        DisguiseHandler disguiseHandler = new DisguiseHandler(plugin);
        plugin.getDisguiseManager().removeDisguise(player);

        player.setCustomName(originalName);
        player.setPlayerListName(originalName);
        player.setDisplayName(originalName);

        Skin.getSkinByName(originalName).thenAccept(skin -> {
            if (skin != null && !skin.equals(Skin.DEFAULT_SKIN)) {
                disguiseHandler.setSkin(player, skin);
            } else {
                player.sendMessage("Failed to fetch skin or default skin used.");
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            player.sendMessage("An error occurred while fetching the skin.");
            return null;
        });

        disguiseHandler.fetchName(player, originalName, true);
    }

    @EventHandler
    public void onUnDisguised(UnDisguiseForceEvent e) {
        Disguise disguise = e.getDisguise();
        String originalName = disguise.getOriginalName();
        Player originalPlayer = Bukkit.getPlayer(originalName);
        if (originalPlayer != null) {
            DisguiseHandler disguiseHandler = new DisguiseHandler(plugin);

            plugin.getDisguiseManager().removeDisguise(originalPlayer);

            originalPlayer.setCustomName(originalName);
            originalPlayer.setPlayerListName(originalName);
            originalPlayer.setDisplayName(originalName);

            Skin.getSkinByName(originalName).thenAccept(skin -> {
                if (skin != null && !skin.equals(Skin.DEFAULT_SKIN)) {
                    disguiseHandler.setSkin(originalPlayer, skin);
                } else {
                    originalPlayer.sendMessage("Failed to fetch skin or default skin used.");
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                originalPlayer.sendMessage("An error occurred while fetching the skin.");
                return null;
            });

            disguiseHandler.fetchName(originalPlayer, originalName, true);
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
