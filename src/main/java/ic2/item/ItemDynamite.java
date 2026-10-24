

package ic2.item;

import ic2.IC2Blocks;
import ic2.entity.EntityDynamite;
import ic2.item.ItemIC2;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemDynamite
extends ItemIC2 {
    public final boolean sticky;

    public ItemDynamite(String name, String texKey, int id, boolean sticky) {
        super(name, texKey, id);
        this.sticky = sticky;
        this.setMaxStackSize(16);
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        Block<?> dynamiteBlock;
        if (this.sticky) {
            return false;
        }
        TilePos target = new TilePos(pos.x() + side.offsetX(), pos.y() + side.offsetY(), pos.z() + side.offsetZ());
        if (world.isAirBlock((TilePosc)target) && (dynamiteBlock = IC2Blocks.dynamite) != null && dynamiteBlock.canPlaceAt(world, (TilePosc)target)) {
            world.setBlockTypeNotify((TilePosc)target, dynamiteBlock);
            if (selfStack != null && player != null) {
                selfStack.consumeItem(player);
            }
            return true;
        }
        return true;
    }

    @Nullable
    public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
        selfStack.consumeItem(player);
        world.playSoundAtEntity((Entity)player, (Entity)player, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
        if (!world.isClientSide) {
            world.entityJoinedWorld((Entity)new EntityDynamite(world, (Mob)player, this.sticky));
        }
        return selfStack;
    }
}

