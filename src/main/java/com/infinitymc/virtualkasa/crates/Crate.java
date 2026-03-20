package com.infinitymc.virtualkasa.crates;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Crate {
    
    private final String id;
    private String name;
    private ItemStack displayItem;
    private ItemStack keyItem;
    private final List<Reward> rewards = new ArrayList<>();
    
    public Crate(String id, ConfigurationSection section) {
        this.id = id;
        this.name = section.getString("name", id);
        this.displayItem = section.getItemStack("displayItem");
        this.keyItem = section.getItemStack("keyItem");
        
        ConfigurationSection rewardsSection = section.getConfigurationSection("rewards");
        if (rewardsSection != null) {
            for (String key : rewardsSection.getKeys(false)) {
                ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(key);
                if (rewardSection != null) {
                    rewards.add(new Reward(rewardSection));
                }
            }
        }
    }
    
    public Crate(String id, String name, ItemStack displayItem) {
        this.id = id;
        this.name = name;
        this.displayItem = displayItem;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ItemStack getDisplayItem() {
        return displayItem;
    }
    
    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }
    
    public ItemStack getKeyItem() {
        return keyItem;
    }
    
    public void setKeyItem(ItemStack keyItem) {
        this.keyItem = keyItem;
    }
    
    public List<Reward> getRewards() {
        return rewards;
    }
    
    public void addReward(Reward reward) {
        rewards.add(reward);
    }
    
    public Reward rollReward() {
        double totalChance = rewards.stream().mapToDouble(Reward::getChance).sum();
        double random = Math.random() * totalChance;
        
        double current = 0;
        for (Reward reward : rewards) {
            current += reward.getChance();
            if (random <= current) {
                return reward;
            }
        }
        
        return rewards.get(rewards.size() - 1);
    }
}
