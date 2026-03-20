package com.infinitymc.virtualkasa.crates;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Reward {
    
    private ItemStack item;
    private double chance;
    private Rarity rarity;
    private boolean broadcast;
    private String command;
    
    public Reward(ConfigurationSection section) {
        this.item = section.getItemStack("item");
        this.chance = section.getDouble("chance", 1.0);
        this.rarity = Rarity.valueOf(section.getString("rarity", "COMMON"));
        this.broadcast = section.getBoolean("broadcast", false);
        this.command = section.getString("command", null);
    }
    
    public Reward(ItemStack item, double chance, Rarity rarity) {
        this.item = item;
        this.chance = chance;
        this.rarity = rarity;
        this.broadcast = false;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public double getChance() {
        return chance;
    }
    
    public void setChance(double chance) {
        this.chance = chance;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
    
    public boolean isBroadcast() {
        return broadcast;
    }
    
    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public boolean hasCommand() {
        return command != null && !command.isEmpty();
    }
}
