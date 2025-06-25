package me.buildtoproduce.db;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:plugins/BuildToProduce/data.db";

    public static void initialize() {
    	CooldownManager.initialize();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS nations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    created_by TEXT NOT NULL,
                    owner TEXT NOT NULL,
                    owner_uuid TEXT NOT NULL,
                    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    approved BOOLEAN DEFAULT 0
                );
                
                CREATE TABLE IF NOT EXISTS businesses (
				    id INTEGER PRIMARY KEY AUTOINCREMENT,
				    name TEXT NOT NULL,
				    owner TEXT NOT NULL,
				    owner_uuid TEXT NOT NULL,
				    nation_id INTEGER NOT NULL,
				    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				    approved BOOLEAN DEFAULT 0,
				    FOREIGN KEY (nation_id) REFERENCES nations(id)
				);
				
				CREATE TABLE IF NOT EXISTS universities (
				    id INTEGER PRIMARY KEY AUTOINCREMENT,
				    name TEXT NOT NULL,
				    leader TEXT NOT NULL,
				    leader_uuid TEXT NOT NULL,
				    nation_id INTEGER NOT NULL,
				    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				    approved BOOLEAN DEFAULT 0,
				    FOREIGN KEY (nation_id) REFERENCES nations(id)
				);
				
				CREATE TABLE IF NOT EXISTS machines (
				    id INTEGER PRIMARY KEY AUTOINCREMENT,
				    name TEXT NOT NULL,
				    owner TEXT NOT NULL,
				    owner_uuid TEXT NOT NULL,
				    nation_id INTEGER NOT NULL,
				    business_id INTEGER,
				    coordinates TEXT NOT NULL,
				    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				    approved BOOLEAN DEFAULT 0,
				    FOREIGN KEY (nation_id) REFERENCES nations(id),
				    FOREIGN KEY (business_id) REFERENCES businesses(id)
				);

            """);
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
        }
    }
    
    public static boolean createNation(String name, String owner, String ownerUUID) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nations (name, created_by, owner, owner_uuid) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, name);
            ps.setString(2, owner);
            ps.setString(3, owner);
            ps.setString(4, ownerUUID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean deleteNation(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM nations WHERE name = ?");
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean approveNation(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("UPDATE nations SET approved = 1 WHERE name = ?");
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean transferNationOwnership(String name, String newOwner, String newOwnerUUID) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE nations SET owner = ?, owner_uuid = ? WHERE name = ?"
            );
            ps.setString(1, newOwner);
            ps.setString(2, newOwnerUUID);
            ps.setString(3, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static List<String> listNations() {
        List<String> nations = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("SELECT name, owner, approved FROM nations");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String owner = rs.getString("owner");
                boolean approved = rs.getBoolean("approved");
                String status = approved ? "§aОдобрено" : "§eНа рассмотрении";
                nations.add("§6" + name + " §7(владелец: " + owner + ", " + status + "§7)");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return nations;
    }


    // BUSINESS
    public static boolean requestBusiness(String name, String owner, String ownerUUID, String nationName) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement nationStmt = conn.prepareStatement("SELECT id FROM nations WHERE name = ?");
            nationStmt.setString(1, nationName);
            ResultSet rs = nationStmt.executeQuery();
            if (!rs.next()) return false;
            int nationId = rs.getInt("id");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO businesses (name, owner, owner_uuid, nation_id) 
                VALUES (?, ?, ?, ?)
            """);
            ps.setString(1, name);
            ps.setString(2, owner);
            ps.setString(3, ownerUUID);
            ps.setInt(4, nationId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean approveBusiness(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("UPDATE businesses SET approved = 1 WHERE name = ?");
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // UNIVERSITY
    public static boolean createUniversity(String name, String leader, String leaderUUID, String nationName) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement nationStmt = conn.prepareStatement("SELECT id FROM nations WHERE name = ?");
            nationStmt.setString(1, nationName);
            ResultSet rs = nationStmt.executeQuery();
            if (!rs.next()) return false;
            int nationId = rs.getInt("id");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO universities (name, leader, leader_uuid, nation_id)
                VALUES (?, ?, ?, ?)
            """);
            ps.setString(1, name);
            ps.setString(2, leader);
            ps.setString(3, leaderUUID);
            ps.setInt(4, nationId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean approveUniversity(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("UPDATE universities SET approved = 1 WHERE name = ?");
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // MACHINE
    public static boolean registerMachine(String name, String owner, String ownerUUID, String nationName, String businessName, String coordinates) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            int nationId = -1;
            int businessId = -1;

            PreparedStatement nationStmt = conn.prepareStatement("SELECT id FROM nations WHERE name = ?");
            nationStmt.setString(1, nationName);
            ResultSet nationRs = nationStmt.executeQuery();
            if (nationRs.next()) nationId = nationRs.getInt("id");
            else return false;

            if (businessName != null && !businessName.isEmpty()) {
                PreparedStatement businessStmt = conn.prepareStatement("SELECT id FROM businesses WHERE name = ?");
                businessStmt.setString(1, businessName);
                ResultSet businessRs = businessStmt.executeQuery();
                if (businessRs.next()) businessId = businessRs.getInt("id");
                else return false;
            }

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO machines (name, owner, owner_uuid, nation_id, business_id, coordinates)
                VALUES (?, ?, ?, ?, ?, ?)
            """);
            ps.setString(1, name);
            ps.setString(2, owner);
            ps.setString(3, ownerUUID);
            ps.setInt(4, nationId);
            if (businessId == -1) ps.setNull(5, java.sql.Types.INTEGER);
            else ps.setInt(5, businessId);
            ps.setString(6, coordinates);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        	Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean approveMachine(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("UPDATE machines SET approved = 1 WHERE name = ?");
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

}
