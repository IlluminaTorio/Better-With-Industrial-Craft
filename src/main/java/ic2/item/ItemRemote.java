

package ic2.item;

import ic2.IC2Blocks;
import ic2.entity.EntityDynamite;
import ic2.item.ItemIC2;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRemote
extends ItemIC2 {
    public static final List<int[]> remotes = new ArrayList<int[]>();

    public ItemRemote(String name, String texKey, int id) {
        super(name, texKey, id);
        this.setMaxStackSize(1);
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        int freq;
        if (world.isClientSide) {
            return world.getBlockType(pos) == IC2Blocks.dynamite || world.getBlockType(pos) == IC2Blocks.dynamiteRemote;
        }
        if (selfStack != null && selfStack.getMetadata() == 0) {
            selfStack.setMetadata(world.rand.nextInt(9001));
        }
        int n = freq = selfStack == null ? 0 : selfStack.getMetadata();
        if (world.getBlockType(pos) == IC2Blocks.dynamite) {
            ItemRemote.addRemote(pos.x(), pos.y(), pos.z(), freq);
            world.setBlockTypeNotify(pos, IC2Blocks.dynamiteRemote);
            return true;
        }
        if (world.getBlockType(pos) == IC2Blocks.dynamiteRemote) {
            ItemRemote.removeRemote(pos.x(), pos.y(), pos.z());
            ItemRemote.addRemote(pos.x(), pos.y(), pos.z(), freq);
            return true;
        }
        return true;
    }

    @Nullable
    public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
        world.playSoundAtEntity((Entity)player, (Entity)player, "random.click", 1.0f, 1.0f);
        ItemRemote.launchRemotes(world, selfStack.getMetadata());
        return selfStack;
    }

    public static void addRemote(int x, int y, int z, int freq) {
        remotes.add(new int[]{x, y, z, freq});
    }

    public static void removeRemote(int x, int y, int z) {
        for (int i = 0; i < remotes.size(); ++i) {
            int[] arr = remotes.get(i);
            if (arr[0] != x || arr[1] != y || arr[2] != z) continue;
            remotes.remove(i);
            return;
        }
    }

    public static void launchRemotes(World world, int freq) {
        for (int i = 0; i < remotes.size(); ++i) {
            TilePos pos;
            int[] arr = remotes.get(i);
            if (arr[3] != freq || world.getBlockType((TilePosc)(pos = new TilePos(arr[0], arr[1], arr[2]))) != IC2Blocks.dynamiteRemote) continue;
            world.setBlockTypeNotify((TilePosc)pos, Blocks.AIR);
            if (world.getBlockId(arr[0], arr[1] - 1, arr[2]) == 0) {
                world.setBlockTypeNotify((TilePosc)pos, IC2Blocks.dynamite);
                continue;
            }
            EntityDynamite dynamite = new EntityDynamite(world, (double)arr[0] + 0.5, (double)arr[1] + 0.5, (double)arr[2] + 0.5, false);
            dynamite.fuse = 5;
            world.entityJoinedWorld((Entity)dynamite);
        }
    }

    public static boolean isThereRemote(int x, int y, int z) {
        for (int[] arr : remotes) {
            if (arr[0] != x || arr[1] != y || arr[2] != z) continue;
            return true;
        }
        return false;
    }
}

