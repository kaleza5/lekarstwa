package com.medicalcraft.listeners;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.managers.DiseaseManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimalDiseaseListener implements Listener {

    private final MedicalCraftPlugin plugin;
    private final DiseaseManager diseaseManager;
    private final Set<Material> meatMaterials;

    public AnimalDiseaseListener(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.diseaseManager = plugin.getDiseaseManager();
        this.meatMaterials = new HashSet<>(Arrays.asList(
                Material.BEEF,
                Material.PORKCHOP,
                Material.MUTTON,
                Material.CHICKEN,
                Material.RABBIT,
                Material.COOKED_BEEF,
                Material.COOKED_PORKCHOP,
                Material.COOKED_MUTTON,
                Material.COOKED_CHICKEN,
                Material.COOKED_RABBIT
        ));
    }

    @EventHandler
    public void onAnimalDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;

        // Only consider typical farm and wild animals that drop meat
        EntityType type = entity.getType();
        if (!isMeatDroppingAnimal(type)) return;

        // If animal is infected -> reduce meat drops
        if (diseaseManager != null && diseaseManager.isInfected(entity)) {
            FileConfiguration cfg = plugin.getConfig();
            double multiplier = cfg.getDouble("settings.infected_meat_multiplier", 0.5);
            int minDrop = cfg.getInt("settings.infected_meat_minimum", 1);

            List<ItemStack> drops = event.getDrops();
            for (ItemStack drop : drops) {
                if (drop != null && meatMaterials.contains(drop.getType())) {
                    int original = drop.getAmount();
                    int adjusted = (int) Math.floor(original * multiplier);
                    if (original > 0) {
                        adjusted = Math.max(adjusted, minDrop);
                    }
                    drop.setAmount(adjusted);
                }
            }
        }
        // If not infected: do nothing -> normal drops
    }

    private boolean isMeatDroppingAnimal(EntityType type) {
        switch (type) {
            case COW:
            case MUSHROOM_COW:
            case PIG:
            case SHEEP:
            case CHICKEN:
            case RABBIT:
                return true;
            default:
                return false;
        }
    }
}