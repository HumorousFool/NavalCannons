package io.github.humorousfool.navalcannons.cannon.task.normal;

import io.github.humorousfool.navalcannons.NavalCannons;
import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.util.BlockUtil;
import io.github.humorousfool.navalcannons.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class CannonPreFireTask extends BukkitRunnable
{
    protected final Location signLocation;
    protected Player director;

    public CannonPreFireTask(Location signLocation)
    {
        this.signLocation = signLocation;
    }

    @Override
    public void run()
    {
        if(signLocation.getBlock().getType() != Material.WALL_SIGN)
            return;

        if(CannonManager.getInstance().cannonCooldowns.containsKey(signLocation))
        {
            if(CannonManager.getInstance().cannonCooldowns.get(signLocation) + Config.CannonReloadTime > System.currentTimeMillis())
            {
                return;
            }

            CannonManager.getInstance().cannonCooldowns.remove(signLocation);
        }

        //Get power and length
        Sign blockSign = (Sign) signLocation.getBlock().getState();
        int power = ChatUtil.getLastDigit(blockSign.getLine(1));
        int presetLength = ChatUtil.getLastDigit(blockSign.getLine(2));

        //Get facing
        org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) signLocation.getBlock().getState().getData();
        Vector facingVector = BlockUtil.blockFaceToVector(materialSign.getFacing().getOppositeFace());

        //Get current length
        int actualLength = BlockUtil.getCannonLength(materialSign, signLocation);

        //Stop if broken
        if(actualLength == 0)
            return;

        //Add cannon director vector and low accuracy if broken
        Vector finalVector = facingVector.clone();
        if(actualLength < presetLength)
        {
            Random random = new Random();
            finalVector.setY(finalVector.getY() + ((random.nextDouble() - 0.5) / 4));
            if(finalVector.getX() == 0)
                finalVector.setX(finalVector.getX() + ((random.nextDouble() - 0.5) / 4));
            else if(finalVector.getZ() == 0)
                finalVector.setZ(finalVector.getZ() + ((random.nextDouble() - 0.5) / 4));
        }
        else if (actualLength > presetLength)
        {
            actualLength = presetLength;
        }

        //Task system needs to be redone for this to work
        //CannonManager.getInstance().cannonCooldowns.put(signLocation, System.currentTimeMillis());

        //Get end of barrel
        signLocation.getWorld().playSound(signLocation, Sound.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 0.5f, 1f);
        Location spawnLocation = signLocation.add(facingVector.multiply(actualLength - 1));
        new CannonFireTask(spawnLocation, power, finalVector, director).runTaskLaterAsynchronously(NavalCannons.getInstance(), Config.CannonFuse);
    }
}
