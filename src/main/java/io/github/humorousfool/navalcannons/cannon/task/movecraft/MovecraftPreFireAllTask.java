package io.github.humorousfool.navalcannons.cannon.task.movecraft;

import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.cannon.MovecraftCannon;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MovecraftPreFireAllTask extends BukkitRunnable
{
    Craft craft;
    String identifier;
    Player player;

    public MovecraftPreFireAllTask(Craft c, String identifier, Player player)
    {
        craft = c;
        this.identifier = identifier;
        this.player = player;
    }

    @Override
    public void run()
    {
        ConcurrentLinkedQueue<MovecraftPreFireCheckedTask> tasks = new ConcurrentLinkedQueue<>();

        for(MovecraftCannon c : CannonManager.getInstance().craftCannons.get(craft))
        {
            if(!identifier.equals("") && !c.identifier.equals(identifier))
                continue;

            MovecraftPreFireCheckedTask task = new MovecraftPreFireCheckedTask(c, craft, player);
            tasks.add(task);
        }

        CannonManager.getInstance().submitFireAll(craft, tasks);
    }
}
