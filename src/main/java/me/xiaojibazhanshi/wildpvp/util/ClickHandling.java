package me.xiaojibazhanshi.wildpvp.util;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.config.ConfigManager;
import me.xiaojibazhanshi.wildpvp.runnables.FightStartRunnable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class ClickHandling {

    private final GUIFactory guiFactory;
    private final ConfigManager configManager;

    public ClickHandling() {
        this.guiFactory = WildPvP.getInstance().getGuiFactory();
        this.configManager = WildPvP.getInstance().getConfigManager();
    }

    public void handlePlayerHeadClick(InventoryClickEvent event, ItemStack currentItem, Player player) {
        boolean teleportIfTrueAndShowInvIfFalse = event.getClick().isLeftClick();
        Player enemy = (Player) ((SkullMeta) Objects.requireNonNull(currentItem.getItemMeta())).getOwningPlayer();

        if (enemy == null) {
            return;
        }

        if (enemy.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + (teleportIfTrueAndShowInvIfFalse ? "You can't fight yourself." : "You can already see your own inventory."));
            player.closeInventory();
            return;
        }

        if (teleportIfTrueAndShowInvIfFalse) {
            enemy.closeInventory();
            player.closeInventory();
            player.teleport(enemy.getLocation());
            Bukkit.getScheduler().runTaskAsynchronously(WildPvP.getInstance(), new FightStartRunnable(player, enemy, configManager.getFightStartRecord()));
        } else {
            player.openInventory(guiFactory.getEnemyInventory(enemy));
        }
    }

    public void handleBarrierClick(Player player) {
        player.closeInventory();
        player.openInventory(guiFactory.getGUI(player));
    }

    public void handleDefaultClick(Player player, ItemStack currentItem, Material buttonMaterial, boolean inQueueAlready) {
        if (currentItem.getType() != buttonMaterial) {
            return;
        }

        if (inQueueAlready) {
            guiFactory.removeFromQueue(player);
        } else {
            guiFactory.addToQueue(player);
        }

        player.closeInventory();
        player.openInventory(guiFactory.getGUI(player));

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (guiFactory.hasPersistentData(target.getOpenInventory().getTopInventory(), guiFactory.enemyInvKey) ||
                    guiFactory.hasPersistentData(target.getOpenInventory().getTopInventory(), guiFactory.buttonKey)) {
                target.closeInventory();
                target.openInventory(guiFactory.getGUI(target));
            }
        }
    }
}