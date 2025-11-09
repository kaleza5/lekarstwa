package com.medicalcraft.managers;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.items.SyringeItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.*;

public class VaccineManager {
    
    private final MedicalCraftPlugin plugin;
    private final SyringeItem syringeItem;
    private final Map<String, Vaccine> vaccines;
    private final NamespacedKey vaccineKey;
    
    public VaccineManager(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.syringeItem = new SyringeItem(plugin);
        this.vaccines = new HashMap<>();
        this.vaccineKey = new NamespacedKey(plugin, "vaccine_type");
        initializeVaccines();
    }
    
    private void initializeVaccines() {
        // Create 20 different vaccines for various diseases
        
        // Viral vaccines
        vaccines.put("rabies_vaccine", new Vaccine("rabies_vaccine", "Szczepionka przeciwko Wściekliźnie", 
                "Chroni przed wirusem wścieklizny", 2592000, 0.95, "VIRAL")); // 30 days
        
        vaccines.put("fmd_vaccine", new Vaccine("fmd_vaccine", "Szczepionka przeciwko P&W", 
                "Chroni przed wirusem p&w", 1814400, 0.90, "VIRAL")); // 21 days
        
        vaccines.put("swine_flu_vaccine", new Vaccine("swine_flu_vaccine", "Szczepionka przeciwko Grypie Świń", 
                "Chroni przed wirusem grypy świń", 1209600, 0.85, "VIRAL")); // 14 days
        
        vaccines.put("bird_flu_vaccine", new Vaccine("bird_flu_vaccine", "Szczepionka przeciwko Grypie Ptaków", 
                "Chroni przed wirusem grypy ptaków", 1209600, 0.80, "VIRAL")); // 14 days
        
        vaccines.put("tb_vaccine", new Vaccine("tb_vaccine", "Szczepionka przeciwko Gruźlicy", 
                "Chroni przed bakterią gruźlicy", 5184000, 0.80, "BACTERIAL")); // 60 days
        
        // Bacterial vaccines
        vaccines.put("anthrax_vaccine", new Vaccine("anthrax_vaccine", "Szczepionka przeciwko Wąglikowi", 
                "Chroni przed bakterią wąglika", 3888000, 0.90, "BACTERIAL")); // 45 days
        
        vaccines.put("tetanus_vaccine", new Vaccine("tetanus_vaccine", "Szczepionka przeciwko Tężcowi", 
                "Chroni przed bakterią tężca", 7776000, 0.95, "BACTERIAL")); // 90 days
        
        vaccines.put("brucellosis_vaccine", new Vaccine("brucellosis_vaccine", "Szczepionka przeciwko Brucelozie", 
                "Chroni przed bakterią brucelozy", 2592000, 0.85, "BACTERIAL")); // 30 days
        
        vaccines.put("lepto_vaccine", new Vaccine("lepto_vaccine", "Szczepionka przeciwko Leptospirozie", 
                "Chroni przed bakterią leptospirozy", 15552000, 0.75, "BACTERIAL")); // 180 days
        
        vaccines.put("ecoli_vaccine", new Vaccine("ecoli_vaccine", "Szczepionka przeciwko E. Coli", 
                "Chroni przed bakterią E. Coli", 1209600, 0.70, "BACTERIAL")); // 14 days
        
        vaccines.put("salmonella_vaccine", new Vaccine("salmonella_vaccine", "Szczepionka przeciwko Salmonelli", 
                "Chroni przed bakterią salmonelli", 1209600, 0.70, "BACTERIAL")); // 14 days
        
        // Parasite vaccines/preventives
        vaccines.put("lyme_vaccine", new Vaccine("lyme_vaccine", "Szczepionka przeciwko Chorobie Lyme", 
                "Chroni przed bakterią choroby Lyme", 7776000, 0.70, "PARASITE")); // 90 days
        
        vaccines.put("mange_preventive", new Vaccine("mange_preventive", "Środek zapobiegawczy przeciwko Łajności", 
                "Zapobiega łajności", 2592000, 0.80, "PARASITE")); // 30 days
        
        // Fungal vaccines
        vaccines.put("ringworm_vaccine", new Vaccine("ringworm_vaccine", "Szczepionka przeciwko Łupieżowi", 
                "Chroni przed grzybami łupieżu", 15552000, 0.75, "FUNGAL")); // 180 days
        
        // Combination vaccines
        vaccines.put("combo_viral", new Vaccine("combo_viral", "Wieloskładnikowa Szczepionka Wirusowa", 
                "Chroni przed wieloma wirusami", 2592000, 0.85, "COMBO"));
        
        vaccines.put("combo_bacterial", new Vaccine("combo_bacterial", "Wieloskładnikowa Szczepionka Bakteryjna", 
                "Chroni przed wieloma bakteriami", 2592000, 0.80, "COMBO"));
        
        vaccines.put("universal_vaccine", new Vaccine("universal_vaccine", "Uniwersalna Szczepionka", 
                "Podstawowa ochrona przed wieloma chorobami", 1209600, 0.60, "UNIVERSAL")); // 14 days
        
        // Experimental vaccines
        vaccines.put("experimental_vaccine", new Vaccine("experimental_vaccine", "Eksperymentalna Szczepionka", 
                "Nowa, eksperymentalna szczepionka", 604800, 0.50, "EXPERIMENTAL")); // 7 days
        
        // Boosters
        vaccines.put("booster_shot", new Vaccine("booster_shot", "Dawka Przypominająca", 
                "Wzmacnia istniejącą odporność", 2592000, 0.95, "BOOSTER")); // 30 days
    }
    
