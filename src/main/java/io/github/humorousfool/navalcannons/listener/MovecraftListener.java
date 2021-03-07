package io.github.humorousfool.navalcannons.listener;

import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.cannon.MovecraftCannon;
import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.localisation.I18nSupport;
import io.github.humorousfool.navalcannons.util.BlockUtil;
import io.github.humorousfool.navalcannons.util.ChatUtil;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.events.CraftRotateEvent;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.countercraft.movecraft.utils.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.LinkedList;

public class MovecraftListener implements Listener
{
    @EventHandler
    public void onDetect(CraftDetectEvent event)
    {
        LinkedList<MovecraftCannon> cannons = new LinkedList<>();
        int totalPower = 0;

        for(MovecraftLocation loc : event.getCraft().getHitBox())
        {
            Block block = event.getCraft().getW().getBlockAt(loc.getX(), loc.getY(), loc.getZ());

            if(block.getType() != Material.WALL_SIGN)
                continue;

            Sign blockSign = (Sign) block.getState();
            if(!blockSign.getLine(0).equals(ChatColor.DARK_RED + "[Cannon]"))
                continue;

            org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) block.getState().getData();

            MovecraftLocation relativeLocation = loc.subtract(event.getCraft().getHitBox().getMidPoint());
            int power = ChatUtil.getLastDigit(blockSign.getLine(1));
            totalPower += power;
            int barrelLength = BlockUtil.getCannonLength(block);
            if(barrelLength == 0)
                continue;
            Vector direction = BlockUtil.blockFaceToVector(materialSign.getFacing().getOppositeFace());
            String identifier = blockSign.getLine(3);

            MovecraftCannon cannon = new MovecraftCannon(relativeLocation, power, barrelLength, direction, identifier);
            cannons.add(cannon);
        }

        if(cannons.size() == 0)
            return;

        String typeName = event.getCraft().getType().getCraftName();

        if(!Config.MinBlocksPerCannon.containsKey(typeName))
        {
            event.setCancelled(true);
            event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Cannons Not Allowed"));
            return;
        }

        if(Config.MaxCannons.containsKey(typeName) && Config.MaxCannons.get(typeName) < cannons.size())
        {
            event.setCancelled(true);
            event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Too Many Cannons"));
            return;
        }

        if(Config.MinBlocksPerCannon.get(typeName) > event.getCraft().getOrigBlockCount() / cannons.size())
        {
            event.setCancelled(true);
            event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Too Many Cannons Per Block"));
            return;
        }

        if(Config.MinBlocksPerPower.containsKey(typeName) && Config.MinBlocksPerPower.get(typeName) > event.getCraft().getOrigBlockCount() / totalPower)
        {
            event.setCancelled(true);
            event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Too Much Power Per Block"));
            return;
        }

        CannonManager.getInstance().updateCraftCannons(event.getCraft(), cannons);
    }

    @EventHandler
    public void onRotate(CraftRotateEvent event)
    {
        if(event.isCancelled() || !CannonManager.getInstance().craftCannons.containsKey(event.getCraft()))
            return;

        if(CannonManager.getInstance().pendingFires.containsKey(event.getCraft()))
        {
            event.setCancelled(true);
            event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Cant Turn After Firing"));
            return;
        }

        for(MovecraftCannon c : CannonManager.getInstance().craftCannons.get(event.getCraft()))
        {
            if(c.lastFire + Config.CannonCraftPauseTime > System.currentTimeMillis())
            {
                event.setCancelled(true);
                event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Cant Turn After Firing"));
                return;
            }
        }

        for(MovecraftCannon cannon : CannonManager.getInstance().craftCannons.get(event.getCraft()))
        {
            MovecraftLocation oldLocation = cannon.relativeLocation;
            MovecraftLocation newLocation = MathUtils.rotateVec(event.getRotation(), oldLocation).add(event.getOriginPoint());
            cannon.relativeLocation = newLocation.subtract(event.getNewHitBox().getMidPoint());
        }
    }

    @EventHandler
    public void onTranslate(CraftTranslateEvent event)
    {
        if(event.isCancelled() || !CannonManager.getInstance().craftCannons.containsKey(event.getCraft()))
            return;

        if(CannonManager.getInstance().pendingFires.containsKey(event.getCraft()))
        {
            event.setCancelled(true);
            event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Cant Move After Firing"));
        }

        for(MovecraftCannon c : CannonManager.getInstance().craftCannons.get(event.getCraft()))
        {
            if(c.lastFire + Config.CannonCraftPauseTime > System.currentTimeMillis())
            {
                event.setCancelled(true);
                event.setFailMessage(I18nSupport.getInternationalisedString("Craft - Cant Move After Firing"));
                return;
            }
        }
    }

    @EventHandler
    public void onRelease(CraftReleaseEvent event)
    {
        CannonManager.getInstance().removeCraft(event.getCraft());
        CannonManager.getInstance().removeDirector(event.getCraft());
    }
}
