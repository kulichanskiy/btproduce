package me.buildtoproduce.commands;

import me.buildtoproduce.SubCommand;
import me.buildtoproduce.db.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class NationCommand extends SubCommand {
	
	
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
            	
            	Player player = (Player) sender;
            	
                if (!sender.isOp()) {
                    sender.sendMessage("§cТолько админ может создать страну напрямую.");
                    return;
                }
                
                if(CooldownManager.isOnCooldown(player.getUniqueId(), "nation", 86400)) {
                	sender.sendMessage("§cКд сори.");
                    return;
                }
                String ownerName = player.getName();
                String ownerUUID = player.getUniqueId().toString();

                boolean success = DatabaseManager.createNation(name, ownerName, ownerUUID);
                if (success) {
                    player.sendMessage("§aСтрана '" + name + "' успешно создана.");
                    if(!sender.isOp()) {
                    	CooldownManager.setCooldown(player.getUniqueId(), "nation");
                    }
                } else {
                    player.sendMessage("§cНе удалось создать страну. Возможно, она уже существует.");
                }
                break;

            case "request":
                // TODO: log request to DB or queue
                sender.sendMessage("§aЗапрос на создание страны '" + name + "' отправлен.");
                break;

            case "delete":
                // TODO: check if owner or op
                sender.sendMessage("§cСтрана '" + name + "' удалена.");
                break;
                
            case "list":
                List<String> nations = DatabaseManager.listNations();
                if (nations.isEmpty()) {
                    sender.sendMessage("§7Нет зарегистрированных стран.");
                } else {
                    sender.sendMessage("§6Список стран:");
                    for (String entry : nations) {
                        sender.sendMessage(entry);
                    }
                }
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
