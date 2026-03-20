package com.infinitymc.virtualkasa.commands;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.crates.Crate;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AdminCrateCommand implements CommandExecutor {
    
    private final VirtualKasa plugin;
    
    public AdminCrateCommand(VirtualKasa plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("virtualkasa.admin")) {
            sender.sendMessage("§cBunu yapmak için yetkiniz yok!");
            return true;
        }
        
        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                if (args.length < 2) {
                    sender.sendMessage("§cKullanım: /adminkasa create <id> <isim>");
                    return true;
                }
                createCrate(sender, args[1], args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : args[1]);
                break;
                
            case "delete":
                if (args.length < 2) {
                    sender.sendMessage("§cKullanım: /adminkasa delete <id>");
                    return true;
                }
                deleteCrate(sender, args[1]);
                break;
                
            case "setkey":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cKullanım: /adminkasa setkey <kasa_id>");
                    return true;
                }
                setKey((Player) sender, args[1]);
                break;
                
            case "setdisplay":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cKullanım: /adminkasa setdisplay <kasa_id>");
                    return true;
                }
                setDisplay((Player) sender, args[1]);
                break;
                
            case "reload":
                plugin.getCrateManager().loadCrates();
                sender.sendMessage("§aKasa konfigürasyonu yeniden yüklendi!");
                break;
                
            default:
                sendAdminHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("§6=== Admin Kasa Komutları ===");
        sender.sendMessage("§e/adminkasa create <id> <isim> §7- Yeni kasa oluşturur");
        sender.sendMessage("§e/adminkasa delete <id> §7- Kasa siler");
        sender.sendMessage("§e/adminkasa setkey <id> §7- Elinizdeki itemi anahtar yapar");
        sender.sendMessage("§e/adminkasa setdisplay <id> §7- Elinizdeki itemi kasa görseli yapar");
        sender.sendMessage("§e/adminkasa reload §7- Konfigürasyonu yeniler");
    }
    
    private void createCrate(CommandSender sender, String id, String name) {
        if (plugin.getCrateManager().getCrate(id) != null) {
            sender.sendMessage("§cBu ID'de bir kasa zaten var!");
            return;
        }
        
        ItemStack defaultItem = new ItemStack(Material.CHEST);
        ItemMeta meta = defaultItem.getItemMeta();
        meta.setDisplayName("§6" + name);
        defaultItem.setItemMeta(meta);
        
        plugin.getCrateManager().createCrate(id, name, defaultItem);
        sender.sendMessage("§a" + name + " kasası başarıyla oluşturuldu!");
        sender.sendMessage("§eKasa ayarlarını yapmak için:");
        sender.sendMessage("§e• Anahtar belirlemek: /adminkasa setkey " + id);
        sender.sendMessage("§e• Görsel belirlemek: /adminkasa setdisplay " + id);
    }
    
    private void deleteCrate(CommandSender sender, String id) {
        Crate crate = plugin.getCrateManager().getCrate(id);
        if (crate == null) {
            sender.sendMessage("§cBu ID'de bir kasa bulunamadı!");
            return;
        }
        
        plugin.getCrateManager().deleteCrate(id);
        sender.sendMessage("§a" + crate.getName() + " kasası silindi!");
    }
    
    private void setKey(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) {
            player.sendMessage("§cKasa bulunamadı!");
            return;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage("§cElinizde bir item tutmalısınız!");
            return;
        }
        
        crate.setKeyItem(item.clone());
        plugin.getCrateManager().saveData();
        player.sendMessage("§a" + crate.getName() + " kasasının anahtarı belirlendi!");
    }
    
    private void setDisplay(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) {
            player.sendMessage("§cKasa bulunamadı!");
            return;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage("§cElinizde bir item tutmalısınız!");
            return;
        }
        
        crate.setDisplayItem(item.clone());
        plugin.getCrateManager().saveData();
        player.sendMessage("§a" + crate.getName() + " kasasının görseli belirlendi!");
    }
}
