package com.infinitymc.virtualkasa.database;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.config.ConfigManager;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    
    private final VirtualKasa plugin;
    private Connection connection;
    private final ConfigManager config;
    
    public DatabaseManager(VirtualKasa plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }
    
    public boolean connect() {
        try {
            String databaseType = config.getDatabaseType().toLowerCase();
            
            if (databaseType.equals("sqlite")) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + config.getDatabaseFile());
            } else if (databaseType.equals("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                    "jdbc:mysql://" + config.getMySQLHost() + ":" + config.getMySQLPort() + "/" + config.getMySQLDatabase(),
                    config.getMySQLUsername(),
                    config.getMySQLPassword()
                );
            } else {
                plugin.getLogger().severe("Desteklenmeyen veritabanı türü: " + databaseType);
                return false;
            }
            
            createTables();
            plugin.getLogger().info("Veritabanı bağlantısı başarıyla kuruldu.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Veritabanı bağlantı hatası: " + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Veritabanı kapatma hatası: " + e.getMessage());
        }
    }
    
    private void createTables() throws SQLException {
        String databaseType = config.getDatabaseType().toLowerCase();
        String autoIncrement = databaseType.equals("sqlite") ? "INTEGER PRIMARY KEY AUTOINCREMENT" : "INT AUTO_INCREMENT PRIMARY KEY";
        
        String createPlayerTable = "CREATE TABLE IF NOT EXISTS players (" +
            "id " + autoIncrement + ", " +
            "uuid VARCHAR(36) UNIQUE NOT NULL, " +
            "username VARCHAR(16) NOT NULL, " +
            "balance DOUBLE DEFAULT " + config.getStartingBalance() + ", " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS transactions (" +
            "id " + autoIncrement + ", " +
            "player_uuid VARCHAR(36) NOT NULL, " +
            "type VARCHAR(20) NOT NULL, " +
            "amount DOUBLE NOT NULL, " +
            "balance_before DOUBLE NOT NULL, " +
            "balance_after DOUBLE NOT NULL, " +
            "description TEXT, " +
            "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayerTable);
            stmt.execute(createTransactionTable);
        }
    }
    
    public boolean playerExists(UUID uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT uuid FROM players WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Oyuncu kontrol hatası: " + e.getMessage());
            return false;
        }
    }
    
    public void createPlayer(UUID uuid, String username) {
        if (playerExists(uuid)) return;
        
        try (PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO players (uuid, username, balance) VALUES (?, ?, ?)")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, username);
            stmt.setDouble(3, config.getStartingBalance());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Oyuncu oluşturma hatası: " + e.getMessage());
        }
    }
    
    public double getBalance(UUID uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT balance FROM players WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Bakiye okuma hatası: " + e.getMessage());
        }
        return 0.0;
    }
    
    public boolean setBalance(UUID uuid, double newBalance) {
        if (newBalance < 0 || newBalance > config.getMaxBalance()) {
            return false;
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(
            "UPDATE players SET balance = ? WHERE uuid = ?")) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, uuid.toString());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Bakiye ayarlama hatası: " + e.getMessage());
            return false;
        }
    }
    
    public boolean addBalance(UUID uuid, double amount) {
        double currentBalance = getBalance(uuid);
        double newBalance = currentBalance + amount;
        
        if (newBalance > config.getMaxBalance()) {
            return false;
        }
        
        if (setBalance(uuid, newBalance)) {
            addTransaction(uuid, "DEPOSIT", amount, currentBalance, newBalance, "Para yatırma");
            return true;
        }
        return false;
    }
    
    public boolean removeBalance(UUID uuid, double amount) {
        double currentBalance = getBalance(uuid);
        
        if (currentBalance < amount) {
            return false;
        }
        
        double newBalance = currentBalance - amount;
        
        if (setBalance(uuid, newBalance)) {
            addTransaction(uuid, "WITHDRAW", amount, currentBalance, newBalance, "Para çekme");
            return true;
        }
        return false;
    }
    
    public boolean transferBalance(UUID fromUuid, UUID toUuid, double amount) {
        double fromBalance = getBalance(fromUuid);
        
        if (fromBalance < amount) {
            return false;
        }
        
        double toBalance = getBalance(toUuid);
        double newFromBalance = fromBalance - amount;
        double newToBalance = toBalance + amount;
        
        if (newToBalance > config.getMaxBalance()) {
            return false;
        }
        
        try {
            connection.setAutoCommit(false);
            
            if (setBalance(fromUuid, newFromBalance) && setBalance(toUuid, newToBalance)) {
                addTransaction(fromUuid, "TRANSFER_OUT", amount, fromBalance, newFromBalance, "Para gönderme");
                addTransaction(toUuid, "TRANSFER_IN", amount, toBalance, newToBalance, "Para alma");
                connection.commit();
                return true;
            }
            
            connection.rollback();
            return false;
        } catch (SQLException e) {
            plugin.getLogger().severe("Transfer hatası: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                plugin.getLogger().severe("Rollback hatası: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                plugin.getLogger().severe("AutoCommit ayarlama hatası: " + e.getMessage());
            }
        }
    }
    
    private void addTransaction(UUID uuid, String type, double amount, double balanceBefore, double balanceAfter, String description) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO transactions (player_uuid, type, amount, balance_before, balance_after, description) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setDouble(4, balanceBefore);
            stmt.setDouble(5, balanceAfter);
            stmt.setString(6, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("İşlem kaydetme hatası: " + e.getMessage());
        }
    }
    
    public Map<UUID, Double> getTopBalances(int limit) {
        Map<UUID, Double> topBalances = new HashMap<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT uuid, balance FROM players ORDER BY balance DESC LIMIT ?")) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                double balance = rs.getDouble("balance");
                topBalances.put(uuid, balance);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Zirve bakiyeleri okuma hatası: " + e.getMessage());
        }
        
        return topBalances;
    }
}
