package com.infinitymc.virtualkasa;

import com.infinitymc.virtualkasa.crates.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class KasaPlaceholderExpansion extends PlaceholderExpansion {
    
    private final VirtualKasa plugin;
    
    public KasaPlaceholderExpansion() {
        this.plugin = VirtualKasa.getInstance();
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
        return "2.0.0";
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
        
        switch (params.toLowerCase()) {
            case "crate_count":
            case "kasa_sayisi":
                return String.valueOf(plugin.getCrateManager().getCrateCount());
                
            case "crate_list":
            case "kasa_listesi":
                StringBuilder crates = new StringBuilder();
                for (Crate crate : plugin.getCrateManager().getAllCrates()) {
                    if (crates.length() > 0) crates.append(", ");
                    crates.append(crate.getName());
                }
                return crates.toString();
                
            default:
                return null;
        }
    }
}
