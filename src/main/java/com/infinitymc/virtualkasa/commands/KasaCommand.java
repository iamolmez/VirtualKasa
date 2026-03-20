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

public class KasaCommand implements CommandExecutor, TabCompleter {
    
    private final VirtualKasa plugin;
    private final EconomyManager economy;
    
    public KasaCommand(VirtualKasa plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!economy.hasAccount(player)) {
            economy.createPlayerAccount(player);
        }
        
        if (args.length == 0) {
            sendBalance(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "para":
            case "balance":
            case "bakiye":
                sendBalance(player);
                break;
                
            case "yatır":
            case "deposit":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /kasa yatır <miktar>");
                    return true;
                }
                handleDeposit(player, args[1]);
                break;
                
            case "çek":
            case "withdraw":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /kasa çek <miktar>");
                    return true;
                }
                handleWithdraw(player, args[1]);
                break;
                
            case "transfer":
            case "gönder":
                if (args.length < 3) {
                    player.sendMessage(plugin.getConfigManager().getPrefix() + "§cKullanım: /kasa transfer <oyuncu> <miktar>");
                    return true;
                }
                handleTransfer(player, args[1], args[2]);
                break;
                
            case "bilgi":
            case "info":
                sendInfo(player);
                break;
                
            case "yardım":
            case "help":
                sendHelp(player);
                break;
                
            default:
                player.sendMessage(plugin.getConfigManager().getPrefix() + "§cBilinmeyen komut! /kasa yardım yazarak komutları görebilirsiniz.");
                break;
        }
        
        return true;
    }
    
    private void sendBalance(Player player) {
        double balance = economy.getBalance(player);
        String message = plugin.getConfigManager().getMessage("balance")
            .replace("%balance%", economy.formatBalance(balance));
        player.sendMessage(plugin.getConfigManager().getPrefix() + message);
    }
    
    private void handleDeposit(Player player, String amountStr) {
        if (!player.hasPermission("virtualkasa.deposit")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                player.sendMessage(plugin.getConfigManager().getPrefix() + "§cMiktar 0'dan büyük olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        if (!economy.isValidAmount(amount)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cGeçersiz miktar!");
            return;
        }
        
        if (economy.addBalance(player, amount)) {
            String message = plugin.getConfigManager().getMessage("deposit")
                .replace("%amount%", economy.formatBalance(amount));
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
        } else {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cPara yatırma işlemi başarısız oldu!");
        }
    }
    
    private void handleWithdraw(Player player, String amountStr) {
        if (!player.hasPermission("virtualkasa.withdraw")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                player.sendMessage(plugin.getConfigManager().getPrefix() + "§cMiktar 0'dan büyük olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        if (!economy.hasBalance(player, amount)) {
            player.sendMessage(plugin.getConfigManager().getMessage("insufficient_funds"));
            return;
        }
        
        if (economy.removeBalance(player, amount)) {
            String message = plugin.getConfigManager().getMessage("withdraw")
                .replace("%amount%", economy.formatBalance(amount));
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
        } else {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cPara çekme işlemi başarısız oldu!");
        }
    }
    
    private void handleTransfer(Player player, String targetName, String amountStr) {
        if (!player.hasPermission("virtualkasa.transfer")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return;
        }
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }
        
        if (target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cKendinize para transfer edemezsiniz!");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                player.sendMessage(plugin.getConfigManager().getPrefix() + "§cMiktar 0'dan büyük olmalıdır!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid_number"));
            return;
        }
        
        if (!economy.hasBalance(player, amount)) {
            player.sendMessage(plugin.getConfigManager().getMessage("insufficient_funds"));
            return;
        }
        
        if (!economy.hasAccount(target)) {
            economy.createPlayerAccount(target);
        }
        
        if (economy.transferBalance(player, target, amount)) {
            String senderMessage = plugin.getConfigManager().getMessage("transfer")
                .replace("%target%", target.getName())
                .replace("%amount%", economy.formatBalance(amount));
            player.sendMessage(plugin.getConfigManager().getPrefix() + senderMessage);
            
            String receiverMessage = plugin.getConfigManager().getMessage("receive")
                .replace("%sender%", player.getName())
                .replace("%amount%", economy.formatBalance(amount));
            target.sendMessage(plugin.getConfigManager().getPrefix() + receiverMessage);
        } else {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cTransfer işlemi başarısız oldu!");
        }
    }
    
    private void sendInfo(Player player) {
        player.sendMessage("§6=== VirtualKasa Bilgi ===");
        player.sendMessage("§eBakiye: §a" + economy.formatBalance(player));
        player.sendMessage("§eMaksimum Bakiye: §a" + economy.formatBalance(economy.getMaxBalance()));
        player.sendMessage("§eBaşlangıç Bakiyesi: §a" + economy.formatBalance(economy.getStartingBalance()));
        player.sendMessage("§ePara Birimi: §a" + plugin.getConfigManager().getCurrencySymbol());
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== VirtualKasa Komutları ===");
        player.sendMessage("§e/kasa §7- Bakiyenizi gösterir");
        player.sendMessage("§e/kasa para §7- Bakiyenizi gösterir");
        player.sendMessage("§e/kasa yatır <miktar> §7- Hesabınıza para yatırır");
        player.sendMessage("§e/kasa çek <miktar> §7- Hesabınızdan para çeker");
        player.sendMessage("§e/kasa transfer <oyuncu> <miktar> §7- Para transfer eder");
        player.sendMessage("§e/kasa bilgi §7- Sistem bilgisi gösterir");
        player.sendMessage("§e/kasa yardım §7- Bu yardım menüsünü gösterir");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("para", "yatır", "çek", "transfer", "bilgi", "yardım");
            return subCommands.stream()
                .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2 && (args[0].equalsIgnoreCase("transfer") || args[0].equalsIgnoreCase("gönder"))) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return Arrays.asList();
    }
}
