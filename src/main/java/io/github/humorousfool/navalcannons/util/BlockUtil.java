package io.github.humorousfool.navalcannons.util;

import io.github.humorousfool.navalcannons.config.Config;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_12_R1.SoundCategory;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

public class BlockUtil
{
    public static Material[] UNBREAKABLE_BLOCKS = new Material[] {
            Material.OBSIDIAN,
            Material.BEDROCK,
            Material.AIR,
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA
    };

    public static int getCannonLength(Block signBlock)
    {
        Sign sign = (Sign) signBlock.getState().getData();
        return getCannonLength(sign, signBlock.getLocation());
    }

    public static int getCannonLength(Sign materialSign, Location loc)
    {
        Vector facing = blockFaceToVector(materialSign.getFacing().getOppositeFace());

        loc.add(facing);
        if(loc.getBlock().getType() == Config.CannonBackBlockType)
        {
            if (Config.CannonBackBlockData >= 0 && loc.getBlock().getData() != Config.CannonBackBlockData)
                return 0;
        }
        else
            return 0;

        int length = 0;

        for(int i = 0; i < Config.CannonMaxLength; i++)
        {
            loc.add(facing);
            if(loc.getBlock().getType() == Config.CannonMainBlockType)
            {
                if (Config.CannonMainBlockData >= 0 && loc.getBlock().getData() != Config.CannonMainBlockData)
                    break;

                length += 1;
            }
            else
                break;
        }

        return length;
    }

    public static Vector blockFaceToVector(BlockFace face)
    {
        if(face == BlockFace.NORTH)
            return new Vector(0, 0, -1);
        if(face == BlockFace.SOUTH)
            return new Vector(0, 0, 1);
        if(face == BlockFace.EAST)
            return new Vector(1, 0, 0);
        if(face == BlockFace.WEST)
            return new Vector(-1, 0, 0);

        return new Vector();
    }

    public static void playBlockBreakSound(Material material, Location loc)
    {
        net.minecraft.server.v1_12_R1.Block block = net.minecraft.server.v1_12_R1.Block.getById(material.getId());
        PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(block.getStepSound().e(), SoundCategory.BLOCKS, loc.getX(), loc.getY(), loc.getZ(), 1f, 0.8f);

        for(Player p : loc.getWorld().getPlayers())
        {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static boolean isChunkLoaded(Location location)
    {
        World world = ((CraftWorld)(location.getWorld())).getHandle();
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        return world.isLoaded(blockPosition);
    }
}
