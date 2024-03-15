package io.dogsbean.xedisguise.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

/**
 * Credit: github.com/Paroxial/pCore
 * Author: Paroxial
 */

public abstract class BaseCommand extends Command {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    protected BaseCommand(String name) {
        super(name);
    }

    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args) {
        execute(sender, args);
        return true;
    }

    protected final void setAliases(String... aliases) {
        if (aliases.length > 0) {
            setAliases(aliases.length == 1 ? Collections.singletonList(aliases[0]) : Arrays.asList(aliases));
        }
    }

    protected final void setUsage(String... uses) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < uses.length; i++) {
            String use = uses[i];

            builder.append(use);

            if (i + 1 != uses.length) {
                builder.append(LINE_SEPARATOR);
            }
        }

        setUsage(builder.toString());
    }

    protected abstract void execute(CommandSender sender, String[] args);
}
