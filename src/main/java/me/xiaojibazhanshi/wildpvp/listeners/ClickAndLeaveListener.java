package me.xiaojibazhanshi.wildpvp.listeners;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.config.ConfigManager;
import me.xiaojibazhanshi.wildpvp.util.ClickHandling;
import me.xiaojibazhanshi.wildpvp.util.GUIFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ClickAndLeaveListener implements Listener {

    private final GUIFactory guiFactory = WildPvP.getInstance().getGuiFactory();
    private final ConfigManager configManager = WildPvP.getInstance().getConfigManager();
    private final ClickHandling clickHandling = new ClickHandling();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        if (!guiFactory.hasPersistentData(event.getClickedInventory(), guiFactory.buttonKey) &&
                !guiFactory.hasPersistentData(event.getClickedInventory(), guiFactory.enemyInvKey)) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        boolean inQueueAlready = GUIFactory.inQueue.containsKey(player.getUniqueId());
        Material buttonMaterial = configManager.getAQueueButton(inQueueAlready ? "leave" : "join").item().getType();

        switch (currentItem.getType()) {
            case PLAYER_HEAD:
                clickHandling.handlePlayerHeadClick(event, currentItem, player);
                break;
            case BARRIER:
                clickHandling.handleBarrierClick(player);
                break;
            default:
                clickHandling.handleDefaultClick(player, currentItem, buttonMaterial, inQueueAlready);
                break;
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        guiFactory.removeFromQueue(event.getPlayer());

        for (UUID uuid : GUIFactory.inQueue.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
                player.openInventory(guiFactory.getGUI(player));
            }
        }
    }
}
