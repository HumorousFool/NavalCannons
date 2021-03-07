package io.github.humorousfool.navalcannons.cannon.task.movecraft;

import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.cannon.MovecraftCannon;
import io.github.humorousfool.navalcannons.cannon.task.normal.CannonPreFireTask;
import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.localisation.I18nSupport;
import net.countercraft.movecraft.craft.Craft;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MovecraftPreFireCheckedTask extends CannonPreFireTask
{
    private final MovecraftCannon cannon;
    private final Craft craft;
    private final Player player;

    public MovecraftPreFireCheckedTask(MovecraftCannon cannon, Craft c, Player player)
    {
        super(cannon.relativeLocation.add(c.getHitBox().getMidPoint()).toBukkit(c.getW()));
        this.cannon = cannon;
        craft = c;
        this.player = player;
    }

    @Override
    public void run()
    {

        if(player != null)
        {
            if(cannon.lastFire + Config.CannonReloadTime > System.currentTimeMillis())
                return;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + I18nSupport.getInternationalisedString("Fire")));
        }


        director = CannonManager.getInstance().directors.get(craft);

        cannon.lastFire = System.currentTimeMillis();
        super.run();
    }
}
