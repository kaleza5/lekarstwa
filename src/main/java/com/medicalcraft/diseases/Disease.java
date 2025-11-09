package com.medicalcraft.diseases;

import org.bukkit.entity.EntityType;
import java.util.*;

public class Disease {
    
    private final String name;
    private final String displayName;
    private final String description;
    private final List<EntityType> affectedAnimals;
    private final List<String> symptoms;
    private final double infectionChance;
    private final int incubationTime; // in seconds
    private final int duration; // in seconds, -1 for permanent until cured
    private final boolean fatal;
    private final int deathTime; // time until death if untreated
    private final String virusType;
    private final Map<String, Double> treatmentEffectiveness;
    
    public Disease(String name, String displayName, String description, 
                   List<EntityType> affectedAnimals, List<String> symptoms,
                   double infectionChance, int incubationTime, int duration, 
                   boolean fatal, int deathTime, String virusType) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.affectedAnimals = affectedAnimals;
        this.symptoms = symptoms;
        this.infectionChance = infectionChance;
        this.incubationTime = incubationTime;
        this.duration = duration;
        this.fatal = fatal;
        this.deathTime = deathTime;
        this.virusType = virusType;
        this.treatmentEffectiveness = new HashMap<>();
    }
    
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public List<EntityType> getAffectedAnimals() { return affectedAnimals; }
    public List<String> getSymptoms() { return symptoms; }
    public double getInfectionChance() { return infectionChance; }
    public int getIncubationTime() { return incubationTime; }
    public int getDuration() { return duration; }
    public boolean isFatal() { return fatal; }
    public int getDeathTime() { return deathTime; }
    public String getVirusType() { return virusType; }
    
    public void addTreatmentEffectiveness(String treatment, double effectiveness) {
        treatmentEffectiveness.put(treatment, effectiveness);
    }
    
    public double getTreatmentEffectiveness(String treatment) {
        return treatmentEffectiveness.getOrDefault(treatment, 0.0);
    }
    
    public boolean canAffect(EntityType entityType) {
        return affectedAnimals.contains(entityType);
    }
    
    public String getRandomSymptom() {
        return symptoms.get(new Random().nextInt(symptoms.size()));
    }
}