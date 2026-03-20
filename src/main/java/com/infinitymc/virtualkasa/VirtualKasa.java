package com.infinitymc.virtualkasa;

import com.infinitymc.virtualkasa.commands.CrateCommand;
import com.infinitymc.virtualkasa.commands.AdminCrateCommand;
import com.infinitymc.virtualkasa.config.ConfigManager;
import com.infinitymc.virtualkasa.crates.CrateManager;
import com.infinitymc.virtualkasa.listeners.CrateListener;
import com.infinitymc.virtualkasa.listeners.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class VirtualKasa extends JavaPlugin {
    
    private static VirtualKasa instance;
    private ConfigManager configManager;
    private CrateManager crateManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        crateManager = new CrateManager(this);
        
        getCommand("kasa").setExecutor(new CrateCommand(this));
        getCommand("adminkasa").setExecutor(new AdminCrateCommand(this));
        
        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KasaPlaceholderExpansion().register();
            getLogger().info("PlaceholderAPI entegrasyonu aktif.");
        }
        
        getLogger().info("VirtualKasa (Crate Sistemi) plugini başarıyla aktif edildi!");
        getLogger().info("Yüklenen kasa sayısı: " + crateManager.getCrateCount());
    }
    
    @Override
    public void onDisable() {
        if (crateManager != null) {
            crateManager.saveData();
        }
        getLogger().info("VirtualKasa plugini devre dışı bırakıldı.");
    }
    
    public static VirtualKasa getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public CrateManager getCrateManager() {
        return crateManager;
    }
}
