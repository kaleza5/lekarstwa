package com.medicalcraft.diseases;

import com.medicalcraft.diseases.Disease;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InfectedAnimal {
    
    private final Entity entity;
    private final Disease disease;
    private final long infectionTime;
    private long lastSymptomTime;
    private boolean showingSymptoms;
    private int stage; // 0: incubation, 1: early, 2: advanced, 3: critical
    
    public InfectedAnimal(Entity entity, Disease disease) {
        this.entity = entity;
        this.disease = disease;
        this.infectionTime = System.currentTimeMillis();
        this.lastSymptomTime = infectionTime;
        this.showingSymptoms = false;
        this.stage = 0;
    }
    
    public boolean update() {
        long currentTime = System.currentTimeMillis();
        long timeSinceInfection = (currentTime - infectionTime) / 1000; // in seconds
        
        // Check if symptoms should appear
        if (!showingSymptoms && timeSinceInfection >= disease.getIncubationTime()) {
            showingSymptoms = true;
            showSymptom();
            stage = 1;
        }
        
        // Show symptoms periodically
        if (showingSymptoms && (currentTime - lastSymptomTime) / 1000 >= 60) { // Every minute
            showSymptom();
            lastSymptomTime = currentTime;
            
            // Progress disease stage
            if (timeSinceInfection > disease.getIncubationTime() + 300) { // 5 minutes after symptoms
                stage = 2;
            }
            if (timeSinceInfection > disease.getIncubationTime() + 600) { // 10 minutes after symptoms
                stage = 3;
            }
        }
        
        // Apply disease effects
        if (showingSymptoms) {
            applyDiseaseEffects();
        }
        
        // Check for death
        if (disease.isFatal() && showingSymptoms) {
            int deathTime = disease.getDeathTime();
            if (deathTime > 0 && timeSinceInfection >= deathTime) {
                if (Math.random() < 0.01) { // 1% chance per second after death time
                    killAnimal();
                    return true; // Animal died
                }
            }
        }
        
        return false; // Animal still alive
    }
    
    private void showSymptom() {
        if (entity == null || entity.isDead()) return;
        
        String symptom = disease.getRandomSymptom();
        String message = ChatColor.RED + "⚠️ " + ChatColor.YELLOW + entity.getType().name() + 
                        " shows symptom: " + ChatColor.WHITE + symptom;
        
        // Show particles around animal
        entity.getWorld().spawnParticle(org.bukkit.Particle.SPELL_MOB, 
                entity.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        
        // Send message to nearby players
        entity.getNearbyEntities(20, 20, 20).stream()
                .filter(e -> e instanceof org.bukkit.entity.Player)
                .forEach(p -> p.sendMessage(message));
    }
    
    private void applyDiseaseEffects() {
        if (entity == null || entity.isDead()) return;
        
        // Apply effects based on disease and stage
        switch (stage) {
            case 1: // Early stage
                entity.setVelocity(entity.getVelocity().multiply(0.8)); // Slower movement
                break;
            case 2: // Advanced stage
                entity.setVelocity(entity.getVelocity().multiply(0.6));
                if (Math.random() < 0.1) { // 10% chance to stop moving
                    entity.setVelocity(entity.getVelocity().multiply(0));
                }
                break;
            case 3: // Critical stage
                entity.setVelocity(entity.getVelocity().multiply(0.3));
                if (Math.random() < 0.3) { // 30% chance to stop moving
                    entity.setVelocity(entity.getVelocity().multiply(0));
                }
                break;
        }
        
        // Special effects for specific diseases
        if (disease.getName().equals("rabies") && stage >= 2) {
            if (Math.random() < 0.05) { // 5% chance to attack nearby entities
                entity.getNearbyEntities(3, 3, 3).stream()
                        .filter(e -> e instanceof org.bukkit.entity.Animals || e instanceof org.bukkit.entity.Player)
                        .findFirst()
                        .ifPresent(target -> {
                            // Make animal aggressive (simplified)
                            entity.setVelocity(target.getLocation().subtract(entity.getLocation()).toVector().normalize().multiply(0.5));
                        });
            }
        }
    }
    
    private void killAnimal() {
        if (entity == null || entity.isDead()) return;
        
        entity.getWorld().strikeLightningEffect(entity.getLocation());
        entity.remove();
        
        String deathMessage = ChatColor.DARK_RED + "💀 " + ChatColor.RED + "A " + 
                             entity.getType().name() + " has died from " + disease.getDisplayName() + "!";
        
        entity.getNearbyEntities(30, 30, 30).stream()
                .filter(e -> e instanceof org.bukkit.entity.Player)
                .forEach(p -> p.sendMessage(deathMessage));
    }
    
    public Entity getEntity() { return entity; }
    public Disease getDisease() { return disease; }
    public boolean isShowingSymptoms() { return showingSymptoms; }
    public int getStage() { return stage; }
    public long getInfectionTime() { return infectionTime; }
    
    public double getTreatmentSuccessChance(String treatment) {
        double baseEffectiveness = disease.getTreatmentEffectiveness(treatment);
        
        // Reduce effectiveness based on disease stage
        switch (stage) {
            case 0: return baseEffectiveness * 1.2; // Easier to cure in incubation
            case 1: return baseEffectiveness;
            case 2: return baseEffectiveness * 0.7;
            case 3: return baseEffectiveness * 0.4;
            default: return baseEffectiveness;
        }
    }
}