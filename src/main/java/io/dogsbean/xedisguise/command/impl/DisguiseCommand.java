package io.dogsbean.xedisguise.command.impl;

import io.dogsbean.xedisguise.XeDisguise;
import io.dogsbean.xedisguise.XePermissions;
import io.dogsbean.xedisguise.command.PlayerCommand;
import io.dogsbean.xedisguise.event.DisguiseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DisguiseCommand extends PlayerCommand {
    private final XeDisguise plugin;
    private final Map<Player, Long> lastDisguiseTime;

    public DisguiseCommand(XeDisguise plugin) {
        super("disguise");
        setAliases("nick");
        this.plugin = plugin;
        this.lastDisguiseTime = new HashMap<>();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /disguise <name> <skin>");
            return;
        }

        if (!player.hasPermission(XePermissions.CAN_DISGUISE)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
            return;
        }

        String nickname = args[0];
        String skin = args[1];

        if (Bukkit.getPlayer(nickname) != null && Bukkit.getPlayer(nickname).isOnline()) {
            player.sendMessage(ChatColor.RED + "That player is already online!");
            return;
        }

        if (plugin.getDisguiseManager().isDisguised(player)) {
            player.sendMessage(ChatColor.RED + "You are already disguised.");
            return;
        }

        if (!player.isOp()) {
            long currentTime = System.currentTimeMillis();
            if (lastDisguiseTime.containsKey(player)) {
                long lastDisguise = lastDisguiseTime.get(player);
                long cooldown = 3 * 1000;

                if (currentTime - lastDisguise < cooldown) {
                    long remainingCooldown = (cooldown - (currentTime - lastDisguise)) / 1000;
                    player.sendMessage(ChatColor.RED + "You must wait " + remainingCooldown + " seconds before disguising again.");
                    return;
                }
            }

            lastDisguiseTime.put(player, currentTime);
        }

        if (!isValidNickname(nickname)) {
            player.sendMessage(ChatColor.RED + "Invalid nickname. The nickname can only contain letters and numbers.");
            return;
        }

        if (args.length == 2) {
            DisguiseEvent disguiseEvent = new DisguiseEvent(player, player.getName(), nickname, skin, true);
            Bukkit.getServer().getPluginManager().callEvent(disguiseEvent);
            player.sendMessage(ChatColor.GREEN + "You are disguised!");
        } else {
            player.sendMessage("Usage: /disguise <nickname>");
        }
    }

    private boolean isValidNickname(String nickname) {
        return nickname.matches("[a-zA-Z0-9_]+");
    }
}
