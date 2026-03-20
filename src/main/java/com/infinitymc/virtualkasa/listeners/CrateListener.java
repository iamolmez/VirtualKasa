package com.infinitymc.virtualkasa.listeners;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.crates.Crate;
import com.infinitymc.virtualkasa.gui.CrateOpenGUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {
    
    private final VirtualKasa plugin;
    
    public CrateListener(VirtualKasa plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item == null || item.getType().isAir()) {
            return;
        }
        
        // Check if player is holding a crate
        for (Crate crate : plugin.getCrateManager().getAllCrates()) {
            if (isSimilarItem(item, crate.getDisplayItem())) {
                event.setCancelled(true);
                
                if (!player.hasPermission("virtualkasa.open." + crate.getId())) {
                    player.sendMessage("§cBu kasayı açmak için yetkiniz yok!");
                    return;
                }
                
                // Check for key
                ItemStack key = crate.getKeyItem();
                if (key == null) {
                    player.sendMessage("§cBu kasa için anahtar tanımlanmamış!");
                    return;
                }
                
                if (!player.getInventory().containsAtLeast(key, 1)) {
                    player.sendMessage("§cBu kasayı açmak için anahtarınız yok!");
                    return;
                }
                
                // Remove key and open
                player.getInventory().removeItem(key.clone());
                new CrateOpenGUI(plugin, player, crate);
                return;
            }
        }
    }
    
    private boolean isSimilarItem(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;
        if (item1.getType() != item2.getType()) return false;
        
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            String name1 = item1.getItemMeta().getDisplayName();
            String name2 = item2.getItemMeta().getDisplayName();
            return name1 != null && name1.equals(name2);
        }
        
        return true;
    }
}
