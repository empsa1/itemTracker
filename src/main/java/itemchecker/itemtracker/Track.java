package itemchecker.itemtracker;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Scanner;

public class Track implements CommandExecutor {

    // /track command : adds item in hand to the file to be tracked if has valid lore
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        /* /track command that when ran places the item in tracking if it's not already being tracked */
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            ItemTracker.communication_handler(player,"You must be holding an item with a unique lore in your main hand!",
                    1);
            return false;
        }
        String lore = item.getItemMeta().getLore().toString();
        String playerUID = player.getUniqueId().toString();
        String itemName = item.getType().toString();
        try {
            File file = new File(ItemTracker.file_name);
            File canonical = file.getCanonicalFile();
            if (!file.exists() || file.isDirectory()) {
                ItemTracker.communication_handler(player, "Something went wrong", 1);
                ItemTracker.communication_handler(null, "An error occurred while accessing " +
                        ItemTracker.file_name, 1);
                return false;
            }
            if (strings.length != 0 && strings[0].contains(":")) {
                ItemTracker.communication_handler(player, "You can't track an item with the character ':' in its lore!", 1);
                ItemTracker.communication_handler(null, "The player: " + player + " tried to track an item with an illegal character, possibly trying to crash the plugin!", 1);
                return false;
            }
            try (Scanner sc = new Scanner(canonical); FileWriter writer = new FileWriter(canonical, true)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(":");
                    if (parts.length < 4 || parts[2].equals(lore)) {
                        ItemTracker.communication_handler(player,
                                " The lore of the item you are holding is already being tracked careful to only having unique lore's in the items to track",
                                1);
                        return false;
                    }
                }

                writer.append("1:").append(playerUID).append(":").append(lore).append(":").append(itemName).append("\n");
            }
            ItemTracker.communication_handler(player,"Successfully added the item to the lore tracker.", 0);
            ItemTracker.communication_handler(null, "The item with lore: "
                    + lore + " is now being tracked because of: " + player, 0);
            return true;
        } catch (IOException e) {
            ItemTracker.communication_handler(player, "Something went wrong", 1);
            ItemTracker.communication_handler(null, "An error occurred in the track command function: onCommand()",
                    1);
            return false;
        }
    }
}