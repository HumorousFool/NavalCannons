package io.github.humorousfool.navalcannons.cannon.task.movecraft;

import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.cannon.MovecraftCannon;
import io.github.humorousfool.navalcannons.cannon.task.normal.CannonPreFireTask;
import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.localisation.I18nSupport;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MovecraftPreFireTask extends CannonPreFireTask
{
    private final Location signLocation;
    private final Craft craft;
    private final Player player;

    //Fire Manually
    public MovecraftPreFireTask(Location signLocation, Craft craft, Player player)
    {
        super(signLocation);
        this.signLocation = signLocation;
        this.craft = craft;
        this.player = player;
    }

    @Override
    public void run() {
        MovecraftCannon cannon = null;
        for (MovecraftCannon c : CannonManager.getInstance().craftCannons.get(craft)) {
            if (craft.getHitBox().getMidPoint().add(c.relativeLocation).equals(MathUtils.bukkit2MovecraftLoc(signLocation))) {
                cannon = c;
            }
        }

        if (cannon == null)
            return;

        if (cannon.lastFire + Config.CannonReloadTime > System.currentTimeMillis() && player != null)
        {
            if (craft.getNotificationPlayer() != player && !cannon.speedLoaded)
            {
                cannon.lastFire -= Config.CannonReloadShorten;
                cannon.speedLoaded = true;
                long remaining = ((cannon.lastFire + Config.CannonReloadTime) - System.currentTimeMillis()) / 1000;

                player.sendMessage(ChatColor.GRAY + String.format(I18nSupport.getInternationalisedString("Cannon - Reload Shorten"), ChatColor.RED + "" + remaining + ChatColor.GRAY));
                return;
            }

            long remaining = ((cannon.lastFire + Config.CannonReloadTime) - System.currentTimeMillis()) / 1000;
            player.sendMessage(ChatColor.GRAY + String.format(I18nSupport.getInternationalisedString("Cannon - Reloading"), ChatColor.RED + "" + remaining + ChatColor.GRAY));
            return;
        }

        director = CannonManager.getInstance().directors.get(craft);

        cannon.lastFire = System.currentTimeMillis();
        super.run();
    }
}
