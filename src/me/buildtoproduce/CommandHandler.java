package me.buildtoproduce;

import me.buildtoproduce.commands.*;
import org.bukkit.command.*;
import java.util.*;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> commands = new HashMap<>();

    public CommandHandler() {
        registerCommand(new VersionCommand());
        registerCommand(new ValidateCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new HelpCommand());
        registerCommand(new NationCommand());
        registerCommand(new BusinessCommand());
        registerCommand(new UniversityCommand());
    }

    private void registerCommand(SubCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§eИспользование: /btproduce <команда>");
            return true;
        }

        SubCommand sub = commands.get(args[0].toLowerCase());
        if (sub != null) {
            sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendMessage("§cНеизвестная команда. Используй /btproduce help");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String name : commands.keySet()) {
                if (name.startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
            return completions;
        }
        return null;
    }
}
