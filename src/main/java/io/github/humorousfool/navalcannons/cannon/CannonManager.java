package io.github.humorousfool.navalcannons.cannon;

import io.github.humorousfool.navalcannons.NavalCannons;
import io.github.humorousfool.navalcannons.cannon.task.movecraft.MovecraftPreFireCheckedTask;
import io.github.humorousfool.navalcannons.config.Config;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CannonManager
{
    public final ConcurrentHashMap<Location, Long> cannonCooldowns = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Craft, LinkedList<MovecraftCannon>> craftCannons = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Craft, ConcurrentLinkedQueue<MovecraftPreFireCheckedTask>> pendingFires = new ConcurrentHashMap<>();

    public final ConcurrentHashMap<Craft, Player> directors = new ConcurrentHashMap<>();

    private final Set<CannonProjectile> currentProjectiles = ConcurrentHashMap.newKeySet();
    private final Queue<Object> pendingSynchronousTasks = new ConcurrentLinkedQueue<>();

    private static CannonManager instance;
    public static CannonManager getInstance()
    {
        if(instance == null)
            instance = new CannonManager();
        return instance;
    }
    private CannonManager()
    {
        cannonLoop.runTaskTimerAsynchronously(NavalCannons.getInstance(), 0, 0);
        fireLoop.runTaskTimerAsynchronously(NavalCannons.getInstance(), 0, 1);
        synchronousTaskLoop.runTaskTimer(NavalCannons.getInstance(), 0, 0);
    }

    public void addProjectile(CannonProjectile projectile)
    {
        currentProjectiles.add(projectile);
    }

    public void submitBlockDestroy(Location position)
    {
        pendingSynchronousTasks.add(position);
    }

    public void submitEntityDamage(Damageable entity)
    {
        pendingSynchronousTasks.add(entity);
    }

    public void updateCraftCannons(Craft c, LinkedList<MovecraftCannon> cannons)
    {
        if(c.getNotificationPlayer() == null)
            return;

        if(craftCannons.containsKey(c))
        {
            craftCannons.replace(c, cannons);
        }
        else
        {
            craftCannons.put(c, cannons);
        }
    }

    public void removeCraft(Craft c)
    {
        craftCannons.remove(c);
    }

    public void addDirector(Craft c, Player player)
    {
        if(directors.containsKey(c))
        {
            directors.replace(c, player);
        }
        else
        {
            directors.put(c, player);
        }
    }
    public void removeDirector(Craft c)
    {
        directors.remove(c);
    }

    public void submitFireAll(Craft c, ConcurrentLinkedQueue<MovecraftPreFireCheckedTask> tasks)
    {
        if(pendingFires.containsKey(c))
            return;

        pendingFires.put(c, tasks);
    }

    private final BukkitRunnable cannonLoop = new BukkitRunnable()
    {
        @Override
        public void run()
        {
            for(CannonProjectile projectile : currentProjectiles)
            {
                if(projectile.fireTime + Config.CannonProjectileTimeLimit < System.currentTimeMillis() || projectile.expired)
                {
                    currentProjectiles.remove(projectile);
                    continue;
                }

                projectile.process();
            }
        }
    };

    private final BukkitRunnable fireLoop = new BukkitRunnable()
    {
        @Override
        public void run()
        {
            for(Craft c : pendingFires.keySet())
            {
                if(pendingFires.get(c).size() == 0)
                {
                    pendingFires.remove(c);
                    continue;
                }

                MovecraftPreFireCheckedTask poll = pendingFires.get(c).poll();

                if(poll != null)
                    poll.runTaskAsynchronously(NavalCannons.getInstance());
            }
        }
    };

    private final BukkitRunnable synchronousTaskLoop = new BukkitRunnable()
    {
        @Override
        public void run()
        {
            int runLength = Config.MaxUpdatesPerTick;
            int queueLength = pendingSynchronousTasks.size();

            runLength = Math.min(runLength, queueLength);

            for (int i = 0; i < runLength; i++)
            {
                Object poll = pendingSynchronousTasks.poll();

                if(poll instanceof Location)
                {
                    Location loc = (Location) poll;
                    loc.getBlock().setType(Material.AIR);
                }
                else if(poll instanceof Damageable)
                {
                    Damageable damageable = (Damageable) poll;
                    damageable.damage(Config.CannonDamage);
                }
            }
        }
    };
}
