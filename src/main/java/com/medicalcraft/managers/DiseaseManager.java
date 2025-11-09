package com.medicalcraft.managers;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.diseases.Disease;
import com.medicalcraft.diseases.InfectedAnimal;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DiseaseManager {
    
    private final MedicalCraftPlugin plugin;
    private final Map<String, Disease> diseases;
    private final Map<UUID, InfectedAnimal> infectedAnimals;
    
    public DiseaseManager(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.diseases = new HashMap<>();
        this.infectedAnimals = new HashMap<>();
    }
    
    public void initializeDiseases() {
        // Create 15 different animal diseases
        
        // 1. Rabies
        Disease rabies = new Disease("rabies", "Wścieklizna", "Deadly viral disease affecting nervous system",
                Arrays.asList(EntityType.WOLF, EntityType.FOX, EntityType.CAT, EntityType.COW, EntityType.PIG),
                Arrays.asList("Aggressive behavior", "Foaming at mouth", "Paralysis", "Seizures"),
                0.05, 300, -1, true, 1200, "RNA_VIRUS");
        rabies.addTreatmentEffectiveness("rabies_vaccine", 0.95);
        rabies.addTreatmentEffectiveness("antiviral_medication", 0.3);
        diseases.put("rabies", rabies);
        
        // 2. Foot and Mouth Disease
        Disease fmd = new Disease("foot_mouth", "Pasożyt warg i nóg", "Highly contagious viral disease",
                Arrays.asList(EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.GOAT),
                Arrays.asList("Blisters on mouth and feet", "Fever", "Loss of appetite", "Lameness"),
                0.15, 180, 2400, false, 0, "DNA_VIRUS");
        fmd.addTreatmentEffectiveness("fmd_vaccine", 0.9);
        fmd.addTreatmentEffectiveness("antiviral_ointment", 0.7);
        diseases.put("foot_mouth", fmd);
        
        // 3. Mad Cow Disease
        Disease madCow = new Disease("mad_cow", "Choroba wściekłych krów", "Fatal neurodegenerative disease",
                Arrays.asList(EntityType.COW, EntityType.MUSHROOM_COW),
                Arrays.asList("Aggression", "Coordination problems", "Weight loss", "Abnormal posture"),
                0.02, 600, -1, true, 1800, "PRION");
        madCow.addTreatmentEffectiveness("prion_inhibitor", 0.1);
        diseases.put("mad_cow", madCow);
        
        // 4. Swine Flu
        Disease swineFlu = new Disease("swine_flu", "Grypa świń", "Respiratory viral infection",
                Arrays.asList(EntityType.PIG, EntityType.HOGLIN),
                Arrays.asList("Coughing", "Fever", "Nasal discharge", "Lethargy"),
                0.12, 120, 1800, false, 0, "RNA_VIRUS");
        swineFlu.addTreatmentEffectiveness("swine_flu_vaccine", 0.85);
        swineFlu.addTreatmentEffectiveness("antiviral_medication", 0.6);
        diseases.put("swine_flu", swineFlu);
        
        // 5. Bird Flu
        Disease birdFlu = new Disease("bird_flu", "Grypa ptaków", "Avian influenza virus",
                Arrays.asList(EntityType.CHICKEN, EntityType.PARROT, EntityType.BAT),
                Arrays.asList("Respiratory distress", "Swelling", "Diarrhea", "Sudden death"),
                0.08, 90, 1200, true, 800, "RNA_VIRUS");
        birdFlu.addTreatmentEffectiveness("bird_flu_vaccine", 0.8);
        birdFlu.addTreatmentEffectiveness("antiviral_medication", 0.5);
        diseases.put("bird_flu", birdFlu);
        
        // 6. Lyme Disease
        Disease lyme = new Disease("lyme", "Choroba Lyme", "Bacterial infection from ticks",
                Arrays.asList(EntityType.WOLF, EntityType.FOX, EntityType.CAT, EntityType.HORSE),
                Arrays.asList("Fever", "Lameness", "Swollen joints", "Fatigue"),
                0.06, 240, 3600, false, 0, "BACTERIA");
        lyme.addTreatmentEffectiveness("antibiotics", 0.95);
        lyme.addTreatmentEffectiveness("lyme_vaccine", 0.7);
        diseases.put("lyme", lyme);
        
        // 7. Anthrax
        Disease anthrax = new Disease("anthrax", "Wąglik", "Serious bacterial infection",
                Arrays.asList(EntityType.COW, EntityType.SHEEP, EntityType.GOAT, EntityType.HORSE),
                Arrays.asList("Sudden death", "Fever", "Difficulty breathing", "Bloody discharge"),
                0.03, 60, 900, true, 600, "BACTERIA");
        anthrax.addTreatmentEffectiveness("anthrax_vaccine", 0.9);
        anthrax.addTreatmentEffectiveness("strong_antibiotics", 0.6);
        diseases.put("anthrax", anthrax);
        
        // 8. Ringworm
        Disease ringworm = new Disease("ringworm", "Łupież pstry", "Fungal skin infection",
                Arrays.asList(EntityType.CAT, EntityType.WOLF, EntityType.HORSE, EntityType.COW),
                Arrays.asList("Circular lesions", "Hair loss", "Itching", "Scaly skin"),
                0.2, 120, 2800, false, 0, "FUNGUS");
        ringworm.addTreatmentEffectiveness("antifungal_cream", 0.9);
        ringworm.addTreatmentEffectiveness("antifungal_shampoo", 0.8);
        diseases.put("ringworm", ringworm);
        
        // 9. Mange
        Disease mange = new Disease("mange", "Łajność", "Parasitic skin disease",
                Arrays.asList(EntityType.WOLF, EntityType.FOX, EntityType.PIG, EntityType.CAT),
                Arrays.asList("Severe itching", "Hair loss", "Thick crusty skin", "Weight loss"),
                0.1, 180, 3200, false, 0, "PARASITE");
        mange.addTreatmentEffectiveness("ivermectin", 0.95);
        mange.addTreatmentEffectiveness("medicated_shampoo", 0.7);
        diseases.put("mange", mange);
        
        // 10. Brucellosis
        Disease brucellosis = new Disease("brucellosis", "Bruceloza", "Bacterial reproductive disease",
                Arrays.asList(EntityType.COW, EntityType.PIG, EntityType.GOAT, EntityType.SHEEP),
                Arrays.asList("Abortion", "Infertility", "Swollen joints", "Lethargy"),
                0.04, 300, 4000, false, 0, "BACTERIA");
        brucellosis.addTreatmentEffectiveness("brucellosis_vaccine", 0.85);
        brucellosis.addTreatmentEffectiveness("antibiotics", 0.6);
        diseases.put("brucellosis", brucellosis);
        
        // 11. Tetanus
        Disease tetanus = new Disease("tetanus", "Tężec", "Bacterial nervous system disease",
                Arrays.asList(EntityType.HORSE, EntityType.COW, EntityType.SHEEP, EntityType.GOAT),
                Arrays.asList("Muscle stiffness", "Lockjaw", "Seizures", "Difficulty swallowing"),
                0.02, 180, 1500, true, 1000, "BACTERIA");
        tetanus.addTreatmentEffectiveness("tetanus_vaccine", 0.95);
        tetanus.addTreatmentEffectiveness("tetanus_antitoxin", 0.8);
        diseases.put("tetanus", tetanus);
        
        // 12. E. Coli
        Disease ecoli = new Disease("ecoli", "E. Coli", "Bacterial intestinal infection",
                Arrays.asList(EntityType.COW, EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP),
                Arrays.asList("Diarrhea", "Dehydration", "Fever", "Loss of appetite"),
                0.08, 90, 1600, false, 0, "BACTERIA");
        ecoli.addTreatmentEffectiveness("antibiotics", 0.9);
        ecoli.addTreatmentEffectiveness("probiotics", 0.6);
        diseases.put("ecoli", ecoli);
        
        // 13. Salmonella
        Disease salmonella = new Disease("salmonella", "Salmonella", "Bacterial food poisoning",
                Arrays.asList(EntityType.CHICKEN, EntityType.PIG, EntityType.COW),
                Arrays.asList("Severe diarrhea", "Fever", "Dehydration", "Weakness"),
                0.06, 120, 2000, false, 0, "BACTERIA");
        salmonella.addTreatmentEffectiveness("antibiotics", 0.85);
        salmonella.addTreatmentEffectiveness("electrolytes", 0.7);
        diseases.put("salmonella", salmonella);
        
        // 14. Tuberculosis
        Disease tuberculosis = new Disease("tuberculosis", "Grlica", "Bacterial respiratory disease",
                Arrays.asList(EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.GOAT),
                Arrays.asList("Persistent cough", "Weight loss", "Fever", "Difficulty breathing"),
                0.03, 600, -1, true, 2400, "BACTERIA");
        tuberculosis.addTreatmentEffectiveness("tb_vaccine", 0.8);
        tuberculosis.addTreatmentEffectiveness("antibiotics", 0.4);
        diseases.put("tuberculosis", tuberculosis);
        
        // 15. Leptospirosis
        Disease leptospirosis = new Disease("leptospirosis", "Leptospiroza", "Bacterial kidney disease",
                Arrays.asList(EntityType.DOG, EntityType.CAT, EntityType.COW, EntityType.PIG),
                Arrays.asList("Fever", "Muscle pain", "Kidney failure", "Jaundice"),
                0.05, 180, 2800, false, 0, "BACTERIA");
        leptospirosis.addTreatmentEffectiveness("antibiotics", 0.9);
        leptospirosis.addTreatmentEffectiveness("lepto_vaccine", 0.75);
        diseases.put("leptospirosis", leptospirosis);
        
        startDiseaseTimer();
    }
    
    private void startDiseaseTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Update infected animals
                Iterator<Map.Entry<UUID, InfectedAnimal>> iterator = infectedAnimals.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<UUID, InfectedAnimal> entry = iterator.next();
                    InfectedAnimal infected = entry.getValue();
                    
                    if (infected.update()) {
                        // Animal died from disease
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }
    
    public Disease getDisease(String name) {
        return diseases.get(name);
    }
    
    public Collection<Disease> getAllDiseases() {
        return diseases.values();
    }
    
    public int getDiseaseCount() {
        return diseases.size();
    }
    
    public void infectAnimal(Entity entity, Disease disease) {
        if (infectedAnimals.containsKey(entity.getUniqueId())) {
            return; // Already infected
        }
        
        InfectedAnimal infected = new InfectedAnimal(entity, disease);
        infectedAnimals.put(entity.getUniqueId(), infected);
        
        plugin.getServer().broadcastMessage(ChatColor.RED + "⚠️ " + ChatColor.YELLOW + 
            entity.getType().name() + " has been infected with " + disease.getDisplayName() + "!");
    }
    
    public boolean isInfected(Entity entity) {
        return infectedAnimals.containsKey(entity.getUniqueId());
    }
    
    public InfectedAnimal getInfectedAnimal(Entity entity) {
        return infectedAnimals.get(entity.getUniqueId());
    }
    
    public void cureAnimal(Entity entity) {
        infectedAnimals.remove(entity.getUniqueId());
    }
    
    public void saveData() {
        // Save infected animals data
        // Implementation would save to file/database
    }
    
    public void tryRandomInfection(Entity entity) {
        if (isInfected(entity) || !(entity instanceof org.bukkit.entity.Animals)) {
            return;
        }
        
        // 1% chance per minute for random infection
        if (Math.random() < 0.00016) { // 1% per minute = 0.01/60 per second
            List<Disease> possibleDiseases = new ArrayList<>();
            for (Disease disease : diseases.values()) {
                if (disease.canAffect(entity.getType())) {
                    possibleDiseases.add(disease);
                }
            }
            
            if (!possibleDiseases.isEmpty()) {
                Disease randomDisease = possibleDiseases.get(new Random().nextInt(possibleDiseases.size()));
                if (Math.random() < randomDisease.getInfectionChance()) {
                    infectAnimal(entity, randomDisease);
                }
            }
        }
    }
}