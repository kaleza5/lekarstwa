package com.medicalcraft.commands;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.items.SyringeItem;
import com.medicalcraft.managers.DiseaseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class SyringeCommand implements CommandExecutor {
    
    private final MedicalCraftPlugin plugin;
    private final SyringeItem syringeItem;
    private final DiseaseManager diseaseManager;
    
    public SyringeCommand(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.syringeItem = new SyringeItem(plugin);
        this.diseaseManager = plugin.getDiseaseManager();
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
            case "collect":
                collectBlood(player);
                break;
            case "inject":
                if (args.length < 2) {
                    injectSyringe(player);
                } else {
                    injectSpecific(player, args[1]);
                }
                break;
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Użycie: /syringe create <empty/blood/vaccine/medicine> [typ]");
                } else {
                    createSyringe(player, args[1], args.length > 2 ? args[2] : null);
                }
                break;
            default:
                showHelp(player);
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Strzykawki - Pomoc ===");
        player.sendMessage(ChatColor.YELLOW + "/syringe collect" + ChatColor.WHITE + " - Pobierz krew z zwierzęcia");
        player.sendMessage(ChatColor.YELLOW + "/syringe inject" + ChatColor.WHITE + " - Wstrzyknij zawartość strzykawki");
        player.sendMessage(ChatColor.YELLOW + "/syringe create <typ> [rodzaj]" + ChatColor.WHITE + " - Stwórz strzykawkę");
        player.sendMessage(ChatColor.GRAY + "Dostępne typy: empty, blood, vaccine, medicine");
    }
    
    private void collectBlood(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        if (!syringeItem.isEmptySyringe(itemInHand)) {
            player.sendMessage(ChatColor.RED + "Musisz trzymać pustą strzykawkę!");
            return;
        }
        
        // Ray trace to find animal
        RayTraceResult result = player.rayTraceEntities(5);
        if (result == null || result.getHitEntity() == null) {
            player.sendMessage(ChatColor.RED + "Nie znaleziono zwierzęcia!");
            return;
        }
        
        Entity target = result.getHitEntity();
        if (!(target instanceof Animals)) {
            player.sendMessage(ChatColor.RED + "To nie jest zwierzę!");
            return;
        }
        
        Animals animal = (Animals) target;
        String animalType = animal.getType().name();
        String diseaseType = "Unknown";
        
        // Check if animal is infected
        if (diseaseManager.isInfected(animal)) {
            diseaseType = diseaseManager.getInfectedAnimal(animal).getDisease().getDisplayName();
        }
        
        // Create blood syringe
        ItemStack bloodSyringe = syringeItem.createBloodSyringe(animalType, diseaseType);
        
        // Remove empty syringe and add blood syringe
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        
        player.getInventory().addItem(bloodSyringe);
        player.sendMessage(ChatColor.GREEN + "✓ Pobrano krew z " + animalType + "!");
        
        if (!diseaseType.equals("Unknown")) {
            player.sendMessage(ChatColor.YELLOW + "⚠ Wykryto możliwą chorobę: " + diseaseType);
        }
    }
    
    private void injectSyringe(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        if (!syringeItem.isSyringe(itemInHand)) {
            player.sendMessage(ChatColor.RED + "Musisz trzymać strzykawkę!");
            return;
        }
        
        String syringeType = syringeItem.getSyringeType(itemInHand);
        
        // Ray trace to find animal
        RayTraceResult result = player.rayTraceEntities(5);
        if (result == null || result.getHitEntity() == null) {
            player.sendMessage(ChatColor.RED + "Nie znaleziono zwierzęcia!");
            return;
        }
        
        Entity target = result.getHitEntity();
        if (!(target instanceof Animals)) {
            player.sendMessage(ChatColor.RED + "To nie jest zwierzę!");
            return;
        }
        
        Animals animal = (Animals) target;
        
        switch (syringeType) {
            case SyringeItem.SYRINGE_VACCINE:
                injectVaccine(player, animal, itemInHand);
                break;
            case SyringeItem.SYRINGE_MEDICINE:
                injectMedicine(player, animal, itemInHand);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Ta strzykawka nie nadaje się do wstrzyknięć!");
        }
    }
    
    private void injectVaccine(Player player, Animals animal, ItemStack syringe) {
        String vaccineType = syringeItem.getSyringeData(syringe, "vaccine_type");
        
        if (diseaseManager.isInfected(animal)) {
            player.sendMessage(ChatColor.RED + "To zwierzę jest już chore! Użyj leku zamiast szczepionki.");
            return;
        }
        
        // Apply vaccine effect (simplified - would add immunity)
        player.sendMessage(ChatColor.GREEN + "✓ Zaszczepiono " + animal.getType().name() + " przeciwko " + vaccineType + "!");
        animal.setCustomName(ChatColor.GREEN + "✓ " + animal.getType().name());
        
        // Remove syringe
        if (syringe.getAmount() > 1) {
            syringe.setAmount(syringe.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
    
    private void injectMedicine(Player player, Animals animal, ItemStack syringe) {
        String medicineType = syringeItem.getSyringeData(syringe, "medicine_type");
        
        if (!diseaseManager.isInfected(animal)) {
            player.sendMessage(ChatColor.YELLOW + "To zwierzę nie jest chore!");
            return;
        }
        
        // Try to cure the animal
        var infectedAnimal = diseaseManager.getInfectedAnimal(animal);
        double successChance = infectedAnimal.getTreatmentSuccessChance(medicineType);
        
        if (Math.random() < successChance) {
            diseaseManager.cureAnimal(animal);
            player.sendMessage(ChatColor.GREEN + "✓ Uleczono " + animal.getType().name() + " za pomocą " + medicineType + "!");
            animal.setCustomName(null);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Leczenie nie zadziałało. Spróbuj innego leku lub poczekaj.");
        }
        
        // Remove syringe
        if (syringe.getAmount() > 1) {
            syringe.setAmount(syringe.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
    
    private void injectSpecific(Player player, String type) {
        // Implementation for specific injection types
        player.sendMessage(ChatColor.YELLOW + "Funkcja w przygotowaniu...");
    }
    
    private void createSyringe(Player player, String type, String subType) {
        ItemStack syringe = null;
        
        switch (type.toLowerCase()) {
            case "empty":
                syringe = syringeItem.createEmptySyringe();
                break;
            case "vaccine":
                if (subType != null) {
                    syringe = syringeItem.createVaccineSyringe(subType);
                } else {
                    player.sendMessage(ChatColor.RED + "Podaj typ szczepionki!");
                    return;
                }
                break;
            case "medicine":
                if (subType != null) {
                    syringe = syringeItem.createMedicineSyringe(subType);
                } else {
                    player.sendMessage(ChatColor.RED + "Podaj typ leku!");
                    return;
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "Nieznany typ strzykawki!");
                return;
        }
        
        if (syringe != null) {
            player.getInventory().addItem(syringe);
            player.sendMessage(ChatColor.GREEN + "✓ Stworzono strzykawkę!");
        }
    }
}