package com.infinitymc.virtualkasa.crates;

import com.infinitymc.virtualkasa.VirtualKasa;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CrateManager {
    
    private final VirtualKasa plugin;
    private final Map<String, Crate> crates = new HashMap<>();
    private final Map<Location, String> crateLocations = new HashMap<>();
    private File cratesFile;
    private FileConfiguration cratesConfig;
    
    public CrateManager(VirtualKasa plugin) {
        this.plugin = plugin;
        loadCrates();
    }
    
    public void loadCrates() {
        cratesFile = new File(plugin.getDataFolder(), "crates.yml");
        if (!cratesFile.exists()) {
            plugin.saveResource("crates.yml", false);
        }
        cratesConfig = YamlConfiguration.loadConfiguration(cratesFile);
        
        ConfigurationSection cratesSection = cratesConfig.getConfigurationSection("crates");
        if (cratesSection != null) {
            for (String crateId : cratesSection.getKeys(false)) {
                ConfigurationSection crateSection = cratesSection.getConfigurationSection(crateId);
                if (crateSection != null) {
                    Crate crate = new Crate(crateId, crateSection);
                    crates.put(crateId, crate);
                }
            }
        }
        
        // Load physical locations
        ConfigurationSection locationsSection = cratesConfig.getConfigurationSection("locations");
        if (locationsSection != null) {
            for (String key : locationsSection.getKeys(false)) {
                Location loc = (Location) locationsSection.get(key);
                String crateId = locationsSection.getString(key + ".crate");
                if (loc != null && crateId != null) {
                    crateLocations.put(loc, crateId);
                }
            }
        }
    }
    
    public void saveData() {
        try {
            cratesConfig.save(cratesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Kasa verileri kaydedilemedi: " + e.getMessage());
        }
    }
    
    public Crate getCrate(String id) {
        return crates.get(id);
    }
    
    public Collection<Crate> getAllCrates() {
        return crates.values();
    }
    
    public int getCrateCount() {
        return crates.size();
    }
    
    public void createCrate(String id, String name, ItemStack displayItem) {
        Crate crate = new Crate(id, name, displayItem);
        crates.put(id, crate);
        saveCrateToConfig(crate);
    }
    
    public void deleteCrate(String id) {
        crates.remove(id);
        cratesConfig.set("crates." + id, null);
        saveData();
    }
    
    public void addCrateLocation(Location loc, String crateId) {
        crateLocations.put(loc, crateId);
        String path = "locations." + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
        cratesConfig.set(path + ".location", loc);
        cratesConfig.set(path + ".crate", crateId);
        saveData();
    }
    
    public void removeCrateLocation(Location loc) {
        crateLocations.remove(loc);
        String path = "locations." + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
        cratesConfig.set(path, null);
        saveData();
    }
    
    public String getCrateAtLocation(Location loc) {
        return crateLocations.get(loc);
    }
    
    private void saveCrateToConfig(Crate crate) {
        String path = "crates." + crate.getId() + ".";
        cratesConfig.set(path + "name", crate.getName());
        cratesConfig.set(path + "displayItem", crate.getDisplayItem());
        cratesConfig.set(path + "keyItem", crate.getKeyItem());
        
        List<Map<String, Object>> rewardsList = new ArrayList<>();
        for (Reward reward : crate.getRewards()) {
            Map<String, Object> rewardMap = new HashMap<>();
            rewardMap.put("item", reward.getItem());
            rewardMap.put("chance", reward.getChance());
            rewardMap.put("rarity", reward.getRarity().name());
            rewardMap.put("broadcast", reward.isBroadcast());
            rewardsList.add(rewardMap);
        }
        cratesConfig.set(path + "rewards", rewardsList);
        saveData();
    }
}
