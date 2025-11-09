package com.medicalcraft.items;

import com.medicalcraft.MedicalCraftPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.*;

public class MedicalItemsManager {
    
    private final MedicalCraftPlugin plugin;
    private final Map<String, MedicalItem> medicalItems;
    private final NamespacedKey itemKey;
    
    public MedicalItemsManager(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.medicalItems = new HashMap<>();
        this.itemKey = new NamespacedKey(plugin, "medical_item_type");
        initializeItems();
    }
    
    private void initializeItems() {
        // === MEDICATIONS & DRUGS (15 items) ===
        
        // Painkillers
        addItem("ibuprofen", "Ibuprofen", Material.SUGAR, 
                Arrays.asList("§8• §7Lek przeciwbólowy i przeciwzapalny", 
                             "§8• §7Redukuje ból i stany zapalne", 
                             "§8• §7Dawka: 200mg", "§8• §7Nie przekraczać 3 dawek dziennie"));
        
        addItem("paracetamol", "Paracetamol", Material.SUGAR, 
                Arrays.asList("§8• §7Lek przeciwbólowy i przeciwgorączkowy", 
                             "§8• §7Redukuje gorączkę i ból", 
                             "§8• §7Dawka: 500mg", "§8• §7Bezpieczny dla zwierząt"));
        
        addItem("morphine", "Morfina", Material.GLOWSTONE_DUST, 
                Arrays.asList("§8• §7Silny lek przeciwbólowy", 
                             "§8• §7Używać tylko w ciężkich przypadkach", 
                             "§8• §7Może uzależniać", "§c§l§oKontrolowana substancja"));
        
        // Antibiotics
        addItem("amoxicillin", "Amoksycylina", Material.LIGHT_BLUE_DYE, 
                Arrays.asList("§8• §7Antybiotyk szerokiego spektrum", 
                             "§8• §7Działa przeciwko bakteriom", 
                             "§8• §7Dawka: 250mg", "§8• §7Kuracja: 7-10 dni"));
        
        addItem("penicillin", "Penicylina", Material.PURPLE_DYE, 
                Arrays.asList("§8• §7Pierwszy antybiotyk", 
                             "§8• §7Skuteczna przeciwko gronkowcom", 
                             "§8• §7Dawka: 300mg", "§8• §7Sprawdza się od 1928 roku"));
        
        addItem("tetracycline", "Tetracyklina", Material.LAPIS_LAZULI, 
                Arrays.asList("§8• §7Antybiotyk tetracyklinowy", 
                             "§8• §7Działa na szerokie spektrum bakterii", 
                             "§8• §7Dawka: 250mg", "§8• §7Nie łączyć z mlekiem"));
        
        // Antivirals
        addItem("acyclovir", "Acyklowir", Material.RED_DYE, 
                Arrays.asList("§8• §7Lek przeciwwirusowy", 
                             "§8• §7Skuteczny przeciwko wirusom opryszczki", 
                             "§8• §7Dawka: 200mg", "§8• §7Stosować przy pierwszych objawach"));
        
        addItem("oseltamivir", "Oseltamiwir", Material.ORANGE_DYE, 
                Arrays.asList("§8• §7Lek przeciwwirusowy przeciwko grypie", 
                             "§8• §7Skraca czas trwania grypy", 
                             "§8• §7Dawka: 75mg", "§8• §7Najskuteczniejszy w pierwszych 48h"));
        
        // Antifungals
        addItem("nystatin", "Nystatyna", Material.YELLOW_DYE, 
                Arrays.asList("§8• §7Lek przeciwgrzybiczy", 
                             "§8• §7Skuteczny przeciwko Candida", 
                             "§8• §7Dawka: 100 000 jednostek", "§8• §7Stosować miejscowo"));
        
        addItem("clotrimazole", "Klotrimazol", Material.LIME_DYE, 
                Arrays.asList("§8• §7Lek przeciwgrzybiczy", 
                             "§8• §7Działa przeciwko dermatofitom", 
                             "§8• §7Dawka: 1%", "§8• §7Stosować 2-3 razy dziennie"));
        
        // Antiparasitics
        addItem("ivermectin", "Iwermektyna", Material.CYAN_DYE, 
                Arrays.asList("§8• §7Lek przeciwpasożytniczy", 
                             "§8• §7Skuteczny przeciwko robakom i pasożytom", 
                             "§8• §7Dawka: 0.2mg/kg", "§8• §7Szerokie spektrum działania"));
        
        addItem("metronidazole", "Metronidazol", Material.BLUE_DYE, 
                Arrays.asList("§8• §7Lek przeciwpierwotniakowy", 
                             "§8• §7Działa przeciwko Giardia i Trichomonas", 
                             "§8• §7Dawka: 250mg", "§8• §7Unikać alkoholu"));
        
        // Anti-inflammatories
        addItem("prednisone", "Prednizon", Material.BROWN_DYE, 
                Arrays.asList("§8• §7Lek przeciwzapalny", 
                             "§8• §7Hamuje reakcje zapalne", 
                             "§8• §7Dawka: 5mg", "§c§l§oStosować ostrożnie"));
        
        addItem("dexamethasone", "Deksametazon", Material.GRAY_DYE, 
                Arrays.asList("§8• §7Silny lek przeciwzapalny", 
                             "§8• §7Działa szybko i skutecznie", 
                             "§8• §7Dawka: 0.5mg", "§8• §7Krótkotrwałe działanie"));
        
        // === MEDICAL SUPPLIES (20 items) ===
        
        // Bandages and dressings
        addItem("bandage_small", "Mały Bandaż", Material.PAPER, 
                Arrays.asList("§8• §7Podstawowy opatrunek", 
                             "§8• §7Zatrzymuje krwawienie", 
                             "§8• §7Jednorazowy", "§8• §7Rozmiar: 5x5cm"));
        
        addItem("bandage_large", "Duży Bandaż", Material.MAP, 
                Arrays.asList("§8• §7Duży opatrunek", 
                             "§8• §7Zatrzymuje duże krwawienie", 
                             "§8• §7Jednorazowy", "§8• §7Rozmiar: 10x10cm"));
        
        addItem("gauze_pad", "Gaza Opatrunkowa", Material.WHITE_CARPET, 
                Arrays.asList("§8• §7Podstawowy materiał opatrunkowy", 
                             "§8• §7Absorbuje wydzieliny", 
                             "§8• §7Sterylna", "§8• §7Rozmiar: 10x10cm"));
        
        addItem("adhesive_bandage", "Plaster", Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 
                Arrays.asList("§8• §7Samoprzylepny opatrunek", 
                             "§8• §7Na małe rany", 
                             "§8• §7Łatwy w użyciu", "§8• §7Rozmiar: 2x7cm"));
        
        addItem("elastic_bandage", "Bandaż Elastyczny", Material.STRING, 
                Arrays.asList("§8• §7Elastyczny opatrunkowy", 
                             "§8• §7Działa jak opaska uciskowa", 
                             "§8• §7Można ponownie użyć", "§8• §7Długość: 3m"));
        
        // Medical tools
        addItem("thermometer", "Termometr", Material.GLASS_BOTTLE, 
                Arrays.asList("§8• §7Mierzy temperaturę ciała", 
                             "§8• §7Normalna: 38-39°C", 
                             "§8• §7Gorączka: >39.5°C", "§8• §7Używać doodbytniczo"));
        
        addItem("stethoscope", "Stetoskop", Material.IRON_NUGGET, 
                Arrays.asList("§8• §7Słucha dźwięków wewnętrznych", 
                             "§8• §7Sprawdza pracę serca i płuc", 
                             "§8• §7Profesjonalny sprzęt", "§8• §7Używać na klatce piersiowej"));
        
        addItem("blood_pressure_cuff", "Mansetka do Ciśnienia", Material.LEATHER, 
                Arrays.asList("§8• §7Mierzy ciśnienie krwi", 
                             "§8• §7Normalne: 120/80 mmHg", 
                             "§8• §7Nakładać na łapę", "§8• §7Profesjonalny sprzęt"));
        
        addItem("otoscope", "Otofon", Material.ENDER_EYE, 
                Arrays.asList("§8• §7Sprawdza uszy", 
                             "§8• §7Widzi błonę bębenkową", 
                             "§8• §7Diagnostyka zakażeń", "§8• §7Używać ostrożnie"));
        
        addItem("ophthalmoscope", "Oftalmoskop", Material.ENDER_PEARL, 
                Arrays.asList("§8• §7Sprawdza oczy", 
                             "§8• §7Widzi siatkówkę", 
                             "§8• §7Diagnostyka chorób oczu", "§8• §7Wymaga ciemności"));
        
        // Surgical items
        addItem("scalpel", "Skalpel", Material.IRON_SWORD, 
                Arrays.asList("§8• §7Chirurgiczny nóż", 
                             "§8• §7Precyzyjne cięcia", 
                             "§8• §7Stal nierdzewna", "§c§l§oTylko dla profesjonalistów"));
        
        addItem("surgical_needle", "Chirurgiczna Igła", Material.IRON_INGOT, 
                Arrays.asList("§8• §7Igła do szycia", 
                             "§8• §7Zaokrąglony koniec", 
                             "§8• §7Rozmiar: 3-0", "§8• §7Stal nierdzewna"));
        
        addItem("surgical_thread", "Chirurgiczna Nić", Material.STRING, 
                Arrays.asList("§8• §7Nić do szycia ran", 
                             "§8• §7Materiał: Catgut", 
                             "§8• §7Rozpuszczalna", "§8• §7Długość: 75cm"));
        
        addItem("surgical_gloves", "Rękawice Chirurgiczne", Material.LEATHER_BOOTS, 
                Arrays.asList("§8• §7Chirurgiczne rękawice", 
                             "§8• §7Zabezpieczenie przed zakażeniem", 
                             "§8• §7Jednorazowe", "§8• §7Rozmiar: Medium"));
        
        addItem("surgical_mask", "Maska Chirurgiczna", Material.PAPER, 
                Arrays.asList("§8• §7Chirurgiczna maska", 
                             "§8• §7Chroni przed zakażeniem", 
                             "§8• §7Jednorazowa", "§8• §7Filtruje powietrze"));
        
        // Emergency supplies
        addItem("epipen", "Epipen", Material.STICK, 
                Arrays.asList("§8• §7Adrenalina wstrzyknięta", 
                             "§8• §7Leczy reakcje alergiczne", 
                             "§8• §7Dawka: 0.3mg", "§c§l§oUżywać w nagłych wypadkach"));
        
        addItem("defibrillator", "Defibrylator", Material.IRON_BLOCK, 
                Arrays.asList("§8• §7Przywraca rytm serca", 
                             "§8• §7Używać przy zatrzymaniu krążenia", 
                             "§8• §7Wysokie napięcie", "§c§l§oTylko dla profesjonalistów"));
        
        addItem("oxygen_mask", "Maska Tlenowa", Material.GLASS, 
                Arrays.asList("§8• §7Dostarcza tlen", 
                             "§8• §7Przy trudnościach z oddychaniem", 
                             "§8• §7Przepływ: 5L/min", "§8• §7Używać z butlą tlenową"));
        
        addItem("iv_bag", "Worek do Kroplówki", Material.POTION, 
                Arrays.asList("§8• §7Płyn do kroplówki", 
                             "§8• §8• §7Dostarcza płyny i leki", 
                             "§8• §7Objętość: 500ml", "§8• §7Używać z zestawem kroplowym"));
        
        addItem("iv_set", "Zestaw Kroplowy", Material.TRIPWIRE_HOOK, 
                Arrays.asList("§8• §7Zestaw do kroplówki", 
                             "§8• §7Łączy worek z wenflonem", 
                             "§8• §7Regulowany przepływ", "§8• §7Jednorazowy"));
        
        // === DIAGNOSTIC TOOLS (10 items) ===
        
        addItem("test_strip", "Pasek Testowy", Material.PAPER, 
                Arrays.asList("§8• §7Pasek do badań diagnostycznych", 
                             "§8• §7Wykrywa różne substancje", 
                             "§8• §7Szybki wynik", "§8• §7Jednorazowy"));
        
        addItem("urine_cup", "Pojemnik na Mocz", Material.GLASS_BOTTLE, 
                Arrays.asList("§8• §7Pojemnik na próbkę moczu", 
                             "§8• §7Do badań laboratoryjnych", 
                             "§8• §7Pojemność: 100ml", "§8• §7Sterylny"));
        
        addItem("blood_tube", "Probówka na Krew", Material.GLASS_BOTTLE, 
                Arrays.asList("§8• §7Probówka na krew", 
                             "§8• §7Do badań laboratoryjnych", 
                             "§8• §7Pojemność: 5ml", "§8• §7Z heparyną"));
        
        addItem("glucometer", "Glukometr", Material.COMPASS, 
                Arrays.asList("§8• §7Mierzy poziom cukru", 
                             "§8• §7Wymaga pasków testowych", 
                             "§8• §7Wynik w 5 sekund", "§8• §7Dla diabetyków"));
        
        addItem("pregnancy_test", "Test Ciążowy", Material.STICK, 
                Arrays.asList("§8• §7Wykrywa ciążę", 
                             "§8• §7Wynik w 3 minuty", 
                             "§8• §7Skuteczność 99%", "§8• §7Jednorazowy"));
        
        addItem("thermometer_digital", "Cyfrowy Termometr", Material.GLASS_BOTTLE, 
                Arrays.asList("§8• §7Cyfrowy pomiar temperatury", 
                             "§8• §7Wynik w 10 sekund", 
                             "§8• §7Dokładność ±0.1°C", "§8• §7Wodoodporny"));
        
        addItem("blood_pressure_monitor", "Automatyczny Miernik Ciśnienia", Material.COMPASS, 
                Arrays.asList("§8• §7Automatyczny pomiar ciśnienia", 
                             "§8• §8• §7Wynik w 30 sekund", 
                             "§8• §7Pamięć pomiarów", "§8• §7Baterie w zestawie"));
        
        addItem("pulse_oximeter", "Pulsoksymetr", Material.CLOCK, 
                Arrays.asList("§8• §7Mierzy tętno i saturację", 
                             "§8• §8• §7Nieinwazyjny pomiar", 
                             "§8• §7Wynik w 5 sekund", "§8• §7Używać na języku"));
        
        addItem("stethoscope_pediatric", "Dziecięcy Stetoskop", Material.GOLD_NUGGET, 
                Arrays.asList("§8• §7Stetoskop dla małych zwierząt", 
                             "§8• §7Mniejsza głowica", 
                             "§8• §7Lepsza czułość", "§8• §7Dla weterynarzy"));
        
        addItem("otoscope_pediatric", "Dziecięcy Otofon", Material.ENDER_EYE, 
                Arrays.asList("§8• §7Otofon dla małych zwierząt", 
                             "§8• §7Mniejsza głowica", 
                             "§8• §7Bezpieczny dla szczeniąt", "§8• §7Dla weterynarzy"));
        
        // === SPECIALIZED MEDICAL ITEMS (10 items) ===
        
        addItem("insulin", "Insulina", Material.POTION, 
                Arrays.asList("§8• §7Hormon regulujący cukier", 
                             "§8• §7Dla cukrzyków", 
                             "§8• §8• §7Dawka: 10 jednostek", "§c§l§oWymaga recepty"));
        
        addItem("epinephrine", "Adrenalina", Material.POTION, 
                Arrays.asList("§8• §7Hormon stresu", 
                             "§8• §7Leczy wstrząs anafilaktyczny", 
                             "§8• §7Dawka: 0.3mg", "§c§l§oTylko w nagłych wypadkach"));
        
        addItem("atropine", "Atropina", Material.POTION, 
                Arrays.asList("§8• §7Lek przeciwdrobnoustrojowy", 
                             "§8• §7Leczy zatrucia organicznofosforanami", 
                             "§8• §7Dawka: 0.5mg", "§c§l§oTylko dla profesjonalistów"));
        
        addItem("digoxin", "Digoksyna", Material.POTION, 
                Arrays.asList("§8• §7Lek nasercowy", 
                             "§8• §7Wspomaca pracę serca", 
                             "§8• §7Dawka: 0.25mg", "§c§l§oWymaga monitorowania"));
        
        addItem("furosemide", "Furosemid", Material.POTION, 
                Arrays.asList("§8• §7Lek moczopędny", 
                             "§8• §7Usuwa nadmiar płynów", 
                             "§8• §7Dawka: 40mg", "§8• §7Może powodować odwodnienie"));
        
        addItem("potassium_chloride", "Chlorek Potasu", Material.POTION, 
                Arrays.asList("§8• §7Uzupełnia potas", 
                             "§8• §7Przy niedoborach elektrolitów", 
                             "§8• §7Dawka: 10mmol", "§8• §7Rozpuścić w wodzie"));
        
        addItem("sodium_bicarbonate", "Wodorowęglan Sodu", Material.POTION, 
                Arrays.asList("§8• §7Zasadowy lek", 
                             "§8• §7Leczy zakwaszenie krwi", 
                             "§8• §7Dawka: 50mmol", "§8• §7Rozpuścić w wodzie"));
        
        addItem("calcium_gluconate", "Glukonian Wapnia", Material.POTION, 
                Arrays.asList("§8• §7Uzupełnia wapń", 
                             "§8• §7Przy hipokalcemii", 
                             "§8• §7Dawka: 1g", "§8• §7Wolno wstrzykiwać"));
        
        addItem("magnesium_sulfate", "Siarczan Magnezu", Material.POTION, 
                Arrays.asList("§8• §7Uzupełnia magnez", 
                             "§8• §7Przy hipomagnezemii", 
                             "§8• §7Dawka: 2g", "§8• §7Rozpuścić w wodzie"));
        
        addItem("dextrose", "Glukoza", Material.POTION, 
                Arrays.asList("§8• §7Cukier prosty", 
                             "§8• §7Dostarcza energii", 
                             "§8• §7Stężenie: 50%", "§8• §7Wstrzykiwać dożylnie"));
    }
    
