package io.github.humorousfool.navalcannons.listener;

import io.github.humorousfool.navalcannons.config.Config;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.utils.MathUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class CombatListener implements Listener
{
    @EventHandler
    public void onIgnite(BlockIgniteEvent event)
    {
        if(!Config.PreventShipIgnite)
            return;

        Craft c = null;
        for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(event.getBlock().getWorld())) {
            if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(event.getBlock().getLocation())) &&
                    CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                c = tcraft;
                break;
            }
        }

        if(c != null)
        {
            event.setCancelled(true);
        }
    }
}
