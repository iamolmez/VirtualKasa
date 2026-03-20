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
        // Crate sistemi için herhangi bir başlangıç işlemi gerekmez
        plugin.getLogger().info("Oyuncu katıldı: " + event.getPlayer().getName());
    }
}
