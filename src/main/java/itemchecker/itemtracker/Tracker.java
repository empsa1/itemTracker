package itemchecker.itemtracker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tracker implements CommandExecutor {

    private final ItemTracker plugin;

    public Tracker(ItemTracker plugin) {
        this.plugin = plugin;
    }

    // /tracker command : creates a GUI with the heads of the owners of the places where it was last seen
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tracker")) {
            if (sender instanceof Player) {
                if (args.length >= 1) {
                    try {
                        int page = Integer.parseInt(args[0]);
                        TrackerGUI.open((Player) sender, page);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid page number. Please use a valid number.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /tracker <page>");
                }
            } else {
                sender.sendMessage("Only players can use this command.");
            }
        }
        return true;
    }
}
