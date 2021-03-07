package io.github.humorousfool.navalcannons.cannon;

import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;

public class CannonProjectile
{
    private final Location location;
    private final Location startLocation;
    private final Vector velocity;
    private final double rawVelocity;

    public double power;
    public long fireTime;
    public boolean hasImpacted = false;
    public double distanceSinceImpact = 0;
    public boolean expired = false;

    public CannonProjectile(Location loc, int power, Vector direction)
    {
        location = loc.add(0.5D, 0D, 0.5D).clone();
        startLocation = loc.clone().add(0.5D, 0D, 0.5D);
        this.power = power;
        rawVelocity = this.power * Config.CannonVelocityMultiplier;
        velocity = direction.multiply(rawVelocity);
        fireTime = System.currentTimeMillis();
    }

    public void process()
    {
        //Add 1 to the old location until it reaches the new one, checking along the way
        Location newLocation = location.clone();
        newLocation.add(velocity);
        Location tempLocation = location.clone();
        Vector tempVelocity = velocity.clone().divide(new Vector(10, 10, 10));
        for(double i = 0; i < 10; i++)
        {
            if(expired)
                return;
            if(hasImpacted)
            {
                if(distanceSinceImpact >= power * Config.CannonPenetrationModifier)
                {
                    expired = true;
                    return;
                }

                distanceSinceImpact += rawVelocity / 10;
            }
            tempLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, tempLocation, 30, 0.1, 0.1, 0.1, 0.01);

            if(!BlockUtil.isChunkLoaded(tempLocation))
            {
                expired = true;
                return;
            }

            Block block = new Location(tempLocation.getWorld(), tempLocation.getBlockX(), tempLocation.getBlockY(), tempLocation.getBlockZ()).getBlock();

            if(!Arrays.asList(BlockUtil.UNBREAKABLE_BLOCKS).contains(block.getType()))
            {
                if(startLocation.distance(tempLocation) < Config.CannonIgnoreDistance - 1)
                {
                    tempLocation.add(velocity.clone().normalize());
                    continue;
                }
                if (!hasImpacted)
                {
                    hasImpacted = true;
                    block.getWorld().playSound(tempLocation, Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.BLOCKS, 0.5f, 1f);
                }

                BlockUtil.playBlockBreakSound(block.getType(), block.getLocation());
                CannonManager.getInstance().submitBlockDestroy(block.getLocation());
            }
            else if(block.getType().isSolid())
            {
                block.getWorld().playSound(tempLocation, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1f, 1f);
                block.getWorld().playSound(tempLocation, Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.5f, 1.5f);
                expired = true;
                return;
            }

            Collection<Entity> nearbyEntities = tempLocation.getWorld().getNearbyEntities(tempLocation, 0.5D, 0.5D, 0.5D);
            if(nearbyEntities.size() > 0)
            {
                for(Entity e : nearbyEntities)
                {
                    if(e.isValid() && e instanceof Damageable)
                    {
                        CannonManager.getInstance().submitEntityDamage((Damageable) e);
                    }
                }
            }

            tempVelocity.setY(tempVelocity.getY() + (tempVelocity.getY() / 10));
            tempLocation.add(tempVelocity);
        }
        velocity.setY(velocity.getY() - (Config.CannonProjectileGravity / 20));
        location.add(velocity);
    }
}
