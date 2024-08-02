package me.xiaojibazhanshi.wildpvp.util;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUIFactory {

    public static HashMap<UUID, ItemStack> inQueue = new HashMap<>();
    public final NamespacedKey buttonKey = new NamespacedKey(WildPvP.getInstance(), "button");
    public final NamespacedKey enemyInvKey = new NamespacedKey(WildPvP.getInstance(), "enemy-inventory");
    private final ConfigManager configManager;

    public GUIFactory() {
        this.configManager = WildPvP.getInstance().getConfigManager();
    }

    public ItemStack getPersistentDataPlaceholder(boolean button) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(button ? buttonKey : enemyInvKey,
                PersistentDataType.STRING, button ? buttonKey.getKey() : enemyInvKey.getKey());

        meta.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + ChatColor.BOLD + "Return to menu");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setEnchantmentGlintOverride(true);

        List<String> lore = new ArrayList<>(List.of(
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Click me to return",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "to the Wild PvP menu"));
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public Inventory getEnemyInventory(Player enemy) {
        Inventory inv = enemy.getInventory();
        Inventory copy = Bukkit.createInventory(null, 45, enemy.getName() + "'s inventory");
        copy.setContents(inv.getContents());

        copy.setItem(0, getPersistentDataPlaceholder(false));

        return copy;
    }

    public void addToQueue(Player player) {
        ItemStack playerHead = getAnUpdatedHead(configManager.getPlayerHeadFormat(), player);
        inQueue.put(player.getUniqueId(), playerHead);
    }

    public void removeFromQueue(Player player) {
        inQueue.remove(player.getUniqueId());
    }

    public void flushQueue() {
        inQueue.clear();
    }

    public boolean hasPersistentData(Inventory inventory, NamespacedKey namespacedKey) {
        for (ItemStack item : inventory.getContents()) {
            boolean itemHasBPD = item != null && item.getType() != Material.AIR && item.hasItemMeta()
                    && item.getItemMeta().getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING);

            if (itemHasBPD)
                return true;
        }

        return false;
    }

    public ItemStack getAnUpdatedHead(ItemStack headFormat, Player player) {
        ItemStack headCopy = headFormat.clone();
        SkullMeta skullMeta = (SkullMeta) headCopy.getItemMeta();
        assert skullMeta != null;

        skullMeta.setOwningPlayer(player);
        String updatedName = skullMeta.getDisplayName().replace("{player-name}", player.getName());
        skullMeta.setDisplayName(updatedName);

        int playersNearby = (int) player.getNearbyEntities(20, 20, 20)
                .stream().filter(entity -> entity instanceof Player).count();

        String playerHealth = String.format("%.2f", player.getHealth());

        List<String> updatedLore = skullMeta.getLore().stream().map
                (line -> line.replace("{player-hp}", playerHealth)
                        .replace("{players-nearby}", String.valueOf(playersNearby))).toList();

        skullMeta.setLore(updatedLore);
        headCopy.setItemMeta(skullMeta);

        return headCopy;
    }

    public Inventory getGUI(Player player) {
        int guiSize = configManager.getGuiSize();
        String guiTitle = ChatColor.translateAlternateColorCodes('&', configManager.getGuiTitle());

        Inventory baseGUI = Bukkit.createInventory(null, guiSize, guiTitle);

        ItemStack queueButton = inQueue.containsKey(player.getUniqueId())
                ? configManager.getAfterJoin().item() : configManager.getBeforeJoin().item();

        baseGUI.setItem(configManager.getQueueButtonSlot(), queueButton);

        if (inQueue.isEmpty()) {
            return baseGUI;
        }

        int count = 0;
        for (ItemStack item : inQueue.values()) {
            baseGUI.setItem(count, item);
            count++;
        }

        return baseGUI;
    }


}
