package com.infinitymc.virtualkasa.economy;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.config.ConfigManager;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;

public class EconomyManager {
    
    private final VirtualKasa plugin;
    private final ConfigManager config;
    private final DecimalFormat decimalFormat;
    
    public EconomyManager(VirtualKasa plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat = new DecimalFormat("#,##0." + "0".repeat(config.getDecimalPlaces()), symbols);
    }
    
    public double getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }
    
    public double getBalance(UUID uuid) {
        return plugin.getDatabaseManager().getBalance(uuid);
    }
    
    public boolean setBalance(Player player, double amount) {
        return setBalance(player.getUniqueId(), amount);
    }
    
    public boolean setBalance(UUID uuid, double amount) {
        return plugin.getDatabaseManager().setBalance(uuid, amount);
    }
    
    public boolean addBalance(Player player, double amount) {
        return addBalance(player.getUniqueId(), amount);
    }
    
    public boolean addBalance(UUID uuid, double amount) {
        if (amount <= 0) return false;
        return plugin.getDatabaseManager().addBalance(uuid, amount);
    }
    
    public boolean removeBalance(Player player, double amount) {
        return removeBalance(player.getUniqueId(), amount);
    }
    
    public boolean removeBalance(UUID uuid, double amount) {
        if (amount <= 0) return false;
        return plugin.getDatabaseManager().removeBalance(uuid, amount);
    }
    
    public boolean hasBalance(Player player, double amount) {
        return hasBalance(player.getUniqueId(), amount);
    }
    
    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
    
    public boolean transferBalance(Player from, Player to, double amount) {
        return transferBalance(from.getUniqueId(), to.getUniqueId(), amount);
    }
    
    public boolean transferBalance(UUID fromUuid, UUID toUuid, double amount) {
        if (amount <= 0) return false;
        return plugin.getDatabaseManager().transferBalance(fromUuid, toUuid, amount);
    }
    
    public boolean withdrawPlayer(Player player, double amount) {
        if (!hasBalance(player, amount)) {
            return false;
        }
        return removeBalance(player, amount);
    }
    
    public boolean depositPlayer(Player player, double amount) {
        if (amount <= 0) return false;
        return addBalance(player, amount);
    }
    
    public String formatBalance(double amount) {
        return config.getCurrencySymbol() + decimalFormat.format(amount);
    }
    
    public String formatBalance(Player player) {
        return formatBalance(getBalance(player));
    }
    
    public String formatBalance(UUID uuid) {
        return formatBalance(getBalance(uuid));
    }
    
    public double getMaxBalance() {
        return config.getMaxBalance();
    }
    
    public double getStartingBalance() {
        return config.getStartingBalance();
    }
    
    public boolean isValidAmount(double amount) {
        return amount >= 0 && amount <= getMaxBalance();
    }
    
    public boolean createPlayerAccount(Player player) {
        return createPlayerAccount(player.getUniqueId(), player.getName());
    }
    
    public boolean createPlayerAccount(UUID uuid, String username) {
        if (plugin.getDatabaseManager().playerExists(uuid)) {
            return false;
        }
        
        plugin.getDatabaseManager().createPlayer(uuid, username);
        return true;
    }
    
    public boolean hasAccount(Player player) {
        return hasAccount(player.getUniqueId());
    }
    
    public boolean hasAccount(UUID uuid) {
        return plugin.getDatabaseManager().playerExists(uuid);
    }
}
