package com.medicalcraft.commands;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.items.MicroscopeItem;
import com.medicalcraft.items.SyringeItem;
import com.medicalcraft.managers.MicroscopeManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MicroscopeCommand implements CommandExecutor {
    
    private final MedicalCraftPlugin plugin;
    private final MicroscopeItem microscopeItem;
    private final SyringeItem syringeItem;
    private final MicroscopeManager microscopeManager;
    
    public MicroscopeCommand(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.microscopeItem = new MicroscopeItem(plugin);
        this.syringeItem = new SyringeItem(plugin);
        this.microscopeManager = plugin.getMicroscopeManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Tylko gracze mogą używać tej komendy!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "use":
                useMicroscope(player);
                break;
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Użycie: /microscope create <basic/advanced/electron>");
                } else {
                    createMicroscope(player, args[1]);
                }
                break;
            case "cancel":
                cancelAnalysis(player);
                break;
            case "help":
                showDetailedHelp(player);
                break;
            default:
                showHelp(player);
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Mikroskop - Pomoc ===");
        player.sendMessage(ChatColor.YELLOW + "/microscope use" + ChatColor.WHITE + " - Użyj mikroskopu do analizy");
        player.sendMessage(ChatColor.YELLOW + "/microscope create <typ>" + ChatColor.WHITE + " - Stwórz mikroskop");
        player.sendMessage(ChatColor.YELLOW + "/microscope cancel" + ChatColor.WHITE + " - Anuluj analizę");
        player.sendMessage(ChatColor.YELLOW + "/microscope help" + ChatColor.WHITE + " - Szczegółowa pomoc");
        player.sendMessage(ChatColor.GRAY + "Dostępne typy: basic, advanced, electron");
    }
    
    private void showDetailedHelp(Player player) {
        player.sendMessage(ChatColor.DARK_BLUE + "═══════════════════════════════════════");
        player.sendMessage(ChatColor.BLUE + "🔬 MIKROSKOP - SZCZEGÓŁOWA POMOC");
        player.sendMessage(ChatColor.DARK_BLUE + "═══════════════════════════════════════");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "Jak używać mikroskopu:");
        player.sendMessage(ChatColor.WHITE + "1. " + ChatColor.GRAY + "Trzymaj mikroskop w głównej ręce");
        player.sendMessage(ChatColor.WHITE + "2. " + ChatColor.GRAY + "Trzymaj probówkę z krwią w drugiej ręce");
        player.sendMessage(ChatColor.WHITE + "3. " + ChatColor.GRAY + "Użyj komendy /microscope use");
        player.sendMessage(ChatColor.WHITE + "4. " + ChatColor.GRAY + "Poczekaj na wyniki analizy");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "Rodzaje mikroskopów:");
        player.sendMessage(ChatColor.GRAY + "• Podstawowy: " + ChatColor.WHITE + "50% dokładności, 10s analiza");
        player.sendMessage(ChatColor.GRAY + "• Zaawansowany: " + ChatColor.WHITE + "80% dokładności, 5s analiza");
        player.sendMessage(ChatColor.GRAY + "• Elektronowy: " + ChatColor.WHITE + "95% dokładności, 2s analiza");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "Wskazówki:");
        player.sendMessage(ChatColor.GRAY + "• Lepsze mikroskopy dają dokładniejsze wyniki");
        player.sendMessage(ChatColor.GRAY + "• Zawsze istnieje szansa na błędny wynik");
        player.sendMessage(ChatColor.GRAY + "• Wyniki są zapisywane w raportach laboratoryjnych");
        player.sendMessage(ChatColor.GRAY + "• Możesz porównywać stare wyniki z nowymi");
        
        player.sendMessage(ChatColor.DARK_BLUE + "═══════════════════════════════════════");
    }
    
    private void useMicroscope(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        
        // Check if holding microscope in main hand
        if (!microscopeItem.isMicroscope(mainHand)) {
            player.sendMessage(ChatColor.RED + "Musisz trzymać mikroskop w głównej ręce!");
            return;
        }
        
        // Check if holding blood sample in off hand
        if (!syringeItem.isBloodSyringe(offHand)) {
            player.sendMessage(ChatColor.RED + "Musisz trzymać probówkę z krwią w drugiej ręce!");
            return;
        }
        
        // Check if already analyzing
        if (microscopeManager.hasActiveAnalysis(player)) {
            player.sendMessage(ChatColor.YELLOW + "Już prowadzisz analizę!");
            return;
        }
        
        // Start analysis
        microscopeManager.analyzeBloodSample(player, mainHand, offHand);
    }
    
    private void createMicroscope(Player player, String type) {
        ItemStack microscope = null;
        String typeName = "";
        
        switch (type.toLowerCase()) {
            case "basic":
                microscope = microscopeItem.createBasicMicroscope();
                typeName = "Podstawowy";
                break;
            case "advanced":
                microscope = microscopeItem.createAdvancedMicroscope();
                typeName = "Zaawansowany";
                break;
            case "electron":
                microscope = microscopeItem.createElectronMicroscope();
                typeName = "Elektronowy";
                break;
            default:
                player.sendMessage(ChatColor.RED + "Nieznany typ mikroskopu!");
                player.sendMessage(ChatColor.GRAY + "Dostępne: basic, advanced, electron");
                return;
        }
        
        if (microscope != null) {
            player.getInventory().addItem(microscope);
            player.sendMessage(ChatColor.GREEN + "✓ Stworzono mikroskop " + typeName + "!");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
        }
    }
    
    private void cancelAnalysis(Player player) {
        microscopeManager.cancelAnalysis(player);
    }
}