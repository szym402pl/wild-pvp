package me.xiaojibazhanshi.wildpvp.config;

import me.xiaojibazhanshi.wildpvp.WildPvP;

public enum Config {

    FIGHT_START_ACTIONBAR("fight-start.actionbar"),
    FIGHT_START_SOUND("fight-start.sound"),
    FIGHT_START_INVULNERABILITY_TIME("fight-start.invulnerable-time-seconds"),

    GUI_SIZE("gui.size"),
    GUI_TITLE("gui.title"),

    GUI_PLAYER_HEAD_HT("gui.player-head.hide-tooltip"),
    GUI_PLAYER_HEAD_NAME("gui.player-head.name"),
    GUI_PLAYER_HEAD_LORE("gui.player-head.lore"),

    GUI_QUEUE_BUTTON_SLOT("gui.queue-button.slot"),

    GUI_QUEUE_BUTTON_BEFORE_JOIN_ACTION("gui.queue-button.before-queue-join.queue-action"),
    GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_NAME("gui.queue-button.before-queue-join.item.name"),
    GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_MATERIAL("gui.queue-button.before-queue-join.item.material"),
    GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_HT("gui.queue-button.before-queue-join.item.hide-tooltip"),
    GUI_QUEUE_BUTTON_BEFORE_JOIN_ITEM_LORE("gui.queue-button.before-queue-join.item.lore"),

    GUI_QUEUE_BUTTON_AFTER_JOIN_ACTION("gui.queue-button.after-queue-join.queue-action"),
    GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_NAME("gui.queue-button.after-queue-join.item.name"),
    GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_MATERIAL("gui.queue-button.after-queue-join.item.material"),
    GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_HT("gui.queue-button.after-queue-join.item.hide-tooltip"),
    GUI_QUEUE_BUTTON_AFTER_JOIN_ITEM_LORE("gui.queue-button.after-queue-join.item.lore");

    private final String path;

    Config(String path) {
        this.path = path;
    }

    public <T> T get(Class<T> clazz) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null for config enum: " + this.name());
        }
        WildPvP instance = WildPvP.getInstance();
        if (instance == null) {
            throw new IllegalStateException("WildPvP instance is not yet initialized.");
        }
        Object value = instance.getConfig().get(path);
        if (value == null) {
            throw new IllegalArgumentException("No configuration value found for path: " + path);
        }
        return clazz.cast(value);
    }

}
