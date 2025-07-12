package me.buildtoproduce.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.buildtoproduce.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public class ValidateCommand extends SubCommand {

    @Override
    public String getName() {
        return "validate";
    }

    @Override
    public String getDescription() {
        return "Валидирует структуру с WorldEdit выделением по YAML шаблону";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cКоманда доступна только игроку.");
            return;
        }

        if (args.length < 1) {
            player.sendMessage("§cИспользование: /btproduce validate <template_id>");
            return;
        }

        String templateId = args[0];

        // loading machines.yml
        File machinesFile = new File("plugins/BuildToProduce/machines.yml");
        String templatePath = null;
        String displayName = templateId;

        try (FileInputStream fis = new FileInputStream(machinesFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(fis);
            Map<String, Object> machine = (Map<String, Object>) root.get("machines");

            if (machine != null && machine.containsKey(templateId)) {
                Map<String, Object> machineData = (Map<String, Object>) machine.get(templateId);
                templatePath = (String) machineData.get("template_location");
                if (machineData.containsKey("displayed_name")) {
                    displayName = (String) machineData.get("displayed_name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§cОшибка при чтении конфигурации станков");
            return;
        }

        if (templatePath == null) {
            player.sendMessage("§cНе удалось найти путь к шаблону для '" + displayName + "'");
            return;
        }

        File templateFile = new File("plugins/BuildToProduce" + templatePath);
        if (!templateFile.exists()) {
            player.sendMessage("§cФайл шаблона не найден: " + templatePath);
            return;
        }

        // we check
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            player.sendMessage("§cWorldEdit не найден.");
            return;
        }

        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
        CuboidRegion region;
        try {
            region = (CuboidRegion) worldEdit.getSession(player).getSelection(wePlayer.getWorld());
        } catch (Exception e) {
            player.sendMessage("§cСначала выдели регион WorldEdit.");
            return;
        }

        if (region.getVolume() > 10000) {
            player.sendMessage("§cСлишком большой регион.");
            return;
        }

        BlockVector3 min = region.getMinimumPoint();
        Location origin = new Location(player.getWorld(), min.getBlockX(), min.getBlockY(), min.getBlockZ());

        // template loading
        List<Map<String, Object>> blocks;
        try (FileInputStream fis = new FileInputStream(templateFile)) {
            Yaml yaml = new Yaml(); // Без конструктора
            Map<String, Object> root = yaml.load(fis);
            blocks = (List<Map<String, Object>>) root.get("blocks");
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§cОшибка при загрузке шаблона блока.");
            return;
        }

        // validation
        int incorrect = 0;
        for (Map<String, Object> blockData : blocks) {
            List<Integer> pos = (List<Integer>) blockData.get("pos");
            String expectedType = (String) blockData.get("type");

            int checkX = origin.getBlockX() + pos.get(0);
            int checkY = origin.getBlockY() + pos.get(1);
            int checkZ = origin.getBlockZ() + pos.get(2);

            Material actual = player.getWorld().getBlockAt(checkX, checkY, checkZ).getType();
            String expectedShort = expectedType.replace("minecraft:", "").toUpperCase();

            if (!actual.name().equalsIgnoreCase(expectedShort)) {
                incorrect++;
                player.sendMessage("§cНеверный блок на (" + checkX + ", " + checkY + ", " + checkZ + "): ожидается " + expectedShort + ", найдено " + actual);
            }
        }

        if (incorrect == 0) {
            player.sendMessage("§aСтруктура валидна: " + displayName);
        } else {
            player.sendMessage("§eНайдено " + incorrect + " несоответствий.");
        }
    }
}
