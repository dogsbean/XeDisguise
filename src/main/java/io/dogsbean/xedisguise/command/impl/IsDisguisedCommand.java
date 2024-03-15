package io.dogsbean.xedisguise.command.impl;

import io.dogsbean.xedisguise.XeDisguise;
import io.dogsbean.xedisguise.XePermissions;
import io.dogsbean.xedisguise.command.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class IsDisguisedCommand extends PlayerCommand {

    private final XeDisguise plugin;

    public IsDisguisedCommand(XeDisguise plugin) {
        super("isdisguised");
        setAliases("realname");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /isdisguised <target>");
            return;
        }

        if (!player.hasPermission(XePermissions.CAN_SEE_DISGUISED_PLAYERS_REALNAME)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        if (!plugin.getDisguiseManager().isDisguised(target)) {
            player.sendMessage(ChatColor.RED + target.getName() + " is not disguised!");
            return;
        }

        String realName = plugin.getDisguiseManager().getDisguise(target).getOriginalName();
        player.sendMessage(ChatColor.YELLOW + target.getDisplayName() + "'s real name: " + ChatColor.WHITE + realName);
    }
}
