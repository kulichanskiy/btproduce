package me.buildtoproduce.commands;
import org.bukkit.command.CommandSender;

import me.buildtoproduce.SubCommand;

public class HelpCommand extends SubCommand {
    public String getName() { return "help"; }
    public String getDescription() { return "help page"; }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§aTest command");
    }
}