    public Vaccine getVaccine(String vaccineType) {
        return vaccines.get(vaccineType);
    }
    
    public Collection<Vaccine> getAllVaccines() {
        return vaccines.values();
    }
    
    public ItemStack createVaccineVial(String vaccineType) {
        Vaccine vaccine = vaccines.get(vaccineType);
        if (vaccine == null) return null;
        
        ItemStack vial = new ItemStack(Material.POTION);
        ItemMeta meta = vial.getItemMeta();
        
        meta.setDisplayName("§aFiolka - " + vaccine.getDisplayName());
        meta.setLore(Arrays.asList(
            "§8• §7" + vaccine.getDescription(),
            "§8• §7Skuteczność: §f" + (int)(vaccine.getEffectiveness() * 100) + "%",
            "§8• §7Czas działania: §f" + formatDuration(vaccine.getDuration()),
            "§8• §7Typ: §f" + getVaccineTypeName(vaccine.getType()),
            "§8• §7Nie podawać chorym zwierzętom",
            "§8• §7Przechowywać w lodówce"
        ));
        
        meta.getPersistentDataContainer().set(vaccineKey, PersistentDataType.STRING, vaccineType);
        vial.setItemMeta(meta);
        
        return vial;
    }
    
    public ItemStack createVaccineSyringe(String vaccineType) {
        return syringeItem.createVaccineSyringe(vaccineType);
    }
    
    public boolean isVaccineVial(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(vaccineKey, PersistentDataType.STRING);
    }
    
    public String getVaccineType(ItemStack item) {
        if (!isVaccineVial(item)) return null;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(vaccineKey, PersistentDataType.STRING);
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
    
    public static class Vaccine {
        private final String type;
        private final String displayName;
        private final String description;
        private final long duration; // in seconds
        private final double effectiveness; // 0.0 to 1.0
        private final String vaccineType; // VIRAL, BACTERIAL, etc.
        
        public Vaccine(String type, String displayName, String description, 
                      long duration, double effectiveness, String vaccineType) {
            this.type = type;
            this.displayName = displayName;
            this.description = description;
            this.duration = duration;
            this.effectiveness = effectiveness;
            this.vaccineType = vaccineType;
        }
        
        public String getType() { return type; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public long getDuration() { return duration; }
        public double getEffectiveness() { return effectiveness; }
        public String getVaccineType() { return vaccineType; }
    }
}