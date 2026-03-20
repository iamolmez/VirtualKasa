package com.infinitymc.virtualkasa.crates;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON("§7Sıradan", ChatColor.GRAY, 70.0),
    UNCOMMON("§aNadir", ChatColor.GREEN, 20.0),
    RARE("§9Ender", ChatColor.BLUE, 7.0),
    EPIC("§5Destansı", ChatColor.DARK_PURPLE, 2.5),
    LEGENDARY("§6Efsanevi", ChatColor.GOLD, 0.5);
    
    private final String displayName;
    private final ChatColor color;
    private final double defaultChance;
    
    Rarity(String displayName, ChatColor color, double defaultChance) {
        this.displayName = displayName;
        this.color = color;
        this.defaultChance = defaultChance;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ChatColor getColor() {
        return color;
    }
    
    public double getDefaultChance() {
        return defaultChance;
    }
}
