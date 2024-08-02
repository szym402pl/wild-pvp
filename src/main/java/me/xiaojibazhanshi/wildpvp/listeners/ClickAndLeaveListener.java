package me.xiaojibazhanshi.wildpvp.listeners;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.config.ConfigManager;
import me.xiaojibazhanshi.wildpvp.runnables.FightStartRunnable;
import me.xiaojibazhanshi.wildpvp.util.GUIFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;
import java.util.UUID;

public class ClickAndLeaveListener implements Listener {

    GUIFactory guiFactory = WildPvP.getInstance().getGuiFactory();
    ConfigManager configManager = WildPvP.getInstance().getConfigManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();

        if (event.getClickedInventory() != null &&
                (!guiFactory.hasPersistentData(event.getClickedInventory(), guiFactory.buttonKey)
                        && !guiFactory.hasPersistentData(event.getClickedInventory(), guiFactory.enemyInvKey)))
            return;

        if (currentItem == null || currentItem.getType() == Material.AIR)
            return;


        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        boolean inQueueAlready = GUIFactory.inQueue.containsKey(player.getUniqueId());

        Material buttonMaterial = configManager.getAQueueButton(inQueueAlready ? "leave" : "join").item().getType();

        if (currentItem.getType() == Material.PLAYER_HEAD) {
            boolean teleportIfTrueAndShowInvIfFalse = event.getClick().isLeftClick();

            Player enemy = (Player)
                    ((SkullMeta) Objects.requireNonNull(currentItem.getItemMeta())).getOwningPlayer();
            assert enemy != null;

            if (enemy.getUniqueId() == player.getUniqueId()) {
                if (teleportIfTrueAndShowInvIfFalse)
                    player.sendMessage(ChatColor.RED + "You can't fight yourself.");
                else
                    player.sendMessage(ChatColor.RED + "You can already see your own inventory.");

                player.closeInventory();
                return;
            }

            if (teleportIfTrueAndShowInvIfFalse) {
                enemy.closeInventory();
                player.closeInventory();

                player.teleport(enemy.getLocation());

                Bukkit.getScheduler().runTaskAsynchronously(WildPvP.getInstance(),
                        new FightStartRunnable(player, enemy, configManager.getFightStartRecord()));
            } else {
                player.openInventory(guiFactory.getEnemyInventory(enemy));
            }

        } else if (currentItem.getType() == Material.BARRIER) {

            player.closeInventory();
            player.openInventory(guiFactory.getGUI(player));

        } else {

            if (currentItem.getType() != buttonMaterial)
                return;

            if (inQueueAlready) {
                guiFactory.removeFromQueue(player);
                player.closeInventory();
                player.openInventory(guiFactory.getGUI(player));
            } else {
                guiFactory.addToQueue(player);
                player.closeInventory();
                player.openInventory(guiFactory.getGUI(player));
            }

            for (Player target : Bukkit.getOnlinePlayers()) {
                target.getOpenInventory();

                if (guiFactory.hasPersistentData(target.getOpenInventory().getTopInventory(), guiFactory.enemyInvKey)
                        || guiFactory.hasPersistentData(target.getOpenInventory().getTopInventory(), guiFactory.buttonKey)) {
                    target.closeInventory();
                    target.openInventory(guiFactory.getGUI(target));
                }
            }

        }

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        guiFactory.removeFromQueue(event.getPlayer());

        for (UUID uuid : GUIFactory.inQueue.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;

            player.closeInventory();
            player.openInventory(guiFactory.getGUI(player));
        }
    }
}
