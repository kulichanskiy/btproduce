package me.buildtoproduce.commands;

import me.buildtoproduce.SubCommand;
import org.bukkit.command.CommandSender;

public class VersionCommand extends SubCommand {
    public String getName() { return "version"; }
    public String getDescription() { return "Показать версию плагина"; }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§aBuildToProduce v1.0");
    }
}
