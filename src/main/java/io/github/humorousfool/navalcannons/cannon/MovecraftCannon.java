package io.github.humorousfool.navalcannons.cannon;

import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.util.Vector;

public class MovecraftCannon
{
    public MovecraftLocation relativeLocation;
    public int power;
    public int barrelLength;
    public Vector direction;
    public String identifier;
    public long lastFire = 0;
    public boolean speedLoaded = false;

    public MovecraftCannon(MovecraftLocation relativeLocation, int power, int barrelLength, Vector direction, String identifier)
    {
        this.relativeLocation = relativeLocation;
        this.power = power;
        this.barrelLength = barrelLength;
        this.direction = direction;
        this.identifier = identifier;
    }
}
