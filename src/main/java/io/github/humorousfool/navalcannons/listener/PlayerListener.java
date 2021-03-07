package io.github.humorousfool.navalcannons.listener;

import io.github.humorousfool.navalcannons.NavalCannons;
import io.github.humorousfool.navalcannons.cannon.CannonManager;
import io.github.humorousfool.navalcannons.cannon.task.normal.CannonPreFireTask;
import io.github.humorousfool.navalcannons.cannon.task.movecraft.MovecraftPreFireAllTask;
import io.github.humorousfool.navalcannons.cannon.task.movecraft.MovecraftPreFireTask;
import io.github.humorousfool.navalcannons.config.Config;
import io.github.humorousfool.navalcannons.localisation.I18nSupport;
import io.github.humorousfool.navalcannons.util.BlockUtil;
import io.github.humorousfool.navalcannons.util.ChatUtil;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.utils.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(event.getClickedBlock().getType() == Material.WALL_SIGN)
        {
            Sign sign = (Sign) event.getClickedBlock().getState();

            if(sign.getLine(0).equals(ChatColor.DARK_RED + "[Cannon]"))
            {
                if(!event.getPlayer().hasPermission("navalcannons.cannon.fire"))
                {
                    event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
                    return;
                }

                if(NavalCannons.getInstance().movecraftEnabled())
                {
                    Craft c = null;
                    for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(event.getClickedBlock().getWorld())) {
                        if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(event.getClickedBlock().getLocation())) &&
                                CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                            c = tcraft;
                            break;
                        }
                    }

                    if(c != null)
                    {
                        new MovecraftPreFireTask(event.getClickedBlock().getLocation(), c, event.getPlayer()).runTaskAsynchronously(NavalCannons.getInstance());
                        return;
                    }

                    else if(Config.CraftCannonsOnly)
                    {
                        event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(ChatUtil.NAVALCANNONS_COMMAND_PREFIX +
                                I18nSupport.getInternationalisedString("Sign - Must Be Part Of Craft")));
                        return;
                    }
                }

                new CannonPreFireTask(sign.getLocation()).runTaskAsynchronously(NavalCannons.getInstance());
                return;
            }
        }

        if(!NavalCannons.getInstance().movecraftEnabled())
            return;

        if(event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN)
        {
            Sign sign = (Sign) event.getClickedBlock().getState();

            if(sign.getLine(0).equalsIgnoreCase("Cannon Director"))
            {
                if(!event.getPlayer().hasPermission("navalcannons.cannon.direct"))
                {
                    event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
                    return;
                }

                Craft c = null;
                for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(event.getClickedBlock().getWorld())) {
                    if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(event.getClickedBlock().getLocation())) &&
                            CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                        c = tcraft;
                        break;
                    }
                }

                if(c == null || c.getSinking())
                {
                    event.getPlayer().sendMessage(ChatUtil.NAVALCANNONS_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Sign - Must Be Part Of Craft"));
                    return;
                }
                if(!Config.DirectingCraftTypes.contains(c.getType().getCraftName()))
                {
                    event.getPlayer().sendMessage(ChatUtil.NAVALCANNONS_COMMAND_PREFIX + I18nSupport.getInternationalisedString("CannonDirector - Not Allowed On Craft"));
                    return;
                }

                CannonManager.getInstance().addDirector(c, event.getPlayer());
                event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("CannonDirector - Now Directing"));
            }

            else if(sign.getLine(0).equals(ChatColor.DARK_RED + "[Fire All]"))
            {
                if(!event.getPlayer().hasPermission("navalcannons.cannon.fireall"))
                {
                    event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
                    return;
                }

                Craft c = null;
                for (Craft tcraft : CraftManager.getInstance().getCraftsInWorld(event.getClickedBlock().getWorld())) {
                    if (tcraft.getHitBox().contains(MathUtils.bukkit2MovecraftLoc(event.getClickedBlock().getLocation())) &&
                            CraftManager.getInstance().getPlayerFromCraft(tcraft) != null) {
                        c = tcraft;
                        break;
                    }
                }

                if(c == null || c.getSinking())
                {
                    event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(ChatUtil.NAVALCANNONS_COMMAND_PREFIX +
                            I18nSupport.getInternationalisedString("Sign - Must Be Part Of Craft")));
                    return;
                }
                if(!Config.FireAllCraftTypes.contains(c.getType().getCraftName()))
                {
                    event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(ChatUtil.NAVALCANNONS_COMMAND_PREFIX +
                            I18nSupport.getInternationalisedString("FireAll - Not Allowed On Craft")));
                    return;
                }

                new MovecraftPreFireAllTask(c, sign.getLine(1), event.getPlayer()).runTaskAsynchronously(NavalCannons.getInstance());
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if(event.getLine(0).equalsIgnoreCase(ChatColor.stripColor("cannon")) || event.getLine(0).equalsIgnoreCase(ChatColor.stripColor("[cannon]")))
        {
            if(!event.getPlayer().hasPermission("navalcannons.cannon.create"))
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
                return;
            }
            //Check if wall sign
            if(event.getBlock().getType() != Material.WALL_SIGN)
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatUtil.NAVALCANNONS_COMMAND_PREFIX + I18nSupport.getInternationalisedString("CannonSign - Must Be WallSign"));
                return;
            }
            //Check barrel length
            int length = BlockUtil.getCannonLength(event.getBlock());
            if(length == 0)
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatUtil.NAVALCANNONS_COMMAND_PREFIX + I18nSupport.getInternationalisedString("CannonSign - Cannon Not Found"));
                return;
            }

            //Title
            event.setLine(0, ChatColor.DARK_RED + "[Cannon]");

            //Power
            int power = 1;
            if(ChatUtil.getLastDigit(event.getLine(1)) >= Config.CannonMaxPower)
                power = 3;
            else if(ChatUtil.getLastDigit(event.getLine(1)) > 0)
                power = ChatUtil.getLastDigit(event.getLine(1));
            event.setLine(1, I18nSupport.getInternationalisedString("CannonSign - Power")+": " + power);

            //Set Barrel Length
            if(!event.getLine(2).equals("") && event.getLine(3).equals(""))
                event.setLine(3, event.getLine(2));

            event.setLine(2, I18nSupport.getInternationalisedString("CannonSign - Barrel Length")+": " + length);
        }

        else if(event.getLine(0).equalsIgnoreCase(ChatColor.stripColor("fire all")) || event.getLine(0).equalsIgnoreCase(ChatColor.stripColor("[fire all]")))
        {
            if(!Config.FireAllSign)
                return;

            if(!event.getPlayer().hasPermission("navalcannons.cannon.fireall.create"))
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
                return;
            }

            event.setLine(0, ChatColor.DARK_RED + "[Fire All]");
        }
    }
}
