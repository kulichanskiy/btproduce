package me.buildtoproduce.commands;

import me.buildtoproduce.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BusinessCommand extends SubCommand {

    @Override
    public String getName() {
        return "business";
    }

    @Override
    public String getDescription() {
        return "Управление бизнессом";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /btproduce business <create/request/delete/approve> <name>");
            return;
        }

        String action = args[0];
        String name = args[1];

        switch (action.toLowerCase()) {
            case "create":
                // TODO: create business
                sender.sendMessage("§aБизнесс '" + name + "' создан.");
                break;

            case "delete":
                // TODO: check if owner or op
                sender.sendMessage("§cБизнесс '" + name + "' удалена.");
                break;

            default:
                sender.sendMessage("§cНеизвестное действие: " + action);
        }
    }
}
