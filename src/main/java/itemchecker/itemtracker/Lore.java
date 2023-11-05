package itemchecker.itemtracker;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.List;

public class Lore implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        /* /lore command*/
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (strings.length != 0) {
                if (strings[0].contains(":")) {
                    ItemTracker.communication_handler(player, "The lore cant have the ':' character!\nPlease try again", 1);
                    return false;
                }
                if (FileUtils.trackLore("[" + strings[0] + "]")) {
                    ItemTracker.communication_handler(null, "The player: " + player + "tried to change the lore of an item to a lore that already is being tracked!", 1);
                    ItemTracker.communication_handler(player, "There is already an item being tracked with that lore!", 1);
                    return false;
                }
            }
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            String loreString = lore != null ? lore.toString() : null;
            List<String> newLore = Arrays.asList(strings);
            if (FileUtils.isItemBeingTracked(item)) {
                if (strings.length != 0) {
                    FileModifier.updateLore(item, strings[0]);
                } else {
                    FileModifier.updateLore(item, null);
                }
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
            ItemTracker.communication_handler(player, "Item lore was successfully changed to: " + item.getItemMeta().getLore(), 0);
            ItemTracker.communication_handler(null, "The item " + item.getType().toString() + " with lore: "
                    + lore + " was changed to " + item.getItemMeta().getLore() + " because of: " + player, 0);
            return true;
        }
        ItemTracker.communication_handler(null, "Fatal error the entity who sent this command is not a player!", 1);
        return false;
    }
}