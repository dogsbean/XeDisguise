package io.dogsbean.xedisguise.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Credit: github.com/Paroxial/pCore
 * Author: Paroxial
 */
public abstract class PlayerCommand extends BaseCommand {
    protected PlayerCommand(String name) {
        super(name);
    }

    @Override
    protected final void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            execute((Player) sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can perform this command.");
        }
    }

    public abstract void execute(Player player, String[] args);
}
