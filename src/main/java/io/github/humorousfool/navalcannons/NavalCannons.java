package io.github.humorousfool.navalcannons;

import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.listener.CombatListener;
import io.github.humorousfool.navalcannons.listener.MovecraftListener;
import io.github.humorousfool.navalcannons.listener.PlayerListener;
import io.github.humorousfool.navalcannons.localisation.I18nSupport;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class NavalCannons extends JavaPlugin
{
    private static NavalCannons instance;
    private boolean movecraft;

    public void onEnable()
    {
        instance = this;

        saveDefaultConfig();

        String[] languages = {"en", "fr"};
        for (String s : languages)
        {
            if (!new File(getDataFolder() + "/localisation/navalcannonslang_"+ s + ".properties").exists())
            {
                this.saveResource("localisation/navalcannonslang_"+ s + ".properties", false);
            }
        }
        Config.Locale = getConfig().getString("Locale", "en");
        I18nSupport.init();

        if(getServer().getPluginManager().getPlugin("Movecraft") == null || !getServer().getPluginManager().getPlugin("Movecraft").isEnabled())
        {
            getLogger().log(Level.INFO, I18nSupport.getInternationalisedString("Movecraft Not Found"));
            movecraft = false;
        }
        else
        {
            movecraft = true;
        }

        Config.CraftCannonsOnly = getConfig().getBoolean("CraftCannonsOnly", false);
        Config.CannonMaxPower = getConfig().getInt("CannonMaxPower", 3);
        Config.CannonMaxLength = getConfig().getInt("CannonMaxLength", 3);
        Config.CannonBackBlockType = Material.getMaterial(getConfig().getString("CannonBackBlockType", "DARK_OAK_STAIRS"));
        Config.CannonBackBlockData = (byte) getConfig().getInt("CannonBackBlockData", -1);
        Config.CannonMainBlockType = Material.getMaterial(getConfig().getString("CannonMainBlockType", "WOOL"));
        Config.CannonMainBlockData = (byte) getConfig().getInt("CannonMainBlockData", -1);
        Config.CannonProjectileTimeLimit = getConfig().getLong("CannonProjectileTimeLimit", 1000);
        Config.CannonVelocityMultiplier = getConfig().getDouble("CannonVelocityMultiplier", 3);
        Config.CannonProjectileGravity = getConfig().getDouble("CannonProjectileGravity", 2);
        Config.CannonIgnoreDistance = getConfig().getDouble("CannonIgnoreDistance", 2);
        Config.CannonPenetrationModifier = getConfig().getDouble("CannonPenetrationModifier", 1);
        Config.CannonFuse = getConfig().getLong("CannonFuse", 10);
        Config.CannonReloadTime = getConfig().getLong("CannonReloadTime", 20000);
        Config.CannonReloadShorten = getConfig().getLong("CannonReloadShorten", 10000);
        Config.CannonCraftPauseTime = getConfig().getLong("CannonCraftPauseTime", 2000);
        Config.CannonDamage = getConfig().getDouble("CannonDamage", 20);
        Config.MinBlocksPerCannon = Config.getMap((MemorySection) getConfig().get("MinBlocksPerCannon"));
        Config.MinBlocksPerPower = Config.getMap((MemorySection) getConfig().get("MinBlocksPerPower"));
        Config.MaxCannons = Config.getMap((MemorySection) getConfig().get("MaxCannons"));
        Config.FireAllSign = getConfig().getBoolean("FireAllSign");
        Config.FireAllCraftTypes = getConfig().getStringList("FireAllCraftTypes");
        Config.DirectorTool = Material.getMaterial(getConfig().getString("DirectorTool", "STICK"));
        Config.DirectionRadiusPitch = getConfig().getDouble("DirectionRadiusPitch", 0.4D);
        Config.DirectionRadiusYaw = getConfig().getDouble("DirectionRadiusYaw", 0.4D);
        Config.DirectingCraftTypes = getConfig().getStringList("DirectingCraftTypes");
        Config.PreventShipIgnite = getConfig().getBoolean("PreventShipIgnite");
        Config.MaxUpdatesPerTick = getConfig().getInt("MaxUpdatesPerTick", 1000);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        if(movecraft)
            getServer().getPluginManager().registerEvents(new MovecraftListener(), this);
    }

    public void onDisable()
    {
        instance = null;
    }

    public static NavalCannons getInstance() { return instance; }

    public boolean movecraftEnabled()
    {
        return movecraft;
    }
}
