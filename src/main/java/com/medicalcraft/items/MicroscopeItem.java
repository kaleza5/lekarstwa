package com.medicalcraft.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import com.medicalcraft.MedicalCraftPlugin;

import java.util.Arrays;

public class MicroscopeItem {
    
    public static final String MICROSCOPE_BASIC = "microscope_basic";
    public static final String MICROSCOPE_ADVANCED = "microscope_advanced";
    public static final String MICROSCOPE_ELECTRON = "microscope_electron";
    
    private final MedicalCraftPlugin plugin;
    private final NamespacedKey microscopeKey;
    
    public MicroscopeItem(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.microscopeKey = new NamespacedKey(plugin, "microscope_type");
    }
    
    public ItemStack createBasicMicroscope() {
        ItemStack microscope = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = microscope.getItemMeta();
        
        meta.setDisplayName("§7Mikroskop Podstawowy");
        meta.setLore(Arrays.asList(
            "§8• §7Podstawowe badania krwi",
            "§8• §7Wykrywa wirusy i bakterie",
            "§8• §750% dokładności",
            "§8• §7Użyj z probówką krwi"
        ));
        
        meta.getPersistentDataContainer().set(microscopeKey, PersistentDataType.STRING, MICROSCOPE_BASIC);
        microscope.setItemMeta(meta);
        
        return microscope;
    }
    
    public ItemStack createAdvancedMicroscope() {
        ItemStack microscope = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = microscope.getItemMeta();
        
        meta.setDisplayName("§aMikroskop Zaawansowany");
        meta.setLore(Arrays.asList(
            "§8• §7Zaawansowane badania krwi",
            "§8• §7Wykrywa wirusy, bakterie i pasożyty",
            "§8• §780% dokładności",
            "§8• §7Szybsze wyniki",
            "§8• §7Użyj z probówką krwi"
        ));
        
        meta.getPersistentDataContainer().set(microscopeKey, PersistentDataType.STRING, MICROSCOPE_ADVANCED);
        microscope.setItemMeta(meta);
        
        return microscope;
    }
    
    public ItemStack createElectronMicroscope() {
        ItemStack microscope = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = microscope.getItemMeta();
        
        meta.setDisplayName("§5Mikroskop Elektronowy");
        meta.setLore(Arrays.asList(
            "§8• §7Najnowocześniejsze badania",
            "§8• §7Wykrywa wszystkie patogeny",
            "§8• §795% dokładności",
            "§8• §7Natychmiastowe wyniki",
            "§8• §7Wykrywa priony i nowotwory",
            "§8• §7Użyj z probówką krwi"
        ));
        
        meta.getPersistentDataContainer().set(microscopeKey, PersistentDataType.STRING, MICROSCOPE_ELECTRON);
        microscope.setItemMeta(meta);
        
        return microscope;
    }
    
    public String getMicroscopeType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(microscopeKey, PersistentDataType.STRING);
    }
    
    public boolean isMicroscope(ItemStack item) {
        return getMicroscopeType(item) != null;
    }
    
    public int getMicroscopeAccuracy(ItemStack item) {
        String type = getMicroscopeType(item);
        if (type == null) return 0;
        
        switch (type) {
            case MICROSCOPE_BASIC: return 50;
            case MICROSCOPE_ADVANCED: return 80;
            case MICROSCOPE_ELECTRON: return 95;
            default: return 0;
        }
    }
    
    public String getMicroscopeName(ItemStack item) {
        String type = getMicroscopeType(item);
        if (type == null) return "Unknown Microscope";
        
        switch (type) {
            case MICROSCOPE_BASIC: return "Mikroskop Podstawowy";
            case MICROSCOPE_ADVANCED: return "Mikroskop Zaawansowany";
            case MICROSCOPE_ELECTRON: return "Mikroskop Elektronowy";
            default: return "Unknown Microscope";
        }
    }
}