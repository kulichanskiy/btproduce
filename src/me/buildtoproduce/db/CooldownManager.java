package me.buildtoproduce.db;

import org.bukkit.Bukkit;
import java.util.UUID;
import java.sql.*;


public class CooldownManager {
	private static final String DB_URL = "jdbc:sqlite:plugins/BuildToProduce/data.db";
	
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cooldowns (
				    player_uuid TEXT NOT NULL,
				    action_type TEXT NOT NULL, -- 'nation', 'business', 'university', 'machine'
				    last_action TIMESTAMP NOT NULL,
				    PRIMARY KEY (player_uuid, action_type)
                );
            """);
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
        }
        
        
    }
    
    public static void setCooldown(UUID uuid, String actionType) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO cooldowns (player_uuid, action_type, last_action)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT(player_uuid, action_type) DO UPDATE SET last_action = CURRENT_TIMESTAMP
            """);
            ps.setString(1, uuid.toString());
            ps.setString(2, actionType);
            ps.executeUpdate();
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
        }
    }
    
    public static boolean isOnCooldown(UUID uuid, String actionType, long cooldownSeconds) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT strftime('%s', 'now') - strftime('%s', last_action) AS seconds
                FROM cooldowns
                WHERE player_uuid = ? AND action_type = ?
            """);
            ps.setString(1, uuid.toString());
            ps.setString(2, actionType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long seconds = rs.getLong("seconds");
                return seconds < cooldownSeconds;
            }
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
        }
        return false;
    }
    
    public static void cleanupCooldowns(long maxCooldownMillis) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("""
                DELETE FROM cooldowns
                WHERE strftime('%s','now') - strftime('%s', last_action) > ?
            """);
            ps.setLong(1, maxCooldownMillis / 1000); // seconds
            ps.executeUpdate();
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
        }
    }

}
