package com.infinitymc.virtualkasa.listeners;

import com.infinitymc.virtualkasa.VirtualKasa;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final VirtualKasa plugin;
    
    public PlayerJoinListener(VirtualKasa plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getEconomyManager().hasAccount(event.getPlayer())) {
            plugin.getEconomyManager().createPlayerAccount(event.getPlayer());
            plugin.getLogger().info("Yeni oyuncu hesabı oluşturuldu: " + event.getPlayer().getName());
        }
    }
}
