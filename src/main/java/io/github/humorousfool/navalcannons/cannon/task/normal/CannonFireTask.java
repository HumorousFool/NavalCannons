package io.github.humorousfool.navalcannons.cannon.task.normal;

import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.cannon.CannonProjectile;
import io.github.humorousfool.navalcannons.config.Config;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CannonFireTask extends BukkitRunnable
{
    private final Location spawnLocation;
    private final Vector velocity;
    private final int power;
    private final Player director;

    public CannonFireTask(Location spawnLocation, int power, Vector velocity, Player director)
    {
        this.spawnLocation = spawnLocation;
        this.power = power;
        this.velocity = velocity;
        this.director = director;
    }

    @Override
    public void run()
    {
        if(director != null && director.getInventory().getItemInMainHand().getType() == Config.DirectorTool)
        {
            Vector directionVector = director.getLocation().getDirection();

            if(directionVector.getY() > Config.DirectionRadiusPitch)
                velocity.setY(Config.DirectionRadiusPitch);
            else velocity.setY(Math.max(directionVector.getY(), -Config.DirectionRadiusPitch));

            if(velocity.getX() == 0D)
            {
                if(directionVector.getX() > Config.DirectionRadiusYaw)
                    velocity.setX(Config.DirectionRadiusYaw);
                else velocity.setX(Math.max(directionVector.getX(), -Config.DirectionRadiusYaw));

                if(velocity.getX() > 0)
                {
                    velocity.setZ(velocity.getZ() - velocity.getX());
                }
                else if(velocity.getX() < 0)
                {
                    velocity.setZ(velocity.getZ() + velocity.getX());
                }
            }
            else if(velocity.getZ() == 0D)
            {
                if(directionVector.getZ() > Config.DirectionRadiusYaw)
                    velocity.setZ(Config.DirectionRadiusYaw);
                else velocity.setZ(Math.max(directionVector.getZ(), -Config.DirectionRadiusYaw));

                if(velocity.getZ() > 0)
                {
                    velocity.setX(velocity.getX() - velocity.getZ());
                }
                else if(velocity.getZ() < 0)
                {
                    velocity.setX(velocity.getX() + velocity.getZ());
                }
            }
        }
        else
        {
            velocity.setY(0.1D);
        }

        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_FIREWORK_BLAST, SoundCategory.BLOCKS, 1f, 1f);
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1.5f);
        spawnLocation.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, spawnLocation, 1);
        spawnLocation.getWorld().spawnParticle(Particle.CLOUD, spawnLocation, 500, 1, 1, 1, 0.1);
        CannonManager.getInstance().addProjectile(new CannonProjectile(spawnLocation, power, velocity));
    }
}
