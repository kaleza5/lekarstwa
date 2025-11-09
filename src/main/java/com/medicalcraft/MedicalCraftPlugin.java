package com.medicalcraft;

import com.medicalcraft.commands.MedCommand;
import com.medicalcraft.commands.MicroscopeCommand;
import com.medicalcraft.commands.SyringeCommand;
import com.medicalcraft.commands.VaccineCommand;
import com.medicalcraft.listeners.AnimalDiseaseListener;
import com.medicalcraft.listeners.MedicalItemListener;
import com.medicalcraft.managers.DiseaseManager;
import com.medicalcraft.managers.MedicalItemsManager;
import com.medicalcraft.managers.MicroscopeManager;
import com.medicalcraft.managers.VaccineManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MedicalCraftPlugin extends JavaPlugin {
    
    private static MedicalCraftPlugin instance;
    
    private DiseaseManager diseaseManager;
    private MedicalItemsManager medicalItemsManager;
    private MicroscopeManager microscopeManager;
    private VaccineManager vaccineManager;
    
    @Override
    public void onEnable() {
        instance = this;
        // Ensure default config is created for CustomModelData & settings
        this.saveDefaultConfig();
        
        // Initialize managers
        diseaseManager = new DiseaseManager(this);
        medicalItemsManager = new MedicalItemsManager(this);
        microscopeManager = new MicroscopeManager(this);
        vaccineManager = new VaccineManager(this);
        
        // Register commands
        MedCommand medCommand = new MedCommand(this);
        this.getCommand("med").setExecutor(medCommand);
        this.getCommand("med").setTabCompleter(medCommand);
        this.getCommand("syringe").setExecutor(new SyringeCommand(this));
        this.getCommand("microscope").setExecutor(new MicroscopeCommand(this));
        this.getCommand("vaccine").setExecutor(new VaccineCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new AnimalDiseaseListener(this), this);
        getServer().getPluginManager().registerEvents(new MedicalItemListener(this), this);
        
        // Initialize items and diseases
        medicalItemsManager.initializeItems();
        diseaseManager.initializeDiseases();
        
        getLogger().info("MedicalCraft Plugin Enabled!");
        getLogger().info("Loaded " + medicalItemsManager.getItemCount() + " medical items");
        getLogger().info("Loaded " + diseaseManager.getDiseaseCount() + " animal diseases");
    }
    
    @Override
    public void onDisable() {
        diseaseManager.saveData();
        getLogger().info("MedicalCraft Plugin Disabled!");
    }
    
    public static MedicalCraftPlugin getInstance() {
        return instance;
    }
    
    public DiseaseManager getDiseaseManager() {
        return diseaseManager;
    }
    
    public MedicalItemsManager getMedicalItemsManager() {
        return medicalItemsManager;
    }
    
    public MicroscopeManager getMicroscopeManager() {
        return microscopeManager;
    }
    
    public VaccineManager getVaccineManager() {
        return vaccineManager;
    }
}