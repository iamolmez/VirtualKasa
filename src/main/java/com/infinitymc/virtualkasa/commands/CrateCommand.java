package com.infinitymc.virtualkasa.commands;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.crates.Crate;
import com.infinitymc.virtualkasa.gui.CrateOpenGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrateCommand implements CommandExecutor, TabCompleter {
    
    private final VirtualKasa plugin;
    
    public CrateCommand(VirtualKasa plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
            case "liste":
                listCrates(player);
                break;
                
            case "open":
            case "aç":
                if (args.length < 2) {
                    player.sendMessage("§cKullanım: /kasa open <kasa_ismi>");
                    return true;
                }
                openCrate(player, args[1]);
                break;
                
            case "givekey":
            case "anahtarver":
                if (!player.hasPermission("virtualkasa.givekey")) {
                    player.sendMessage("§cBunu yapmak için yetkiniz yok!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cKullanım: /kasa givekey <oyuncu> <kasa_ismi> [miktar]");
                    return true;
                }
                giveKey(player, args[1], args[2], args.length > 3 ? args[3] : "1");
                break;
                
            case "givecrate":
            case "kasaver":
                if (!player.hasPermission("virtualkasa.givecrate")) {
                    player.sendMessage("§cBunu yapmak için yetkiniz yok!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cKullanım: /kasa givecrate <oyuncu> <kasa_ismi> [miktar]");
                    return true;
                }
                giveCrate(player, args[1], args[2], args.length > 3 ? args[3] : "1");
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Kasa Sistemi Komutları ===");
        player.sendMessage("§e/kasa list §7- Mevcut kasaları listeler");
        player.sendMessage("§e/kasa open <kasa> §7- Kasa açar (anahtar gerekli)");
        if (player.hasPermission("virtualkasa.givekey")) {
            player.sendMessage("§e/kasa givekey <oyuncu> <kasa> [miktar] §7- Anahtar verir");
        }
        if (player.hasPermission("virtualkasa.givecrate")) {
            player.sendMessage("§e/kasa givecrate <oyuncu> <kasa> [miktar] §7- Kasa verir");
        }
    }
    
    private void listCrates(Player player) {
        player.sendMessage("§6=== Mevcut Kasalar ===");
        for (Crate crate : plugin.getCrateManager().getAllCrates()) {
            player.sendMessage("§e• " + crate.getName());
        }
    }
    
    private void openCrate(Player player, String crateName) {
        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            player.sendMessage("§cBu isimde bir kasa bulunamadı!");
            return;
        }
        
        if (!player.hasPermission("virtualkasa.open." + crate.getId())) {
            player.sendMessage("§cBu kasayı açmak için yetkiniz yok!");
            return;
        }
        
        ItemStack key = crate.getKeyItem();
        if (key == null) {
            player.sendMessage("§cBu kasa için anahtar tanımlanmamış!");
            return;
        }
        
        if (!player.getInventory().containsAtLeast(key, 1)) {
            player.sendMessage("§cBu kasayı açmak için anahtarınız yok!");
            return;
        }
        
        player.getInventory().removeItem(key);
        new CrateOpenGUI(plugin, player, crate);
    }
    
    private void giveKey(CommandSender sender, String targetName, String crateName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cOyuncu bulunamadı!");
            return;
        }
        
        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            sender.sendMessage("§cKasa bulunamadı!");
            return;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            amount = 1;
        }
        
        ItemStack key = crate.getKeyItem().clone();
        key.setAmount(amount);
        target.getInventory().addItem(key);
        
        sender.sendMessage("§a" + target.getName() + " adlı oyuncuya " + amount + " adet " + crate.getName() + " anahtarı verildi.");
        target.sendMessage("§a" + amount + " adet " + crate.getName() + " anahtarı aldınız!");
    }
    
    private void giveCrate(CommandSender sender, String targetName, String crateName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cOyuncu bulunamadı!");
            return;
        }
        
        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            sender.sendMessage("§cKasa bulunamadı!");
            return;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            amount = 1;
        }
        
        ItemStack crateItem = crate.getDisplayItem().clone();
        crateItem.setAmount(amount);
        target.getInventory().addItem(crateItem);
        
        sender.sendMessage("§a" + target.getName() + " adlı oyuncuya " + amount + " adet " + crate.getName() + " verildi.");
        target.sendMessage("§a" + amount + " adet " + crate.getName() + " aldınız!");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "open", "givekey", "givecrate");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
            return plugin.getCrateManager().getAllCrates().stream()
                .map(Crate::getId)
                .collect(Collectors.toList());
        }
        
        if (args.length == 2 && (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("givecrate"))) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
        
        return Arrays.asList();
    }
}
