package itemchecker.itemtracker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EventUtils {

    //Handles cases where the event meant a change in ownership
    public static boolean changed_inv(InventoryClickEvent event, int slotsize, Player player, ItemStack item) {
        try {
            String lore = item.getItemMeta().getLore().toString();
            File file = new File(ItemTracker.file_name);
            File canonical = file.getCanonicalFile();
            StringBuilder buffer = new StringBuilder();
            boolean result = false;
            if (file.exists() && !file.isDirectory()) {
                try (Scanner sc = new Scanner(canonical); FileWriter writer = new FileWriter(canonical)) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] parts = line.split(":");
                        if (parts.length >= 4 && parts[1].equals(lore)) {
                            if (event.getRawSlot() >= slotsize && !player.getUniqueId().toString().equals(parts[0])) {
                                buffer.append(player.getUniqueId()).append(':').append(parts[1]).append('\n');
                                result = true;
                            } else if (event.getRawSlot() < slotsize && !parts[0].isEmpty()) {
                                buffer.append(':').append(parts[1]).append('\n');
                                result = true;
                            }
                        } else {
                            buffer.append(line).append('\n');
                        }
                    }
                    writer.write(buffer.toString());
                    writer.flush();
                }
            }
            return result;
        } catch (IOException e) {
            ItemTracker.communication_handler(player, "An error has occurred please contact the devs", 1);
            return false;
        }
    }
    public static void place_all(InventoryClickEvent event, Player player, ItemStack item) {
        int slotsize = 0;
        if (item == null || item.getType() == Material.AIR)
            return ;
        if (item.getItemMeta().hasLore()) {
            if (FileUtils.trackLore(item.getItemMeta().getLore().toString())) {
                slotsize = event.getView().getTopInventory().getSize() + event.getView().getBottomInventory().getSize();
                slotsize -= player.getInventory().getSize() + 1;
                if (event.getRawSlot() >= slotsize && changed_inv(event, slotsize, player, item)) {
                    ItemTracker.communication_handler(player,"Placed in player inventory", 0);
                } else if (event.getRawSlot() < slotsize && changed_inv(event, slotsize, player, item)) {
                    ItemTracker.communication_handler(player,"Placed in a container", 0);
                } else {
                    ItemTracker.communication_handler(player,"Something went wrong", 1);
                }
            }
        }
    }
    public static void playerPickupTracker(Player player, ItemStack item) {
        if (item.getItemMeta().hasLore()) {
            if (FileUtils.trackLore(item.getItemMeta().getLore().toString())) {
                ItemTracker.communication_handler(null, player.getDisplayName() + " now has picked up the " +
                        item.getItemMeta().getLore(), 0);
                FileModifier.updateOwnerType(item.getItemMeta().getLore().toString(), "1");
                FileModifier.updateOwner(player, null, item.getItemMeta().getLore().toString());
            }
        }
    }
    public static void playerDropTracker(PlayerDropItemEvent event, Player player, ItemStack item) {
        if (item.getItemMeta().hasLore()) {
            if (FileUtils.trackLore(item.getItemMeta().getLore().toString())) {
                ItemTracker.communication_handler(null, player.getDisplayName() + " has dropped the " +
                        item.getItemMeta().getLore(), 0);
                FileModifier.updateOwnerType(item.getItemMeta().getLore().toString(), "0");
                Location loc = player.getLocation();
                double x = Math.round(loc.getX() * 10.0) / 10.0; // Round to one decimal place
                double y = Math.round(loc.getY() * 10.0) / 10.0; // Round to one decimal place
                double z = Math.round(loc.getZ() * 10.0) / 10.0; // Round to one decimal place
                String toSend = "x=" + x + "," + "y=" + y + "," + "z=" + z;
                FileModifier.updateOwner(null, toSend, item.getItemMeta().getLore().toString());
            }
        }
    }
    public static void move(InventoryClickEvent event, Player player, ItemStack item) {
        String outcome = null;
        if (item == null || item.getType() == Material.AIR)
            return ;
        if (item.getItemMeta().hasLore()) {
            if (FileUtils.trackLore(item.getItemMeta().getLore().toString())) {
                if (event.getClickedInventory().getType() == InventoryType.CHEST
                        && player.getInventory().firstEmpty() != -1) {
                    outcome = event.getWhoClicked().getName().toString() + " has taken the " + item.getItemMeta().getLore()
                            + " from a container: "  + event.getInventory().getLocation();
                    FileModifier.updateOwnerType(item.getItemMeta().getLore().toString(), "1");
                    FileModifier.updateOwner(player, null, item.getItemMeta().getLore().toString());
                }else if (event.getClickedInventory().getType() == InventoryType.PLAYER
                        && event.getInventory().firstEmpty() != -1) {
                    outcome = event.getWhoClicked().getName().toString() + " has placed the " + item.getItemMeta().getLore() +
                            " in a container" + event.getInventory().getLocation();
                    FileModifier.updateOwnerType(item.getItemMeta().getLore().toString(), "2");
                    String[] parts = event.getInventory().getLocation().toString().split(",");
                    String local = parts[1] + "," + parts[2] + "," + parts[3];
                    FileModifier.updateOwner(null, local , item.getItemMeta().getLore().toString());
                }
            }
            if (outcome != null) {
                ItemTracker.communication_handler(null, outcome, 0);
            }
        }
    }
}
