package me.buildtoproduce.commands;
import org.bukkit.command.CommandSender;

import me.buildtoproduce.SubCommand;

public class ReloadCommand extends SubCommand{
    public String getName() { return "reload"; }
    public String getDescription() { return "Reload plugin"; }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§aTest command");
    }
}
