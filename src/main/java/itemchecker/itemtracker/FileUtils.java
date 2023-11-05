package itemchecker.itemtracker;

import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Scanner;

public class FileUtils {

    public static void print_file() {
        File file = new File(ItemTracker.file_name);
        try {
            if (file.exists() && !file.isDirectory()) {
                File canonical = file.getCanonicalFile();
                BufferedReader reader = new BufferedReader(new FileReader(canonical));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("file line: " + line);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //Checks if item is being tracked through lore
    public static boolean trackLore(String lore) {
        File file = new File(ItemTracker.file_name);
        if (file.exists() && !file.isDirectory()) {
            File canonical = null;
            try {
                canonical = file.getCanonicalFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (Scanner sc = new Scanner(canonical)) {
                while (sc.hasNextLine()) {
                    String[] parts = sc.nextLine().split(":");
                    if (parts.length >= 4 && parts[2].equals(lore)) {
                        return true;
                    }
                }
            } catch (FileNotFoundException e) {
                ItemTracker.communication_handler(null, "An error occurred while reading " + ItemTracker.file_name, 1);
            }
        }
        return false;
    }

    //Checks if item is being tracked through item
    public static boolean isItemBeingTracked(ItemStack item) {
        try {
            if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                String lore = item.getItemMeta().getLore().toString();
                File file = new File(ItemTracker.file_name);
                if (file.exists() && !file.isDirectory()) {
                    File canonical = file.getCanonicalFile();
                    try (Scanner sc = new Scanner(canonical)) {
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            String[] temp = line.split(":");
                            if (temp.length >= 4 && temp[2] != null && temp[2].equals(lore)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            ItemTracker.communication_handler(null, "An error occurred in lore command in function: isItemBeingTracked()",
                    1);
        }
        return false;
    }
}
