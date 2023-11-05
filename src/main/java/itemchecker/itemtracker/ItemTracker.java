package itemchecker.itemtracker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.tr7zw.nbtapi.NBTItem;
import static org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY;
import static org.bukkit.event.inventory.InventoryAction.PLACE_ALL;

public final class ItemTracker extends JavaPlugin implements Listener {
    private Plugin instance;
    public static String plugin_version = "[itemTracker1.0]: ";
    public static String file_name = "itemTrackerConfig.txt";

    @Override //When server boots
    public void onEnable() {
        instance = this;
        //take hold of the commands
        this.getCommand("track").setExecutor(new Track());
        this.getCommand("lore").setExecutor(new Lore());
        getCommand("tracker").setExecutor(new Tracker(this));

        Bukkit.getPluginManager().registerEvents(this, this);
        communication_handler(null, "was enabled successfully", 0);
    }
    public Plugin getInstance() {
        return instance;
    }

    //function to handle the prints
    public static void communication_handler(Player target, String message, int errorCode)
    {
        MessageHandler msg = new MessageHandler(message, errorCode);
        msg.msg(target);
    }

    @EventHandler //Container v Player actions
    public void onContainerClickAction(InventoryClickEvent event) {
        System.out.println("onContainerClickAction()");
            if (event.getWhoClicked() instanceof Player) {
                    if (event.getAction().toString().equals(MOVE_TO_OTHER_INVENTORY.toString())) {
                        EventUtils.move(event,((Player) event.getWhoClicked()), event.getCurrentItem());
                    } else if (event.getAction().toString().equals(PLACE_ALL.toString())
                            && event.getWhoClicked().getItemOnCursor().getMaxStackSize() == 1) {
                        EventUtils.place_all(event,((Player) event.getWhoClicked()), event.getWhoClicked().getItemOnCursor());
                    }
                }
    }

    @EventHandler //When a player picks a tracked item
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && event.getItem().getItemStack().getMaxStackSize() == 1) {
            EventUtils.playerPickupTracker(((Player) event.getEntity()).getPlayer(), event.getItem().getItemStack());
        }
    }

    @EventHandler //When a player drops a tracked item
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getMaxStackSize() == 1) {
            EventUtils.playerDropTracker(event, event.getPlayer(), event.getItemDrop().getItemStack());
        }
    }

    @Override //When server shuts down
    public void onDisable() {
        communication_handler(null, "was disabled successfully", 0);
    }
}
