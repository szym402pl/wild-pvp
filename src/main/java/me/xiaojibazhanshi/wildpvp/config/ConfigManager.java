package me.xiaojibazhanshi.wildpvp.config;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.records.FightStartRecord;
import me.xiaojibazhanshi.wildpvp.records.QueueButtonRecord;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final int guiSize;
    private final String guiTitle;

    private final ItemStack playerHeadFormat;

    private final int queueButtonSlot;
    private final QueueButtonRecord beforeJoin;
    private final QueueButtonRecord afterJoin;

    private final FightStartRecord fightStartRecord;

    @SuppressWarnings("unchecked")
    public ConfigManager() {
        guiSize = Config.GUI_SIZE.get(Integer.class);
        guiTitle = Config.GUI_TITLE.get(String.class);

        queueButtonSlot = Config.GUI_QUEUE_BUTTON_SLOT.get(Integer.class);

        beforeJoin = new QueueButtonRecord(Config.GUI_QUEUE_BUTTON_BEFORE_JOIN_ACTION.get(String.class),
                makeAnItemStack
                        (Config.GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_NAME.get(String.class),
                                Material.valueOf(Config.GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_MATERIAL.get(String.class)),
                                Config.GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_HT.get(Boolean.class),
                                (List<String>) Config.GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_LORE.get(List.class),
                                true));

        afterJoin = new QueueButtonRecord(Config.GUI_QUEUE_BUTTON_AFTER_JOIN_ACTION.get(String.class),
                makeAnItemStack
                        (Config.GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_NAME.get(String.class),
                                Material.valueOf(Config.GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_MATERIAL.get(String.class)),
                                Config.GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_HT.get(Boolean.class),
                                (List<String>) Config.GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_LORE.get(List.class),
                                true));

        playerHeadFormat = makeAnItemStack
                (Config.GUI_PLAYER_HEAD_NAME.get(String.class),
                        Material.PLAYER_HEAD,
                        Config.GUI_PLAYER_HEAD_HT.get(Boolean.class),
                        (List<String>) Config.GUI_PLAYER_HEAD_LORE.get(List.class),
                        true);

        fightStartRecord = new FightStartRecord(
                Config.FIGHT_START_ACTIONBAR.get(String.class),
                Config.FIGHT_START_SOUND.get(Boolean.class),
                Config.FIGHT_START_INVULNERABILITY_TIME.get(Integer.class)
        );
    }

    public ItemStack makeAnItemStack(String name, Material material, boolean hideTT, List<String> lore, boolean addData) {
        ItemStack item = new ItemStack(material);

        List<String> loreWReplacedColorCodes = new ArrayList<>(lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line)).toList());
        String nameWReplacedColorCodes = ChatColor.translateAlternateColorCodes('&', name);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(loreWReplacedColorCodes);
        itemMeta.setDisplayName(nameWReplacedColorCodes);
        itemMeta.setEnchantmentGlintOverride(true);

        if (hideTT)
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (addData) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(new NamespacedKey(WildPvP.getInstance(), "button"), PersistentDataType.STRING, "button");
        }

        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack getPlayerHeadFormat() {
        return playerHeadFormat;
    }

    public QueueButtonRecord getAQueueButton(String action) {
        return action.equalsIgnoreCase("join") ? beforeJoin : afterJoin;
    }

    public FightStartRecord getFightStartRecord() {
        return fightStartRecord;
    }

    public int getGuiSize() {
        return guiSize;
    }

    public QueueButtonRecord getAfterJoin() {
        return afterJoin;
    }

    public QueueButtonRecord getBeforeJoin() {
        return beforeJoin;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public int getQueueButtonSlot() {
        return queueButtonSlot;
    }
}
