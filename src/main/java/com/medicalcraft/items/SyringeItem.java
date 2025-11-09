package com.medicalcraft.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import com.medicalcraft.MedicalCraftPlugin;

import java.util.Arrays;
import java.util.List;

public class SyringeItem {
    
    public static final String SYRINGE_EMPTY = "syringe_empty";
    public static final String SYRINGE_BLOOD = "syringe_blood";
    public static final String SYRINGE_VACCINE = "syringe_vaccine";
    public static final String SYRINGE_MEDICINE = "syringe_medicine";
    
    private final MedicalCraftPlugin plugin;
    private final NamespacedKey syringeKey;
    
    public SyringeItem(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.syringeKey = new NamespacedKey(plugin, "syringe_type");
    }
    
    public ItemStack createEmptySyringe() {
        ItemStack syringe = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta meta = syringe.getItemMeta();
        
        meta.setDisplayName("§7Pusta Strzykawka");
        meta.setLore(Arrays.asList(
            "§8• §7Użyj na zwierzęciu aby pobrać krew",
            "§8• §7Niezbędna do badań laboratoryjnych",
            "§8• §7Można wypełnić szczepionką lub lekiem"
        ));
        
        meta.getPersistentDataContainer().set(syringeKey, PersistentDataType.STRING, SYRINGE_EMPTY);
        syringe.setItemMeta(meta);
        
        return syringe;
    }
    
    public ItemStack createBloodSyringe(String animalType, String diseaseType) {
        ItemStack syringe = new ItemStack(Material.POTION);
        ItemMeta meta = syringe.getItemMeta();
        
        meta.setDisplayName("§cKrew - " + animalType);
        meta.setLore(Arrays.asList(
            "§8• §7Zwierzę: §f" + animalType,
            "§8• §7Możliwa choroba: §f" + diseaseType,
            "§8• §7Użyj z mikroskopem aby przebadać",
            "§8• §7Niebezpieczny materiał biologiczny"
        ));
        
        meta.getPersistentDataContainer().set(syringeKey, PersistentDataType.STRING, SYRINGE_BLOOD);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "animal_type"), PersistentDataType.STRING, animalType);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "disease_type"), PersistentDataType.STRING, diseaseType);
        
        syringe.setItemMeta(meta);
        return syringe;
    }
    
    public ItemStack createVaccineSyringe(String vaccineType) {
        ItemStack syringe = new ItemStack(Material.POTION);
        ItemMeta meta = syringe.getItemMeta();
        
        meta.setDisplayName("§aSzczepionka - " + vaccineType);
        meta.setLore(Arrays.asList(
            "§8• §7Typ: §f" + vaccineType,
            "§8• §7Chroni przed chorobami",
            "§8• §7Użyj na zdrowym zwierzęciu",
            "§8• §7Działa przez 30 dni"
        ));
        
        meta.getPersistentDataContainer().set(syringeKey, PersistentDataType.STRING, SYRINGE_VACCINE);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "vaccine_type"), PersistentDataType.STRING, vaccineType);
        
        syringe.setItemMeta(meta);
        return syringe;
    }
    
    public ItemStack createMedicineSyringe(String medicineType) {
        ItemStack syringe = new ItemStack(Material.POTION);
        ItemMeta meta = syringe.getItemMeta();
        
        meta.setDisplayName("§bLek - " + medicineType);
        meta.setLore(Arrays.asList(
            "§8• §7Lek: §f" + medicineType,
            "§8• §7Leczy choroby bakteryjne",
            "§8• §7Użyj na chorym zwierzęciu",
            "§8• §7Szybkie działanie"
        ));
        
        meta.getPersistentDataContainer().set(syringeKey, PersistentDataType.STRING, SYRINGE_MEDICINE);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "medicine_type"), PersistentDataType.STRING, medicineType);
        
        syringe.setItemMeta(meta);
        return syringe;
    }
    
    public String getSyringeType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(syringeKey, PersistentDataType.STRING);
    }
    
    public boolean isSyringe(ItemStack item) {
        return getSyringeType(item) != null;
    }
    
    public boolean isEmptySyringe(ItemStack item) {
        return SYRINGE_EMPTY.equals(getSyringeType(item));
    }
    
    public boolean isBloodSyringe(ItemStack item) {
        return SYRINGE_BLOOD.equals(getSyringeType(item));
    }
    
    public boolean isVaccineSyringe(ItemStack item) {
        return SYRINGE_VACCINE.equals(getSyringeType(item));
    }
    
    public boolean isMedicineSyringe(ItemStack item) {
        return SYRINGE_MEDICINE.equals(getSyringeType(item));
    }
    
    public String getSyringeData(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return null;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }
}