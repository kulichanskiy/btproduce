package me.buildtoproduce.db;

import org.bukkit.Bukkit;

import java.io.*;
import me.buildtoproduce.*;
import java.util.Arrays;
import java.util.Comparator;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class BackupManager {

    private static final String DB_PATH = "plugins/BuildToProduce/BuildToProduce.db";
    private static final String BACKUP_DIR = "plugins/BuildToProduce/db_backups/";
    private static final int MAX_BACKUPS = 4;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static void initializeBackupScheduler() {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                performBackup();
            }
        }.runTaskTimer(BuildToProduce.getInstance(), 0L, 6 * 60 * 60 * 20L); // Каждые 6 часов (в тиках)
    }

    public static void performBackup() {
        try {
            File dir = new File(BACKUP_DIR);
            if (!dir.exists()) dir.mkdirs();

            String timestamp = java.time.LocalDateTime.now()
                .toString()
                .replace(":", "-")
                .replace("T", "_")
                .substring(0, 16);
            File backupFile = new File(dir, "backup_" + timestamp + ".sqlite");

            Files.copy(Paths.get(DB_PATH), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Bukkit.getLogger().info("[BuildToProduce] Бэкап создан: " + backupFile.getName());

            // Удаление старых бэкапов, если больше 4
            File[] backups = dir.listFiles((d, name) -> name.endsWith(".sqlite"));
            if (backups != null && backups.length > 4) {
                Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
                for (int i = 0; i < backups.length - 4; i++) {
                    backups[i].delete();
                    Bukkit.getLogger().info("[BuildToProduce] Старый бэкап удалён: " + backups[i].getName());
                }
            }

        } catch (IOException e) {
            Bukkit.getLogger().severe("[BuildToProduce] Ошибка при создании бэкапа: " + e.getMessage());
        }
    }

}
