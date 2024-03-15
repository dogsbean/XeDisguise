package io.dogsbean.xedisguise.command.impl;

import io.dogsbean.xedisguise.XeDisguise;
import io.dogsbean.xedisguise.XePermissions;
import io.dogsbean.xedisguise.command.PlayerCommand;
import io.dogsbean.xedisguise.disguise.Disguise;
import io.dogsbean.xedisguise.event.UnDisguiseEvent;
import io.dogsbean.xedisguise.event.UnDisguiseForceEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UnDisguiseCommand extends PlayerCommand {

    private final XeDisguise plugin;

    public UnDisguiseCommand(XeDisguise plugin) {
        super("undisguise");
        setAliases("unnick");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            if (!player.hasPermission(XePermissions.CAN_UNDISGUISE)) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
                return;
            }
            undisguisePlayer(player);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (!player.hasPermission(XePermissions.CAN_UNDISGUISE_OTHERS)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
                    return;
                }
                undisguiseTarget(player, target);
            } else {
                player.sendMessage(ChatColor.RED + "Player not found!");
            }
        } else if (args.length == 2 && args[1].equalsIgnoreCase("-f")) { // /undisguise <target> -f 입력한 경우
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (!player.hasPermission(XePermissions.CAN_FORCE_UNDISGUISE_OTHERS)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
                    return;
                }
                undisguiseTargetForce(player, target);
            } else {
                player.sendMessage(ChatColor.RED + "Player not found!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /undisguise");
        }
    }

    private void undisguisePlayer(Player player) {
        if (!plugin.getDisguiseManager().isDisguised(player)) {
            player.sendMessage(ChatColor.RED + "You are not disguised!");
            return;
        }

        UnDisguiseEvent unDisguiseEvent = new UnDisguiseEvent(player, plugin.getDisguiseManager().getDisguise(player));
        Bukkit.getPluginManager().callEvent(unDisguiseEvent);
        player.sendMessage(ChatColor.GREEN + "You are undisguised!");
    }

    private void undisguiseTarget(Player sender, Player player) {
        if (!plugin.getDisguiseManager().isDisguised(player)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not disguised!");
            return;
        }

        UnDisguiseEvent unDisguiseEvent = new UnDisguiseEvent(player, plugin.getDisguiseManager().getDisguise(player));
        Bukkit.getPluginManager().callEvent(unDisguiseEvent);
    }

    private void undisguiseTargetForce(Player sender, Player target) {
        if (!plugin.getDisguiseManager().isDisguised(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not disguised!");
            return;
        }

        Disguise disguise = plugin.getDisguiseManager().getDisguise(target);
        UnDisguiseForceEvent unDisguiseForceEvent = new UnDisguiseForceEvent(target, disguise);
        Bukkit.getPluginManager().callEvent(unDisguiseForceEvent);
    }
}
