package io.dogsbean.xedisguise;

import io.dogsbean.xedisguise.command.BaseCommand;
import io.dogsbean.xedisguise.command.impl.DisguiseCommand;
import io.dogsbean.xedisguise.command.impl.IsDisguisedCommand;
import io.dogsbean.xedisguise.command.impl.UnDisguiseCommand;
import io.dogsbean.xedisguise.disguise.DisguiseManager;
import io.dogsbean.xedisguise.listener.DisguiseListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class XeDisguise extends JavaPlugin {

    private XeDisguise instance;
    private DisguiseManager disguiseManager;

    @Override
    public void onEnable() {
        instance = this;

        disguiseManager = new DisguiseManager();

        registerListeners();
        registerCommands(
                new DisguiseCommand(this),
                new UnDisguiseCommand(this),
                new IsDisguisedCommand(this)
        );
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DisguiseListener(this), this);
    }

    public void registerCommands(BaseCommand... commands) {
        CommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();

        for (BaseCommand command : commands) {
            commandMap.register(getName(), command);
        }
    }
}
