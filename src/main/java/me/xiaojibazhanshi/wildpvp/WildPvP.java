package me.xiaojibazhanshi.wildpvp;

import me.xiaojibazhanshi.wildpvp.commands.PvPCommand;
import me.xiaojibazhanshi.wildpvp.config.ConfigManager;
import me.xiaojibazhanshi.wildpvp.listeners.ClickAndLeaveListener;
import me.xiaojibazhanshi.wildpvp.util.GUIFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WildPvP extends JavaPlugin {

    /*
        /pvp -> gui opens -> you click, you join the queue

        2 people max queues, 2 player heads show up in a gui

        you click on the enemy's head with left click, you tp to him,
        you click on the enemy's head with right click, you view his inv

        invulnerable for 3 sec after the teleport,
        will make an actionbar and an animation for the countdown

        must make a way to leave the queue
    */

    private static WildPvP instance;
    private ConfigManager configManager;
    private GUIFactory guiFactory;

    public static WildPvP getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        configManager = new ConfigManager();
        guiFactory = new GUIFactory();

        getCommand("pvp").setExecutor(new PvPCommand());
        Bukkit.getPluginManager().registerEvents(new ClickAndLeaveListener(), this);
    }

    @Override
    public void onDisable() {
        guiFactory.flushQueue();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GUIFactory getGuiFactory() {
        return guiFactory;
    }
}
