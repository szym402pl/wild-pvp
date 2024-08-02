package me.xiaojibazhanshi.wildpvp.commands;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.util.GUIFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPCommand implements CommandExecutor {

    private final GUIFactory guiFactory = WildPvP.getInstance().getGuiFactory();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().info("This command can only be executed by a player!");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Usage: " + command.getUsage());
            return true;
        }

        player.openInventory(guiFactory.getGUI(player));

        return true;
    }
}
