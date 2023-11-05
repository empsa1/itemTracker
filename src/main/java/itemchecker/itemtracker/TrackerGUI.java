package itemchecker.itemtracker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


//The actual GUi
public class TrackerGUI implements Listener {
    private final JavaPlugin plugin;
    private static final int ITEMS_PER_PAGE = 36;

    public TrackerGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void open(Player player, int page) {
        List<String> trackedData = readTrackedDataFromFile();
        int maxPages = (int) Math.ceil((double) trackedData.size() / ITEMS_PER_PAGE);

        if (page < 1 || page > maxPages) {
            player.sendMessage("Invalid page");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "Tracker GUI - Page " + page);
        int slot = 0;

        ItemStack ownerHead = null;
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, trackedData.size());

        for (int i = startIndex; i < endIndex; i++) {
            String[] parts = trackedData.get(i).split(":");
            if (parts.length == 4) {
                String lore = parts[2];
                String name = parts[3];
                if (parts[0].equals("1")) { // inside a player inventory
                    UUID ownerUUID = UUID.fromString(parts[1]);
                    ownerHead = createOwnerHead(ownerUUID);
                }
                else if (parts[0].equals("2")) { // inside a chest
                    ownerHead = new ItemStack(Material.CHEST);
                    ItemMeta itemMeta = ownerHead.getItemMeta();
                    if (itemMeta == null) {
                        itemMeta = Bukkit.getItemFactory().getItemMeta(Material.CHEST);
                    }
                    itemMeta.setDisplayName(parts[1]);
                    ownerHead.setItemMeta(itemMeta);
                }
                else { // dropped and not picked up
                    ownerHead = new ItemStack(Material.STAINED_GLASS_PANE);
                    ItemMeta itemMeta = ownerHead.getItemMeta();
                    if (itemMeta == null) {
                        itemMeta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
                    }
                    itemMeta.setDisplayName(parts[1]);
                    ownerHead.setItemMeta(itemMeta);
                }
                inventory.setItem(slot, ownerHead);
                ItemStack trackedItemCopy = createTrackedItemCopy(lore, name);
                inventory.setItem(slot + 1, trackedItemCopy);

                slot += 9; // Move to the next row
            }
        }
        player.openInventory(inventory);
    }

    private static List<String> readTrackedDataFromFile() {
        List<String> trackedData = new ArrayList<>();
        File file = new File(ItemTracker.file_name);

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                trackedData.add(sc.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return trackedData;
    }

    private static ItemStack createOwnerHead(UUID ownerUUID) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);

        meta.setOwner(owner.getName());
        skull.setItemMeta(meta);

        return skull;
    }

    private static ItemStack createTrackedItemCopy(String lore, String name) {
        Material n = Material.valueOf(name);
        ItemStack trackedItem = new ItemStack(n);
        ItemMeta itemMeta = trackedItem.getItemMeta();
        itemMeta.setDisplayName(lore);
        trackedItem.setItemMeta(itemMeta);
        return trackedItem;
    }
}