    private void addItem(String id, String name, Material material, List<String> lore) {
        medicalItems.put(id, new MedicalItem(id, name, material, lore));
    }
    
    public ItemStack createMedicalItem(String itemId) {
        MedicalItem item = medicalItems.get(itemId);
        if (item == null) return null;
        
        ItemStack itemStack = new ItemStack(item.getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        
        meta.setDisplayName("§a" + item.getName());
        meta.setLore(item.getLore());
        
        // Add persistent data to identify the item
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, itemId);
        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    public ItemStack createMedicalItem(String itemId, int amount) {
        ItemStack item = createMedicalItem(itemId);
        if (item != null) {
            item.setAmount(amount);
        }
        return item;
    }
    
    public boolean isMedicalItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(itemKey, PersistentDataType.STRING);
    }
    
    public String getMedicalItemType(ItemStack item) {
        if (!isMedicalItem(item)) return null;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
    }
    
    public MedicalItem getMedicalItem(String itemId) {
        return medicalItems.get(itemId);
    }
    
    public Collection<MedicalItem> getAllMedicalItems() {
        return medicalItems.values();
    }
    
    public int getItemCount() {
        return medicalItems.size();
    }
    
    public List<String> getItemCategories() {
        return Arrays.asList("Leki i lekarstwa", "Materiały opatrunkowe", "Narzędzia medyczne", 
                           "Sprzęt chirurgiczny", "Wyposażenie ratunkowe", "Narzędzia diagnostyczne", 
                           "Leki specjalistyczne");
    }
    
