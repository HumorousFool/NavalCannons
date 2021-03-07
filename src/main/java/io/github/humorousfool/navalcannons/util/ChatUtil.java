package io.github.humorousfool.navalcannons.util;

import org.bukkit.ChatColor;

public class ChatUtil
{
    public static final String NAVALCANNONS_COMMAND_PREFIX = ChatColor.DARK_RED + "[" + ChatColor.GRAY + "NavalCannons" + ChatColor.DARK_RED + "] " + ChatColor.GRAY;

    public static int getLastDigit(String s)
    {
        if(s.isEmpty())
            return 0;

        int i = s.length() - 1;

        return Character.getNumericValue(s.charAt(i));
    }
}
