package com.infinitymc.virtualkasa;

import com.infinitymc.virtualkasa.economy.EconomyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class KasaPlaceholderExpansion extends PlaceholderExpansion {
    
    private final VirtualKasa plugin;
    private final EconomyManager economy;
    
    public KasaPlaceholderExpansion() {
        this.plugin = VirtualKasa.getInstance();
        this.economy = plugin.getEconomyManager();
    }
    
    @Override
    public String getIdentifier() {
        return "virtualkasa";
    }
    
    @Override
    public String getAuthor() {
        return "InfinityMC";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }
        
        if (!economy.hasAccount(player)) {
            economy.createPlayerAccount(player);
        }
        
        switch (params.toLowerCase()) {
            case "balance":
            case "bakiye":
                return economy.formatBalance(player);
                
            case "balance_raw":
            case "bakiye_raw":
                return String.valueOf(economy.getBalance(player));
                
            case "balance_formatted":
            case "bakiye_formatted":
                return String.format("%,.2f", economy.getBalance(player));
                
            case "currency":
            case "para_birimi":
                return plugin.getConfigManager().getCurrencySymbol();
                
            case "max_balance":
            case "max_bakiye":
                return economy.formatBalance(economy.getMaxBalance());
                
            case "starting_balance":
            case "baslangic_bakiye":
                return economy.formatBalance(economy.getStartingBalance());
                
            default:
                return null;
        }
    }
}
