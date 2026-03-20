package com.infinitymc.virtualkasa;

import com.infinitymc.virtualkasa.commands.KasaCommand;
import com.infinitymc.virtualkasa.commands.AdminKasaCommand;
import com.infinitymc.virtualkasa.config.ConfigManager;
import com.infinitymc.virtualkasa.database.DatabaseManager;
import com.infinitymc.virtualkasa.economy.EconomyManager;
import com.infinitymc.virtualkasa.listeners.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class VirtualKasa extends JavaPlugin {
    
    private static VirtualKasa instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);
        economyManager = new EconomyManager(this);
        
        if (!databaseManager.connect()) {
            getLogger().severe("Veritabanı bağlantısı başarısız! Plugin devre dışı bırakılıyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        getCommand("kasa").setExecutor(new KasaCommand(this));
        getCommand("adminkasa").setExecutor(new AdminKasaCommand(this));
        
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExpansion().register();
            getLogger().info("PlaceholderAPI entegrasyonu aktif.");
        }
        
        getLogger().info("VirtualKasa plugini başarıyla aktif edildi!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("VirtualKasa plugini devre dışı bırakıldı.");
    }
    
    public static VirtualKasa getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