    public List<String> getItemsByCategory(String category) {
        List<String> items = new ArrayList<>();
        
        switch (category) {
            case "Leki i lekarstwa":
                items.addAll(Arrays.asList("ibuprofen", "paracetamol", "morphine", "amoxicillin", 
                                         "penicillin", "tetracycline", "acyclovir", "oseltamivir", 
                                         "nystatin", "clotrimazole", "ivermectin", "metronidazole", 
                                         "prednisone", "dexamethasone"));
                break;
            case "Materiały opatrunkowe":
                items.addAll(Arrays.asList("bandage_small", "bandage_large", "gauze_pad", 
                                         "adhesive_bandage", "elastic_bandage"));
                break;
            case "Narzędzia medyczne":
                items.addAll(Arrays.asList("thermometer", "stethoscope", "blood_pressure_cuff", 
                                         "otoscope", "ophthalmoscope"));
                break;
            case "Sprzęt chirurgiczny":
                items.addAll(Arrays.asList("scalpel", "surgical_needle", "surgical_thread", 
                                         "surgical_gloves", "surgical_mask"));
                break;
            case "Wyposażenie ratunkowe":
                items.addAll(Arrays.asList("epipen", "defibrillator", "oxygen_mask", "iv_bag", "iv_set"));
                break;
            case "Narzędzia diagnostyczne":
                items.addAll(Arrays.asList("test_strip", "urine_cup", "blood_tube", "glucometer", 
                                         "pregnancy_test", "thermometer_digital", "blood_pressure_monitor", 
                                         "pulse_oximeter", "stethoscope_pediatric", "otoscope_pediatric"));
                break;
            case "Leki specjalistyczne":
                items.addAll(Arrays.asList("insulin", "epinephrine", "atropine", "digoxin", 
                                         "furosemide", "potassium_chloride", "sodium_bicarbonate", 
                                         "calcium_gluconate", "magnesium_sulfate", "dextrose"));
                break;
        }
        
        return items;
    }
    
    public static class MedicalItem {
        private final String id;
        private final String name;
        private final Material material;
        private final List<String> lore;
        
        public MedicalItem(String id, String name, Material material, List<String> lore) {
            this.id = id;
            this.name = name;
            this.material = material;
            this.lore = lore;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public Material getMaterial() { return material; }
        public List<String> getLore() { return lore; }
    }
}