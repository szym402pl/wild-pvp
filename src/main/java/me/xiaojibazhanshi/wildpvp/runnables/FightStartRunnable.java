package me.xiaojibazhanshi.wildpvp.runnables;

import me.xiaojibazhanshi.wildpvp.WildPvP;
import me.xiaojibazhanshi.wildpvp.records.FightStartRecord;
import me.xiaojibazhanshi.wildpvp.util.GUIFactory;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FightStartRunnable implements Runnable {

    private final GUIFactory guiFactory = WildPvP.getInstance().getGuiFactory();
    private final Player challenger;
    private final Player enemy;
    private final FightStartRecord record;
    private int frame = 0;

    public FightStartRunnable(Player challenger, Player enemy, FightStartRecord record) {
        this.challenger = challenger;
        this.enemy = enemy;
        this.record = record;
    }

    @Override
    public void run() {
        if (!(enemy.isInvulnerable() || challenger.isInvulnerable())) {
            enemy.setInvulnerable(true);
            challenger.setInvulnerable(true);
        }

        String actionbarText = ChatColor.translateAlternateColorCodes('&', record.actionbarText());
        int maxFrames = record.invulnerabilityTime();
        Sound sound = record.sound() ? Sound.ENTITY_PLAYER_LEVELUP : null;

        sendActionbar(enemy, maxFrames, frame, actionbarText);
        sendActionbar(challenger, maxFrames, frame, actionbarText);

        if (sound != null) {
            challenger.playSound(challenger, sound, 1, 1);
            enemy.playSound(enemy, sound, 1, 1);
        }

        frame++;
        if (frame > maxFrames) {
            enemy.setInvulnerable(false);
            challenger.setInvulnerable(false);

            guiFactory.removeFromQueue(challenger);
            guiFactory.removeFromQueue(enemy);

            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(WildPvP.getInstance(), this, 20);
    }

    private void sendActionbar(Player player, int maxFrames, int frame, String text) {
        int countdown = maxFrames - frame;
        String updatedText = text.replace("{count-down}", String.valueOf(countdown));

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(updatedText));
    }
}
