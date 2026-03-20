package com.infinitymc.virtualkasa.gui;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.crates.Crate;
import com.infinitymc.virtualkasa.crates.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CrateOpenGUI {
    
    private final VirtualKasa plugin;
    private final Player player;
    private final Crate crate;
    private final Inventory gui;
    private final Random random;
    
    private int ticks = 0;
    private final int maxTicks = 40; // 4 seconds
    private final List<Integer> rollSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16);
    private List<Reward> rollRewards;
    
    public CrateOpenGUI(VirtualKasa plugin, Player player, Crate crate) {
        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
        this.random = new Random();
        this.gui = Bukkit.createInventory(null, 27, "§6§l" + crate.getName() + " Açılıyor...");
        
        setupGUI();
        player.openInventory(gui);
        
        startAnimation();
    }
    
    private void setupGUI() {
        // Fill borders with glass
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i == 9 || i == 17) {
                gui.setItem(i, border);
            }
        }
        
        // Initialize roll rewards
        rollRewards = new ArrayList<>();
        for (int i = 0; i < rollSlots.size(); i++) {
            rollRewards.add(crate.rollReward());
        }
        updateDisplay();
    }
    
    private void startAnimation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    finishRoll();
                    cancel();
                    return;
                }
                
                // Shift rewards
                rollRewards.remove(rollRewards.size() - 1);
                rollRewards.add(0, crate.rollReward());
                updateDisplay();
                
                // Play sound
                if (ticks < 20) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                } else if (ticks < 30) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }
    
    private void updateDisplay() {
        for (int i = 0; i < rollSlots.size(); i++) {
            int slot = rollSlots.get(i);
            Reward reward = rollRewards.get(i);
            gui.setItem(slot, reward.getItem());
        }
        
        // Highlight middle slot
        ItemStack highlight = createItem(Material.YELLOW_STAINED_GLASS_PANE, "§e§l» ÖDÜL «");
        gui.setItem(4, highlight);
        gui.setItem(22, highlight);
    }
    
    private void finishRoll() {
        Reward finalReward = rollRewards.get(3); // Middle slot
        
        // Give reward
        player.getInventory().addItem(finalReward.getItem());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Broadcast if legendary/epic
        if (finalReward.isBroadcast() || finalReward.getRarity().ordinal() >= 3) {
            Bukkit.broadcastMessage("§6§l» §e" + player.getName() + " §6adlı oyuncu §r" + crate.getName() + 
                " §6kasasından §r" + finalReward.getRarity().getColor() + finalReward.getItem().getItemMeta().getDisplayName() + 
                " §6kazandı!");
        }
        
        // Close GUI after delay
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.sendMessage("§aTebrikler! §e" + crate.getName() + " §akasasından ödül kazandınız!");
                player.sendMessage("§7Kazanan: " + finalReward.getRarity().getColor() + finalReward.getItem().getItemMeta().getDisplayName());
            }
        }.runTaskLater(plugin, 40L);
    }
    
    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
