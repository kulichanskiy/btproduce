package me.buildtoproduce.commands;

import me.buildtoproduce.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UniversityCommand extends SubCommand {

    @Override
    public String getName() {
        return "nation";
    }

    @Override
    public String getDescription() {
        return "Управление странами";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /btproduce nation <create/request/delete/approve> <name>");
            return;
        }

        String action = args[0];
        String name = args[1];

        switch (action.toLowerCase()) {
            case "create":
                if (!sender.isOp()) {
                    sender.sendMessage("§cТолько админ может создать страну напрямую.");
                    return;
                }
                // TODO: create country
                sender.sendMessage("§aСтрана '" + name + "' создана.");
                break;

            case "request":
                // TODO: log request to DB or queue
                sender.sendMessage("§aЗапрос на создание страны '" + name + "' отправлен.");
                break;

            case "delete":
                // TODO: check if owner or op
                sender.sendMessage("§cСтрана '" + name + "' удалена.");
                break;

            case "approve":
                if (!sender.isOp()) {
                    sender.sendMessage("§cТолько админ может одобрить создание страны.");
                    return;
                }
                // TODO: approve request
                sender.sendMessage("§aСтрана '" + name + "' одобрена.");
                break;

            default:
                sender.sendMessage("§cНеизвестное действие: " + action);
        }
    }
}
