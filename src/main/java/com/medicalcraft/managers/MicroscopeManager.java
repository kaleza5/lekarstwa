package com.medicalcraft.managers;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.items.SyringeItem;
import com.medicalcraft.items.MicroscopeItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MicroscopeManager {
    
    private final MedicalCraftPlugin plugin;
    private final SyringeItem syringeItem;
    private final MicroscopeItem microscopeItem;
    private final Map<UUID, BloodAnalysis> activeAnalyses;
    
    public MicroscopeManager(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.syringeItem = new SyringeItem(plugin);
        this.microscopeItem = new MicroscopeItem(plugin);
        this.activeAnalyses = new HashMap<>();
    }
    
    public void analyzeBloodSample(Player player, ItemStack microscope, ItemStack bloodSample) {
        if (!microscopeItem.isMicroscope(microscope)) {
            player.sendMessage(ChatColor.RED + "To nie jest mikroskop!");
            return;
        }
        
        if (!syringeItem.isBloodSyringe(bloodSample)) {
            player.sendMessage(ChatColor.RED + "To nie jest próbka krwi!");
            return;
        }
        
        String animalType = syringeItem.getSyringeData(bloodSample, "animal_type");
        String suspectedDisease = syringeItem.getSyringeData(bloodSample, "disease_type");
        int accuracy = microscopeItem.getMicroscopeAccuracy(microscope);
        String microscopeName = microscopeItem.getMicroscopeName(microscope);
        
        player.sendMessage(ChatColor.GREEN + "🔬 Rozpoczynanie analizy krwi...");
        player.sendMessage(ChatColor.GRAY + "Mikroskop: " + microscopeName);
        player.sendMessage(ChatColor.GRAY + "Dokładność: " + accuracy + "%");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        
        // Create analysis task
        BloodAnalysis analysis = new BloodAnalysis(player, animalType, suspectedDisease, accuracy, microscopeName);
        activeAnalyses.put(player.getUniqueId(), analysis);
        
        // Start analysis with delay based on microscope type
        int analysisTime = getAnalysisTime(microscope);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                completeAnalysis(player);
            }
        }.runTaskLater(plugin, analysisTime * 20L); // Convert seconds to ticks
    }
    
    private int getAnalysisTime(ItemStack microscope) {
        String type = microscopeItem.getMicroscopeType(microscope);
        switch (type) {
            case MicroscopeItem.MICROSCOPE_BASIC: return 10; // 10 seconds
            case MicroscopeItem.MICROSCOPE_ADVANCED: return 5; // 5 seconds
            case MicroscopeItem.MICROSCOPE_ELECTRON: return 2; // 2 seconds
            default: return 10;
        }
    }
    
    private void completeAnalysis(Player player) {
        BloodAnalysis analysis = activeAnalyses.remove(player.getUniqueId());
        if (analysis == null) return;
        
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
        
        // Generate analysis results
        AnalysisResult result = generateAnalysisResult(analysis);
        
        // Display results
        displayAnalysisResults(player, result);
        
        // Create analysis report item
        ItemStack report = createAnalysisReport(result);
        player.getInventory().addItem(report);
        player.sendMessage(ChatColor.GREEN + "✓ Wygenerowano raport laboratoryjny!");
    }
    
    private AnalysisResult generateAnalysisResult(BloodAnalysis analysis) {
        Random random = new Random();
        List<String> detectedPathogens = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        String conclusion;
        
        // Simulate analysis based on accuracy
        if (random.nextInt(100) < analysis.accuracy) {
            // Successful detection
            if (!analysis.suspectedDisease.equals("Unknown")) {
                detectedPathogens.add(analysis.suspectedDisease);
                
                // Add additional findings based on accuracy
                if (analysis.accuracy >= 80 && random.nextBoolean()) {
                    detectedPathogens.add("Secondary bacterial infection");
                }
                if (analysis.accuracy >= 95 && random.nextBoolean()) {
                    detectedPathogens.add("Viral load: " + (random.nextInt(900) + 100) + " particles/mL");
                }
                
                conclusion = "Pozytywny - Wykryto patogeny";
                recommendations.add("Podaj odpowiednią szczepionkę");
                recommendations.add("Zastosuj leczenie objawowe");
                recommendations.add("Monitoruj stan zwierzęcia");
            } else {
                conclusion = "Negatywny - Nie wykryto patogenów";
                recommendations.add("Próbka czysta - monitoruj zwierzę");
                recommendations.add("W razie objawów powtórz badanie");
            }
        } else {
            // Failed detection (false negative/positive)
            if (random.nextBoolean()) {
                conclusion = "Niepewny - Wymagana powtórna analiza";
                recommendations.add("Użyj lepszego mikroskopu");
                recommendations.add("Pobierz nową próbkę krwi");
            } else {
                conclusion = "Negatywny - Nie wykryto patogenów";
                recommendations.add("Możliwy fałszywy wynik negatywny");
                recommendations.add("Monitoruj stan zwierzęcia");
            }
        }
        
        return new AnalysisResult(analysis, detectedPathogens, recommendations, conclusion);
    }
    
    private void displayAnalysisResults(Player player, AnalysisResult result) {
        player.sendMessage(ChatColor.DARK_GREEN + "═══════════════════════════════════════");
        player.sendMessage(ChatColor.GREEN + "🔬 WYNIKI ANALIZY KRWI");
        player.sendMessage(ChatColor.DARK_GREEN + "═══════════════════════════════════════");
        player.sendMessage(ChatColor.GRAY + "Zwierzę: " + ChatColor.WHITE + result.analysis.animalType);
        player.sendMessage(ChatColor.GRAY + "Mikroskop: " + ChatColor.WHITE + result.analysis.microscopeName);
        player.sendMessage(ChatColor.GRAY + "Dokładność: " + ChatColor.WHITE + result.analysis.accuracy + "%");
        player.sendMessage(ChatColor.GRAY + "Data: " + ChatColor.WHITE + new Date().toString());
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "Wykryte patogeny:");
        if (result.detectedPathogens.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "  • Brak patogenów");
        } else {
            for (String pathogen : result.detectedPathogens) {
                player.sendMessage(ChatColor.RED + "  • " + pathogen);
            }
        }
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "Wniosek: " + ChatColor.WHITE + result.conclusion);
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "Zalecenia:");
        for (String recommendation : result.recommendations) {
            player.sendMessage(ChatColor.GREEN + "  • " + recommendation);
        }
        
        player.sendMessage(ChatColor.DARK_GREEN + "═══════════════════════════════════════");
    }
    
    private ItemStack createAnalysisReport(AnalysisResult result) {
        ItemStack report = new ItemStack(Material.PAPER);
        ItemMeta meta = report.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + "Raport Laboratoryjny - " + result.analysis.animalType);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Wynik: " + ChatColor.WHITE + result.conclusion);
        lore.add(ChatColor.GRAY + "Dokładność: " + ChatColor.WHITE + result.analysis.accuracy + "%");
        lore.add(ChatColor.GRAY + "Mikroskop: " + ChatColor.WHITE + result.analysis.microscopeName);
        lore.add("");
        lore.add(ChatColor.YELLOW + "Wykryte patogeny:");
        if (result.detectedPathogens.isEmpty()) {
            lore.add(ChatColor.GRAY + "  • Brak patogenów");
        } else {
            for (String pathogen : result.detectedPathogens) {
                lore.add(ChatColor.RED + "  • " + pathogen);
            }
        }
        lore.add("");
        lore.add(ChatColor.GREEN + "Przeciągnij na mikroskop aby porównać");
        
        meta.setLore(lore);
        report.setItemMeta(meta);
        
        return report;
    }
    
    public boolean hasActiveAnalysis(Player player) {
        return activeAnalyses.containsKey(player.getUniqueId());
    }
    
    public void cancelAnalysis(Player player) {
        activeAnalyses.remove(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "Analiza przerwana.");
    }
    
    private static class BloodAnalysis {
        final Player player;
        final String animalType;
        final String suspectedDisease;
        final int accuracy;
        final String microscopeName;
        
        BloodAnalysis(Player player, String animalType, String suspectedDisease, int accuracy, String microscopeName) {
            this.player = player;
            this.animalType = animalType;
            this.suspectedDisease = suspectedDisease;
            this.accuracy = accuracy;
            this.microscopeName = microscopeName;
        }
    }
    
    private static class AnalysisResult {
        final BloodAnalysis analysis;
        final List<String> detectedPathogens;
        final List<String> recommendations;
        final String conclusion;
        
        AnalysisResult(BloodAnalysis analysis, List<String> detectedPathogens, List<String> recommendations, String conclusion) {
            this.analysis = analysis;
            this.detectedPathogens = detectedPathogens;
            this.recommendations = recommendations;
            this.conclusion = conclusion;
        }
    }
}