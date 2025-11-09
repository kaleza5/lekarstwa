package com.medicalcraft.commands;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.managers.VaccineManager;
import com.medicalcraft.items.SyringeItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class VaccineCommand implements CommandExecutor, TabCompleter {
    
    private final MedicalCraftPlugin plugin;
    private final VaccineManager vaccineManager;
    private final SyringeItem syringeItem;
    
    public VaccineCommand(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.vaccineManager = new VaccineManager(plugin);
        this.syringeItem = new SyringeItem(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTa komenda może być użyta tylko przez gracza!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "list":
                showVaccineList(player);
                break;
            case "create":
                handleCreateVaccine(player, args);
                break;
            case "give":
                handleGiveVaccine(player, args);
                break;
            case "info":
                handleVaccineInfo(player, args);
                break;
            case "syringe":
                handleCreateSyringe(player, args);
                break;
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§8=== §a§lSystem Szczepionek §8===");
        player.sendMessage("§8• §7/vaccine list - §fPokaż listę dostępnych szczepionek");
        player.sendMessage("§8• §7/vaccine create <typ> - §fStwórz fiolkę z szczepionką");
        player.sendMessage("§8• §7/vaccine give <gracz> <typ> [ilość] - §fDaj szczepionkę graczowi");
        player.sendMessage("§8• §7/vaccine info <typ> - §fInformacje o szczepionce");
        player.sendMessage("§8• §7/vaccine syringe <typ> - §fStwórz strzykawkę ze szczepionką");
        player.sendMessage("§8• §7/med - §fMenu medyczne ze wszystkimi przedmiotami");
    }
    
    private void showVaccineList(Player player) {
        player.sendMessage("§8=== §a§lDostępne Szczepionki §8===");
        
        for (VaccineManager.Vaccine vaccine : vaccineManager.getAllVaccines()) {
            player.sendMessage("§8• §a" + vaccine.getDisplayName() + " §8- §7" + vaccine.getDescription());
            player.sendMessage("  §8• §7Skuteczność: §f" + (int)(vaccine.getEffectiveness() * 100) + "%");
            player.sendMessage("  §8• §7Czas działania: §f" + formatDuration(vaccine.getDuration()));
        }
        
        player.sendMessage("§8• §7Użyj §f/vaccine create <typ> §7aby stworzyć szczepionkę");
    }
    
    private void handleCreateVaccine(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUżycie: /vaccine create <typ>");
            player.sendMessage("§cUżyj /vaccine list aby zobaczyć dostępne typy");
            return;
        }
        
        String vaccineType = args[1].toLowerCase();
        VaccineManager.Vaccine vaccine = vaccineManager.getVaccine(vaccineType);
        
        if (vaccine == null) {
            player.sendMessage("§cNie znaleziono szczepionki typu: §f" + vaccineType);
            player.sendMessage("§cUżyj /vaccine list aby zobaczyć dostępne typy");
            return;
        }
        
        // Check if player has permission
        if (!player.hasPermission("medicalcraft.vaccine.create")) {
            player.sendMessage("§cNie masz uprawnień do tworzenia szczepionek!");
            return;
        }
        
        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cNie masz miejsca w ekwipunku!");
            return;
        }
        
        // Create vaccine vial
        ItemStack vaccineVial = vaccineManager.createVaccineVial(vaccineType);
        if (vaccineVial == null) {
            player.sendMessage("§cBłąd podczas tworzenia szczepionki!");
            return;
        }
        
        player.getInventory().addItem(vaccineVial);
        player.sendMessage("§aStworzono fiolkę z szepcionką: §f" + vaccine.getDisplayName());
        player.sendMessage("§7Skuteczność: §f" + (int)(vaccine.getEffectiveness() * 100) + "%");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    
    private void handleGiveVaccine(Player player, String[] args) {
        if (!player.hasPermission("medicalcraft.vaccine.give")) {
            player.sendMessage("§cNie masz uprawnień do dawania szczepionek!");
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage("§cUżycie: /vaccine give <gracz> <typ> [ilość]");
            return;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§cNie znaleziono gracza: §f" + args[1]);
            return;
        }
        
        String vaccineType = args[2].toLowerCase();
        VaccineManager.Vaccine vaccine = vaccineManager.getVaccine(vaccineType);
        
        if (vaccine == null) {
            player.sendMessage("§cNie znaleziono szczepionki typu: §f" + vaccineType);
            return;
        }
        
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount < 1 || amount > 64) {
                    player.sendMessage("§cIlość musi być między 1 a 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cNieprawidłowa ilość!");
                return;
            }
        }
        
        // Check target inventory space
        int emptySlots = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            if (item == null) emptySlots++;
        }
        
        if (emptySlots < Math.ceil(amount / 64.0)) {
            player.sendMessage("§cGracz §f" + target.getName() + " §cnie ma wystarczająco miejsca w ekwipunku!");
            return;
        }
        
        // Give vaccines
        int given = 0;
        while (given < amount) {
            ItemStack vaccineVial = vaccineManager.createVaccineVial(vaccineType);
            if (vaccineVial == null) break;
            
            int stackSize = Math.min(64, amount - given);
            vaccineVial.setAmount(stackSize);
            target.getInventory().addItem(vaccineVial);
            given += stackSize;
        }
        
        player.sendMessage("§aDano §f" + given + " §aszczepionek §f" + vaccine.getDisplayName() + " §agraczowi §f" + target.getName());
        target.sendMessage("§aOtrzymałeś §f" + given + " §aszczepionek §f" + vaccine.getDisplayName() + " §aod §f" + player.getName());
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
    
    private void handleVaccineInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUżycie: /vaccine info <typ>");
            return;
        }
        
        String vaccineType = args[1].toLowerCase();
        VaccineManager.Vaccine vaccine = vaccineManager.getVaccine(vaccineType);
        
        if (vaccine == null) {
            player.sendMessage("§cNie znaleziono szczepionki typu: §f" + vaccineType);
            return;
        }
        
        player.sendMessage("§8=== §a§lInformacje o Szczepionce §8===");
        player.sendMessage("§8• §7Nazwa: §f" + vaccine.getDisplayName());
        player.sendMessage("§8• §7Opis: §f" + vaccine.getDescription());
        player.sendMessage("§8• §7Skuteczność: §f" + (int)(vaccine.getEffectiveness() * 100) + "%");
        player.sendMessage("§8• §7Czas działania: §f" + formatDuration(vaccine.getDuration()));
        player.sendMessage("§8• §7Typ: §f" + getVaccineTypeName(vaccine.getVaccineType()));
        player.sendMessage("§8• §7Typ wewnętrzny: §f" + vaccine.getType());
    }
    
    private void handleCreateSyringe(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUżycie: /vaccine syringe <typ>");
            return;
        }
        
        String vaccineType = args[1].toLowerCase();
        VaccineManager.Vaccine vaccine = vaccineManager.getVaccine(vaccineType);
        
        if (vaccine == null) {
            player.sendMessage("§cNie znaleziono szczepionki typu: §f" + vaccineType);
            return;
        }
        
        // Check if player has permission
        if (!player.hasPermission("medicalcraft.vaccine.syringe")) {
            player.sendMessage("§cNie masz uprawnień do tworzenia strzykawek ze szczepionkami!");
            return;
        }
        
        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cNie masz miejsca w ekwipunku!");
            return;
        }
        
        // Create vaccine syringe
        ItemStack vaccineSyringe = vaccineManager.createVaccineSyringe(vaccineType);
        if (vaccineSyringe == null) {
            player.sendMessage("§cBłąd podczas tworzenia strzykawki!");
            return;
        }
        
        player.getInventory().addItem(vaccineSyringe);
        player.sendMessage("§aStworzono strzykawkę ze szczepionką: §f" + vaccine.getDisplayName());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    
    private String formatDuration(long seconds) {
        if (seconds >= 7776000) return (seconds / 2592000) + " miesięcy";
        if (seconds >= 2592000) return (seconds / 2592000) + " miesiąc";
        if (seconds >= 604800) return (seconds / 604800) + " tygodni";
        if (seconds >= 86400) return (seconds / 86400) + " dni";
        return (seconds / 3600) + " godzin";
    }
    
    private String getVaccineTypeName(String type) {
        switch (type) {
            case "VIRAL": return "Przeciwko wirusom";
            case "BACTERIAL": return "Przeciwko bakteriom";
            case "PARASITE": return "Przeciwko pasożytom";
            case "FUNGAL": return "Przeciwko grzybom";
            case "COMBO": return "Wieloskładnikowa";
            case "UNIVERSAL": return "Uniwersalna";
            case "EXPERIMENTAL": return "Eksperymentalna";
            case "BOOSTER": return "Dawka przypominająca";
            default: return "Nieznany";
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "create", "give", "info", "syringe");
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("give") || 
                args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("syringe")) {
                List<String> vaccineTypes = new ArrayList<>();
                for (VaccineManager.Vaccine vaccine : vaccineManager.getAllVaccines()) {
                    vaccineTypes.add(vaccine.getType());
                }
                return vaccineTypes;
            }
            
            if (args[0].equalsIgnoreCase("give")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            }
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> vaccineTypes = new ArrayList<>();
            for (VaccineManager.Vaccine vaccine : vaccineManager.getAllVaccines()) {
                vaccineTypes.add(vaccine.getType());
            }
            return vaccineTypes;
        }
        
        return new ArrayList<>();
    }
}