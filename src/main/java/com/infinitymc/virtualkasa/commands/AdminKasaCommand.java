package com.infinitymc.virtualkasa.commands;

import com.infinitymc.virtualkasa.VirtualKasa;
import com.infinitymc.virtualkasa.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminKasaCommand implements CommandExecutor, TabCompleter {
    
    private final VirtualKasa plugin;
    private final EconomyManager economy;
    
    public AdminKasaCommand(VirtualKasa plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("virtualkasa.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "ver":
            case "give":
                if (args.length < 3) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /adminkasa ver <oyuncu> <miktar>");
                    return true;
                }
                handleGive(sender, args[1], args[2]);
                break;
                
            case "al":
            case "take":
                if (args.length < 3) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /adminkasa al <oyuncu> <miktar>");
                    return true;
                }
                handleTake(sender, args[1], args[2]);
                break;
                
            case "ayarla":
            case "set":
                if (args.length < 3) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /adminkasa ayarla <oyuncu> <miktar>");
                    return true;
                }
                handleSet(sender, args[1], args[2]);
                break;
                
            case "sıfırla":
            case "reset":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /adminkasa sıfırla <oyuncu>");
                    return true;
                }
                handleReset(sender, args[1]);
                break;
                
            case "bak":
            case "check":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /adminkasa bak <oyuncu>");
                    return true;
                }
                handleCheck(sender, args[1]);
                break;
                
            case "top":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /adminkasa top <sayı>");
                    return true;
                }
                handleTop(sender, args[1]);
                break;
                
            case "yardım":
            case "help":
                sendAdminHelp(sender);
                break;
                
            default:
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cBilinmeyen komut! /adminkasa yardım yazarak komutları görebilirsiniz.");
                break;
        }
        
        return true;
    }
    
    private void handleGive(CommandSender sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cMiktar 0'dan büyük olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        if (!economy.isValidAmount(amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cGeçersiz miktar!");
            return;
        }
        
        if (!economy.hasAccount(target)) {
            economy.createPlayerAccount(target);
        }
        
        if (economy.addBalance(target, amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§a" + target.getName() + " oyuncusuna " + economy.formatBalance(amount) + " verildi.");
            target.sendMessage(plugin.getConfigManager().getPrefix() + "§aHesabınıza " + economy.formatBalance(amount) + " yatırıldı!");
        } else {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cPara verme işlemi başarısız oldu!");
        }
    }
    
    private void handleTake(CommandSender sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cMiktar 0'dan büyük olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        if (!economy.hasAccount(target)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cOyuncunun hesabı bulunmuyor!");
            return;
        }
        
        if (!economy.hasBalance(target, amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cOyuncunun yeterli bakiyesi yok!");
            return;
        }
        
        if (economy.removeBalance(target, amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§a" + target.getName() + " oyuncusundan " + economy.formatBalance(amount) + " alındı.");
            target.sendMessage(plugin.getConfigManager().getPrefix() + "§cHesabınızdan " + economy.formatBalance(amount) + " alındı!");
        } else {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cPara alma işlemi başarısız oldu!");
        }
    }
    
    private void handleSet(CommandSender sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cMiktar 0 veya daha büyük olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        if (!economy.isValidAmount(amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cGeçersiz miktar!");
            return;
        }
        
        if (!economy.hasAccount(target)) {
            economy.createPlayerAccount(target);
        }
        
        double oldBalance = economy.getBalance(target);
        
        if (economy.setBalance(target, amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§a" + target.getName() + " oyuncusunun bakiyesi " + 
                economy.formatBalance(oldBalance) + " -> " + economy.formatBalance(amount) + " olarak ayarlandı.");
            target.sendMessage(plugin.getConfigManager().getPrefix() + "§eHesabınızın bakiyesi " + economy.formatBalance(amount) + " olarak ayarlandı!");
        } else {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cBakiye ayarlama işlemi başarısız oldu!");
        }
    }
    
    private void handleReset(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }
        
        if (!economy.hasAccount(target)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cOyuncunun hesabı bulunmuyor!");
            return;
        }
        
        double startingBalance = economy.getStartingBalance();
        double oldBalance = economy.getBalance(target);
        
        if (economy.setBalance(target, startingBalance)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§a" + target.getName() + " oyuncusunun bakiyesi sıfırlandı (" + 
                economy.formatBalance(oldBalance) + " -> " + economy.formatBalance(startingBalance) + ").");
            target.sendMessage(plugin.getConfigManager().getPrefix() + "§eHesabınız sıfırlandı! Yeni bakiyeniz: " + economy.formatBalance(startingBalance));
        } else {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cSıfırlama işlemi başarısız oldu!");
        }
    }
    
    private void handleCheck(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }
        
        if (!economy.hasAccount(target)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cOyuncunun hesabı bulunmuyor!");
            return;
        }
        
        double balance = economy.getBalance(target);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + "§a" + target.getName() + "'nın bakiyesi: " + economy.formatBalance(balance));
    }
    
    private void handleTop(CommandSender sender, String amountStr) {
        int limit;
        try {
            limit = Integer.parseInt(amountStr);
            if (limit <= 0 || limit > 100) {
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cSayı 1-100 arasında olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        sender.sendMessage("§6=== En Zengin " + limit + " Oyuncu ===");
        
        plugin.getDatabaseManager().getTopBalances(limit).forEach((uuid, balance) -> {
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName != null) {
                sender.sendMessage("§e" + playerName + ": §a" + economy.formatBalance(balance));
            }
        });
    }
    
    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("§6=== Admin Kasa Komutları ===");
        sender.sendMessage("§e/adminkasa ver <oyuncu> <miktar> §7- Oyuncuya para verir");
        sender.sendMessage("§e/adminkasa al <oyuncu> <miktar> §7- Oyuncudan para alır");
        sender.sendMessage("§e/adminkasa ayarla <oyuncu> <miktar> §7- Oyuncunun bakiyesini ayarlar");
        sender.sendMessage("§e/adminkasa sıfırla <oyuncu> §7- Oyuncunun bakiyesini sıfırlar");
        sender.sendMessage("§e/adminkasa bak <oyuncu> §7- Oyuncunun bakiyesini gösterir");
        sender.sendMessage("§e/adminkasa top <sayı> §7- En zengin oyuncuları listeler");
        sender.sendMessage("§e/adminkasa yardım §7- Bu yardım menüsünü gösterir");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("virtualkasa.admin")) {
            return Arrays.asList();
        }
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("ver", "al", "ayarla", "sıfırla", "bak", "top", "yardım");
            return subCommands.stream()
                .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2 && !args[0].equalsIgnoreCase("top")) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return Arrays.asList();
    }
}
