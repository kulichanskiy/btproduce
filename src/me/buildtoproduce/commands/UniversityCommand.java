package me.buildtoproduce.commands;

import me.buildtoproduce.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UniversityCommand extends SubCommand {

    @Override
    public String getName() {
        return "university";
    }

    @Override
    public String getDescription() {
        return "Управление университетами";
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
                if (!sender.isOp()) {
                    sender.sendMessage("§cТолько админ может создать университет напрямую.");
                    return;
                }
                // TODO: create uni
                sender.sendMessage("§aУниверситет '" + name + "' создан.");
                break;

            case "request":
                // TODO: log request to DB or queue
                sender.sendMessage("§aЗапрос на создание университета '" + name + "' отправлен.");
                break;

            case "delete":
                // TODO: check if owner or op
                sender.sendMessage("§cУниверситет '" + name + "' удален.");
                break;

            case "approve":
                if (!sender.isOp()) {
                    sender.sendMessage("§cТолько админ может одобрить создание университета.");
                    return;
                }
                // TODO: approve request
                sender.sendMessage("§aУниверситет '" + name + "' одобрена.");
                break;

            default:
                sender.sendMessage("§cНеизвестное действие: " + action);
        }
    }
}
