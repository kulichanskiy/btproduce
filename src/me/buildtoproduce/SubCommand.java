package me.buildtoproduce;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public abstract String getName();
    public abstract String getDescription();
    public abstract void execute(CommandSender sender, String[] args);
}
