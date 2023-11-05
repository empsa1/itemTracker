package itemchecker.itemtracker;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Scanner;

public class FileModifier {

    //Changes owner in txt of the item being tracked
    public static void updateOwner(Player player, String coordinates, String lore) {
        if (player == null && coordinates == null) {
            ItemTracker.communication_handler(null, "There was an error in updateOwner() contact dev", 1);
            return;
        } else if (player != null && coordinates == null) {
            try {
                File file = new File(ItemTracker.file_name);
                if (file.exists() && !file.isDirectory()) {
                    File canonical = file.getCanonicalFile();
                    StringBuilder buffer = new StringBuilder();
                    try (Scanner sc = new Scanner(canonical)) {
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            if (line.contains(lore)) {
                                String[] parts = line.split(":", 4);
                                line = parts[0] + ":" + player.getUniqueId().toString() + ":" + parts[2] + ":" + parts[3];
                            }
                            buffer.append(line).append('\n');
                        }
                        FileWriter writer = new FileWriter(canonical);
                        writer.write(buffer.toString());
                        writer.flush();
                        writer.close();
                    }
                }
            } catch (IOException e) {
                ItemTracker.communication_handler(null, "An error occurred in lore command function: updateOwner()", 1);
            }
        } else if (player == null && coordinates != null) {
            try {
                File file = new File(ItemTracker.file_name);
                if (file.exists() && !file.isDirectory()) {
                    File canonical = file.getCanonicalFile();
                    StringBuilder buffer = new StringBuilder();
                    try (Scanner sc = new Scanner(canonical)) {
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            if (line.contains(lore)) {
                                String[] parts = line.split(":", 4);
                                line = parts[0] + ":" + coordinates + ":" + parts[2] + ":" + parts[3];
                            }
                            buffer.append(line).append('\n');
                        }
                        FileWriter writer = new FileWriter(canonical);
                        writer.write(buffer.toString());
                        writer.flush();
                        writer.close();
                    }
                }
            } catch (IOException e) {
                ItemTracker.communication_handler(null, "An error occurred in lore command function: updateOwner()", 1);
            }
        }
        else
            ItemTracker.communication_handler(null, "An error occurred in lore command function: updateOwner()", 1);
    }

    //Changes lore in the txt of the item being tracked
    public static void updateLore(ItemStack item, String newLore) {
        try {
            String lore = item.getItemMeta().getLore().toString();
            File file = new File(ItemTracker.file_name);
            if (file.exists() && !file.isDirectory()) {
                File canonical = file.getCanonicalFile();
                StringBuilder buffer = new StringBuilder();
                try (Scanner sc = new Scanner(canonical)) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        if (line.contains(lore)) {
                            if (newLore != null) {
                                String[] parts = line.split(":", 4);
                                line = parts[0] + ":" + parts[1] + ":" + "[" + newLore + "]" + ":" + parts[3];
                            } else {
                                continue;
                            }
                        }
                        buffer.append(line).append('\n');
                    }
                    FileWriter writer = new FileWriter(canonical);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                }
            }
        } catch (IOException e) {
            ItemTracker.communication_handler(null, "An error occurred in lore command function: updateLore()", 1);
        }
    }

    //Changes ownertype of the item being tracked in the txt
    public static void updateOwnerType(String targetLore, String newOwnertype) {
        File file = new File(ItemTracker.file_name);
        try {
            if (file.exists() && !file.isDirectory()) {
                File canonical = file.getCanonicalFile();
                StringBuilder buffer = new StringBuilder();
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(":");
                    if (parts.length >= 4) {
                        if (parts[2].equals(targetLore)) {
                            if (newOwnertype != null) {
                                line = newOwnertype + ":" + parts[1] + ":" + parts[2] + ":" + parts[3];
                            }
                        }
                    }
                    buffer.append(line).append('\n');
                }
                FileWriter writer = new FileWriter(canonical);
                writer.write(buffer.toString());
                writer.flush();
                sc.close();
                writer.close();
            }
        } catch (IOException e) {
            ItemTracker.communication_handler(null, "An error occurred in lore command function: updateOwnertypeForLore()",
                    1);
        }
    }
}
