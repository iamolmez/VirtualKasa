package com.infinitymc.virtualkasa.config;

import com.infinitymc.virtualkasa.VirtualKasa;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final VirtualKasa plugin;
    private FileConfiguration config;
    
    public ConfigManager(VirtualKasa plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadDefaultConfig();
    }
    
    private void loadDefaultConfig() {
        config.addDefault("database.type", "sqlite");
        config.addDefault("database.sqlite.file", "database.db");
        config.addDefault("database.mysql.host", "localhost");
        config.addDefault("database.mysql.port", 3306);
        config.addDefault("database.mysql.database", "virtualkasa");
        config.addDefault("database.mysql.username", "root");
        config.addDefault("database.mysql.password", "");
        
        config.addDefault("economy.starting_balance", 1000.0);
        config.addDefault("economy.max_balance", 1000000.0);
        config.addDefault("economy.currency_symbol", "₺");
        config.addDefault("economy.decimal_places", 2);
        
        config.addDefault("messages.prefix", "&6[VirtualKasa]&r ");
        config.addDefault("messages.no_permission", "&cBu komutu kullanmak için yetkiniz yok!");
        config.addDefault("messages.player_only", "&cBu komutu sadece oyuncular kullanabilir!");
        config.addDefault("messages.invalid_number", "&cGeçersiz sayı!");
        config.addDefault("messages.insufficient_funds", "&cYeterli bakiyeniz yok!");
        config.addDefault("messages.player_not_found", "&cOyuncu bulunamadı!");
        config.addDefault("messages.balance", "&aBakiyeniz: %balance%");
        config.addDefault("messages.deposit", "&aHesabınıza %amount% yatırıldı!");
        config.addDefault("messages.withdraw", "&aHesabınızdan %amount% çekildi!");
        config.addDefault("messages.transfer", "&a%target% oyuncusuna %amount% transfer edildi!");
        config.addDefault("messages.receive", "&a%sender% oyuncusundan %amount% aldınız!");
        
        plugin.saveConfig();
    }
    
    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }
    
    public String getDatabaseFile() {
        return config.getString("database.sqlite.file", "database.db");
    }
    
    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }
    
    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }
    
    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "virtualkasa");
    }
    
    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }
    
    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "");
    }
    
    public double getStartingBalance() {
        return config.getDouble("economy.starting_balance", 1000.0);
    }
    
    public double getMaxBalance() {
        return config.getDouble("economy.max_balance", 1000000.0);
    }
    
    public String getCurrencySymbol() {
        return config.getString("economy.currency_symbol", "₺");
    }
    
    public int getDecimalPlaces() {
        return config.getInt("economy.decimal_places", 2);
    }
    
    public String getMessage(String path) {
        return config.getString("messages." + path, "&cMesaj bulunamadı: " + path);
    }
    
    public String getPrefix() {
        return getMessage("prefix");
    }
}
