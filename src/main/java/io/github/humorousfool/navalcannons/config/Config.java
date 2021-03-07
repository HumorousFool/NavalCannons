package io.github.humorousfool.navalcannons.config;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config
{
    public static String Locale = "en";

    public static boolean CraftCannonsOnly = false;

    public static int CannonMaxPower = 3;
    public static int CannonMaxLength = 3;
    public static Material CannonBackBlockType = Material.DARK_OAK_STAIRS;
    public static byte CannonBackBlockData = -1;
    public static Material CannonMainBlockType = Material.WOOL;
    public static byte CannonMainBlockData = -1;

    public static long CannonProjectileTimeLimit = 1000;
    public static double CannonVelocityMultiplier = 3;
    public static double CannonProjectileGravity = 2;
    public static double CannonIgnoreDistance = 2;
    public static double CannonPenetrationModifier = 1;
    public static long CannonFuse = 10;
    public static long CannonReloadTime = 20000;
    public static long CannonReloadShorten = 10000;
    public static long CannonCraftPauseTime = 2000;
    public static double CannonDamage = 20;

    public static HashMap<String, Integer> MinBlocksPerCannon = new HashMap<>();
    public static HashMap<String, Integer> MinBlocksPerPower = new HashMap<>();
    public static HashMap<String, Integer> MaxCannons = new HashMap<>();

    public static boolean FireAllSign = true;
    public static List<String> FireAllCraftTypes = new ArrayList<>();

    public static Material DirectorTool = Material.STICK;
    public static double DirectionRadiusPitch = 0.4D;
    public static double DirectionRadiusYaw = 0.4D;
    public static List<String> DirectingCraftTypes = new ArrayList<>();

    public static boolean PreventShipIgnite = true;

    public static int MaxUpdatesPerTick = 1000;

    public static HashMap<String, Integer> getMap(MemorySection memorySection)
    {
        HashMap<String, Integer> map = new HashMap<>();

        if(memorySection == null)
            return map;

        for(String key : memorySection.getKeys(false))
        {
            map.put(key, memorySection.getInt(key));
        }

        return map;
    }
}
