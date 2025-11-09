package com.medicalcraft.commands;

import com.medicalcraft.MedicalCraftPlugin;
import com.medicalcraft.managers.MedicalItemsManager;
import com.medicalcraft.items.SyringeItem;
import com.medicalcraft.managers.VaccineManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MedCommand implements CommandExecutor, TabCompleter, Listener {
    
    private final MedicalCraftPlugin plugin;
    private final MedicalItemsManager itemsManager;
    private final VaccineManager vaccineManager;
    private final SyringeItem syringeItem;
    
    // GUI Inventories
    private final Map<UUID, Inventory> mainMenuInventories = new HashMap<>();
    private final Map<UUID, Inventory> categoryInventories = new HashMap<>();
    private final Map<UUID, String> playerNavigation = new HashMap<>();
    
    public MedCommand(MedicalCraftPlugin plugin) {
        this.plugin = plugin;
        this.itemsManager = plugin.getMedicalItemsManager();
        this.vaccineManager = plugin.getVaccineManager();
        this.syringeItem = new SyringeItem(plugin);
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTa komenda może być użyta tylko przez gracza!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            openMainMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                handleGiveCommand(player, args);
                break;
            case "search":
                handleSearchCommand(player, args);
                break;
            case "help":
                showHelp(player);
                break;
            case "list":
                showItemList(player);
                break;
            case "categories":
                showCategories(player);
                break;
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void openMainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§8§lMEDYCZNE MENU GŁÓWNE");
        
        // Fill with glass panes
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < 54; i++) {
            menu.setItem(i, glass);
        }
        
        // Category buttons
        setCategoryButton(menu, 10, "Leki i lekarstwa", Material.POTION, "§a§lLEKI I LEKARSTWA", 
                         Arrays.asList("§8• §7Leki przeciwbólowe", "§8• §7Antybiotyki", "§8• §7Leki przeciwwirusowe", "§8• §7Leki przeciwgrzybicze"));
        
        setCategoryButton(menu, 12, "Materiały opatrunkowe", Material.PAPER, "§a§lMATERIAŁY OPATRUNKOWE", 
                         Arrays.asList("§8• §7Bandaże", "§8• §7Gaza", "§8• §7Plastry", "§8• §7Opaski elastyczne"));
        
        setCategoryButton(menu, 14, "Narzędzia medyczne", Material.COMPASS, "§a§lNARZĘDZIA MEDYCZNE", 
                         Arrays.asList("§8• §7Termometry", "§8• §7Stetoskopy", "§8• §7Mierniki ciśnienia", "§8• §7Otofony"));
        
        setCategoryButton(menu, 16, "Sprzęt chirurgiczny", Material.IRON_SWORD, "§a§lSPRZĘT CHIRURGICZNY", 
                         Arrays.asList("§8• §7Skalpiele", "§8• §7Igły", "§8• §7Nici", "§8• §7Rękawice"));
        
        setCategoryButton(menu, 28, "Wyposażenie ratunkowe", Material.TOTEM_OF_UNDYING, "§a§lWYPOSAŻENIE RATUNKOWE", 
                         Arrays.asList("§8• §7Epipeny", "§8• §7Defibrylatory", "§8• §7Maski tlenowe", "§8• §7Kroplówki"));
        
        setCategoryButton(menu, 30, "Narzędzia diagnostyczne", Material.SPYGLASS, "§a§lNARZĘDZIA DIAGNOSTYCZNE", 
                         Arrays.asList("§8• §7Paski testowe", "§8• §7Glukometry", "§8• §7Testy ciążowe", "§8• §7Pulsoksymetry"));
        
        setCategoryButton(menu, 32, "Leki specjalistyczne", Material.POTION, "§a§lLEKI SPECJALISTYCZNE", 
                         Arrays.asList("§8• §7Insulina", "§8• §7Adrenalina", "§8• §7Leki sercowe", "§8• §7Elektrolity"));
        
        setCategoryButton(menu, 34, "Strzykawki i szczepionki", Material.SPLASH_POTION, "§a§lSTRZYKAWKI I SZCZEPIONKI", 
                         Arrays.asList("§8• §7Strzykawki", "§8• §7Szczepionki", "§8• §7Probówki", "§8• §7Mikroskopy"));
        
        // Quick access buttons
        setQuickAccessButton(menu, 38, "Strzykawka", Material.GLASS_BOTTLE, "§e§lSTRZYKAWKA", 
                           Arrays.asList("§8• §7Pusta strzykawka", "§8• §7Do pobierania krwi", "§8• §7Kliknij aby otrzymać"));
        
        setQuickAccessButton(menu, 40, "Podstawowy zestaw", Material.CHEST, "§e§lPODSTAWOWY ZESTAW MEDYCZNY", 
                           Arrays.asList("§8• §7Zawiera podstawowe leki", "§8• §7Materiały opatrunkowe", "§8• §8• §7Narzędzia diagnostyczne"));
        
        setQuickAccessButton(menu, 42, "Zestaw ratunkowy", Material.ENDER_CHEST, "§c§lZESTAW RATUNKOWY", 
                           Arrays.asList("§8• §7Epipen", "§8• §7Defibrylator", "§8• §7Maska tlenowa", "§8• §7Leki ratunkowe"));
        
        // Info and navigation
        setInfoButton(menu, 49, "Informacje", Material.BOOK, "§b§lINFORMACJE", 
                     Arrays.asList("§8• §7Wersja: 1.0.0", "§8• §7Autor: MedicalCraft", "§8• §7Pomoc: /med help"));
        
        setNavigationButton(menu, 45, "Wyszukaj", Material.COMPASS, "§6§lWYSZUKAJ", 
                           Arrays.asList("§8• §7Użyj: /med search <nazwa>", "§8• §7Znajdź konkretny przedmiot"));
        
        setNavigationButton(menu, 53, "Zamknij", Material.BARRIER, "§c§lZAMKNIJ", 
                           Arrays.asList("§8• §7Kliknij aby zamknąć menu"));
        
        mainMenuInventories.put(player.getUniqueId(), menu);
        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }
    
    private void setCategoryButton(Inventory menu, int slot, String category, Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        lore.add("");
        lore.add("§eKliknij aby otworzyć kategorię");
        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    
    private void setQuickAccessButton(Inventory menu, int slot, String itemName, Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        lore.add("");
        lore.add("§eKliknij aby otrzymać");
        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    
    private void setInfoButton(Inventory menu, int slot, String info, Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    
    private void setNavigationButton(Inventory menu, int slot, String action, Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        
        // Check if it's our inventory
        if (!inventory.equals(mainMenuInventories.get(player.getUniqueId())) && 
            !inventory.equals(categoryInventories.get(player.getUniqueId()))) {
            return;
        }
        
        event.setCancelled(true); // Prevent item moving
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        
        // Handle main menu clicks
        if (inventory.equals(mainMenuInventories.get(player.getUniqueId()))) {
            handleMainMenuClick(player, displayName);
        }
        
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }
    
    private void handleMainMenuClick(Player player, String itemName) {
        switch (itemName) {
            case "LEKI I LEKARSTWA":
                openCategoryMenu(player, "Leki i lekarstwa");
                break;
            case "MATERIAŁY OPATRUNKOWE":
                openCategoryMenu(player, "Materiały opatrunkowe");
                break;
            case "NARZĘDZIA MEDYCZNE":
                openCategoryMenu(player, "Narzędzia medyczne");
                break;
            case "SPRZĘT CHIRURGICZNY":
                openCategoryMenu(player, "Sprzęt chirurgiczny");
                break;
            case "WYPOSAŻENIE RATUNKOWE":
                openCategoryMenu(player, "Wyposażenie ratunkowe");
                break;
            case "NARZĘDZIA DIAGNOSTYCZNE":
                openCategoryMenu(player, "Narzędzia diagnostyczne");
                break;
            case "LEKI SPECJALISTYCZNE":
                openCategoryMenu(player, "Leki specjalistyczne");
                break;
            case "STRZYKAWKI I SZCZEPIONKI":
                openSyringeMenu(player);
                break;
            case "STRZYKAWKA":
                giveItem(player, "syringe_empty");
                break;
            case "PODSTAWOWY ZESTAW MEDYCZNY":
                giveBasicMedicalKit(player);
                break;
            case "ZESTAW RATUNKOWY":
                giveEmergencyKit(player);
                break;
            case "WYSZUKAJ":
                player.sendMessage("§eUżyj: /med search <nazwa>");
                player.closeInventory();
                break;
            case "ZAMKNIJ":
                player.closeInventory();
                break;
        }
    }
    
    private void openCategoryMenu(Player player, String category) {
        List<String> itemIds = itemsManager.getItemsByCategory(category);
        int inventorySize = Math.min(54, ((itemIds.size() + 8) / 9) * 9 + 9);
        
        Inventory menu = Bukkit.createInventory(null, inventorySize, "§8§l" + category.toUpperCase());
        
        // Fill with glass panes
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < inventorySize; i++) {
            menu.setItem(i, glass);
        }
        
        // Add items
        int slot = 0;
        for (String itemId : itemIds) {
            if (slot >= inventorySize - 9) break; // Leave space for navigation
            
            ItemStack item = itemsManager.createMedicalItem(itemId);
            if (item != null) {
                menu.setItem(slot, item);
                slot++;
            }
        }
        
        // Navigation buttons
        setNavigationButton(menu, inventorySize - 9, "Powrót", Material.ARROW, "§e§lPOWRÓT", 
                           Arrays.asList("§8• §7Wróć do głównego menu"));
        
        setNavigationButton(menu, inventorySize - 1, "Zamknij", Material.BARRIER, "§c§lZAMKNIJ", 
                           Arrays.asList("§8• §7Zamknij menu"));
        
        categoryInventories.put(player.getUniqueId(), menu);
        playerNavigation.put(player.getUniqueId(), "category:" + category);
        player.openInventory(menu);
    }
    
    private void openSyringeMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§8§lSTRZYKAWKI I SZCZEPIONKI");
        
        // Fill with glass panes
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < 27; i++) {
            menu.setItem(i, glass);
        }
        
        // Syringe items
        setQuickAccessButton(menu, 10, "Pusta strzykawka", Material.GLASS_BOTTLE, "§e§lPUSTA STRZYKAWKA", 
                           Arrays.asList("§8• §7Do pobierania krwi", "§8• §7Kliknij aby otrzymać"));
        
        setQuickAccessButton(menu, 12, "Strzykawka z krwią", Material.POTION, "§c§lSTRZYKAWKA Z KRWIĄ", 
                           Arrays.asList("§8• §7Do badań laboratoryjnych", "§8• §7Kliknij aby otrzymać"));
        
        setQuickAccessButton(menu, 14, "Mikroskop podstawowy", Material.SPYGLASS, "§a§lMIKROSKOP PODSTAWOWY", 
                           Arrays.asList("§8• §7Dokładność: 50%", "§8• §7Do badań krwi", "§8• §7Kliknij aby otrzymać"));
        
        setQuickAccessButton(menu, 16, "Mikroskop zaawansowany", Material.SPYGLASS, "§b§lMIKROSKOP ZAAWANSOWANY", 
                           Arrays.asList("§8• §7Dokładność: 80%", "§8• §7Do badań krwi", "§8• §7Kliknij aby otrzymać"));
        
        // Vaccine buttons
        int slot = 19;
        int count = 0;
        for (VaccineManager.Vaccine vaccine : vaccineManager.getAllVaccines()) {
            if (count >= 7) break; // Limit to 7 vaccines in this menu
            
            ItemStack vaccineVial = vaccineManager.createVaccineVial(vaccine.getType());
            if (vaccineVial != null) {
                menu.setItem(slot, vaccineVial);
                slot++;
                count++;
            }
        }
        
        // Navigation
        setNavigationButton(menu, 22, "Powrót", Material.ARROW, "§e§lPOWRÓT", 
                           Arrays.asList("§8• §7Wróć do głównego menu"));
        
        setNavigationButton(menu, 26, "Zamknij", Material.BARRIER, "§c§lZAMKNIJ", 
                           Arrays.asList("§8• §7Zamknij menu"));
        
        categoryInventories.put(player.getUniqueId(), menu);
        player.openInventory(menu);
    }
    
    private void giveItem(Player player, String itemId) {
        ItemStack item = null;
        
        // Handle special items
        if (itemId.equals("syringe_empty")) {
            item = syringeItem.createEmptySyringe();
        } else {
            item = itemsManager.createMedicalItem(itemId);
        }
        
        if (item == null) {
            player.sendMessage("§cNie można stworzyć tego przedmiotu!");
            return;
        }
        
        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cNie masz miejsca w ekwipunku!");
            return;
        }
        
        player.getInventory().addItem(item);
        player.sendMessage("§aOtrzymałeś: §f" + item.getItemMeta().getDisplayName());
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
    
    private void giveBasicMedicalKit(Player player) {
        String[] items = {
            "bandage_small", "bandage_large", "gauze_pad", "adhesive_bandage",
            "thermometer", "paracetamol", "ibuprofen", "amoxicillin"
        };
        
        for (String itemId : items) {
            ItemStack item = itemsManager.createMedicalItem(itemId, 5);
            if (item != null && player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            }
        }
        
        player.sendMessage("§aOtrzymałeś podstawowy zestaw medyczny!");
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
    
    private void giveEmergencyKit(Player player) {
        String[] items = {
            "epipen", "oxygen_mask", "iv_bag", "iv_set"
        };
        
        for (String itemId : items) {
            ItemStack item = itemsManager.createMedicalItem(itemId, 2);
            if (item != null && player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            }
        }
        
        player.sendMessage("§aOtrzymałeś zestaw ratunkowy!");
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
    
    private void handleGiveCommand(Player player, String[] args) {
        if (!player.hasPermission("medicalcraft.med.give")) {
            player.sendMessage("§cNie masz uprawnień do dawania przedmiotów!");
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage("§cUżycie: /med give <gracz> <przedmiot> [ilość]");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§cNie znaleziono gracza: §f" + args[1]);
            return;
        }
        
        String itemId = args[2].toLowerCase();
        int amount = 1;
        
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount < 1 || amount > 64) {
                    player.sendMessage("§cIlość musi być między 1 a 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cNieprawidłowa ilość!");
                return;
            }
        }
        
        // Check if item exists
        if (itemsManager.getMedicalItem(itemId) == null && !itemId.equals("syringe_empty")) {
            player.sendMessage("§cNie znaleziono przedmiotu: §f" + itemId);
            return;
        }
        
        // Create and give item
        ItemStack item = itemsManager.createMedicalItem(itemId, amount);
        if (item == null && itemId.equals("syringe_empty")) {
            item = syringeItem.createEmptySyringe();
            if (item != null) item.setAmount(amount);
        }
        
        if (item == null) {
            player.sendMessage("§cBłąd podczas tworzenia przedmiotu!");
            return;
        }
        
        // Check target inventory space
        int emptySlots = 0;
        for (ItemStack invItem : target.getInventory().getContents()) {
            if (invItem == null) emptySlots++;
        }
        
        if (emptySlots < Math.ceil(amount / 64.0)) {
            player.sendMessage("§cGracz §f" + target.getName() + " §cnie ma wystarczająco miejsca w ekwipunku!");
            return;
        }
        
        // Give items
        int given = 0;
        while (given < amount) {
            int stackSize = Math.min(64, amount - given);
            ItemStack stack = item.clone();
            stack.setAmount(stackSize);
            target.getInventory().addItem(stack);
            given += stackSize;
        }
        
        player.sendMessage("§aDano §f" + given + " §aprzedmiotów §f" + item.getItemMeta().getDisplayName() + " §agraczowi §f" + target.getName());
        target.sendMessage("§aOtrzymałeś §f" + given + " §aprzedmiotów §f" + item.getItemMeta().getDisplayName() + " §aod §f" + player.getName());
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
    
    private void handleSearchCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUżycie: /med search <nazwa>");
            return;
        }
        
        String searchTerm = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        List<String> foundItems = new ArrayList<>();
        
        // Search in medical items
        for (MedicalItemsManager.MedicalItem item : itemsManager.getAllMedicalItems()) {
            if (item.getName().toLowerCase().contains(searchTerm) || 
                item.getId().toLowerCase().contains(searchTerm)) {
                foundItems.add(item.getId());
            }
        }
        
        // Search in vaccines
        for (VaccineManager.Vaccine vaccine : vaccineManager.getAllVaccines()) {
            if (vaccine.getDisplayName().toLowerCase().contains(searchTerm) || 
                vaccine.getType().toLowerCase().contains(searchTerm)) {
                foundItems.add("vaccine:" + vaccine.getType());
            }
        }
        
        if (foundItems.isEmpty()) {
            player.sendMessage("§cNie znaleziono przedmiotów pasujących do: §f" + searchTerm);
            return;
        }
        
        player.sendMessage("§8=== §a§lWYNIKI WYSZUKIWANIA §8===");
        player.sendMessage("§7Szukano: §f" + searchTerm);
        player.sendMessage("§7Znaleziono §f" + foundItems.size() + " §7przedmiotów:");
        
        for (String itemId : foundItems) {
            if (itemId.startsWith("vaccine:")) {
                String vaccineType = itemId.substring(8);
                VaccineManager.Vaccine vaccine = vaccineManager.getVaccine(vaccineType);
                if (vaccine != null) {
                    player.sendMessage("§8• §a" + vaccine.getDisplayName() + " §8- §7Szczepionka");
                }
            } else {
                MedicalItemsManager.MedicalItem item = itemsManager.getMedicalItem(itemId);
                if (item != null) {
                    player.sendMessage("§8• §a" + item.getName() + " §8- §7Przedmiot medyczny");
                }
            }
        }
        
        player.sendMessage("§7Użyj §f/med give <gracz> <nazwa> §7aby otrzymać przedmiot");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§8=== §a§lMEDYCZNE MENU - POMOC §8===");
        player.sendMessage("§8• §7/med §f- §7Otwiera główne menu medyczne");
        player.sendMessage("§8• §7/med give <gracz> <przedmiot> [ilość] §f- §7Daje przedmiot graczowi");
        player.sendMessage("§8• §7/med search <nazwa> §f- §7Szuka przedmiotów po nazwie");
        player.sendMessage("§8• §7/med list §f- §7Pokazuje listę wszystkich przedmiotów");
        player.sendMessage("§8• §7/med categories §f- §7Pokazuje dostępne kategorie");
        player.sendMessage("§8• §7/med help §f- §7Pokazuje tę pomoc");
        player.sendMessage("");
        player.sendMessage("§8• §7Dostępne komendy: §f/syringe, /microscope, /vaccine");
        player.sendMessage("§8• §7Uprawnienia: §fmedicalcraft.med.give §7(dawanie przedmiotów)");
    }
    
    private void showItemList(Player player) {
        player.sendMessage("§8=== §a§lLISTA PRZEDMIOTÓW MEDYCZNYCH §8===");
        player.sendMessage("§7Łącznie: §f" + itemsManager.getItemCount() + " §7przedmiotów");
        player.sendMessage("");
        
        for (String category : itemsManager.getItemCategories()) {
            player.sendMessage("§a§l" + category.toUpperCase() + "§8:");
            List<String> categoryItems = itemsManager.getItemsByCategory(category);
            for (String itemId : categoryItems) {
                MedicalItemsManager.MedicalItem item = itemsManager.getMedicalItem(itemId);
                if (item != null) {
                    player.sendMessage("  §8• §f" + item.getName() + " §8- §7" + itemId);
                }
            }
            player.sendMessage("");
        }
        
        player.sendMessage("§7Szczepionki: Użyj §f/vaccine list §7aby zobaczyć dostępne szczepionki");
    }
    
    private void showCategories(Player player) {
        player.sendMessage("§8=== §a§lKATEGORIE PRZEDMIOTÓW §8===");
        
        for (String category : itemsManager.getItemCategories()) {
            List<String> items = itemsManager.getItemsByCategory(category);
            player.sendMessage("§8• §a" + category + " §8- §f" + items.size() + " §7przedmiotów");
        }
        
        player.sendMessage("");
        player.sendMessage("§7Użyj §f/med §7aby otworzyć menu i przeglądać kategorie");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "search", "list", "categories", "help");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        
        if (args.length >= 2 && args[0].equalsIgnoreCase("search")) {
            List<String> itemNames = new ArrayList<>();
            for (MedicalItemsManager.MedicalItem item : itemsManager.getAllMedicalItems()) {
                itemNames.add(item.getName().toLowerCase());
            }
            return itemNames;
        }
        
        return new ArrayList<>();
    }
}