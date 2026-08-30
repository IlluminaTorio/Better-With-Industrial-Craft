

package ic2.item.tool;

import ic2.block.BlockLogicCable;
import ic2.item.ItemIC2;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemPainter
extends ItemIC2 {
    public final int color;

    public ItemPainter(String name, String namespaceId, int id, int color) {
        super(name, namespaceId, id);
        this.setMaxDamage(250);
        this.setMaxStackSize(1);
        this.color = color;
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        BlockLogicCable cableLogic;
        boolean changed;
        if (world.isClientSide) {
            return false;
        }
        BlockLogic blockLogic = world.getBlockType(pos).getLogic();
        if (blockLogic instanceof BlockLogicCable && (changed = (cableLogic = (BlockLogicCable)blockLogic).paintCable(world, pos, this.color))) {
            world.playSoundEffect((Entity)player, SoundCategory.WORLD_SOUNDS, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5, "random.click", 1.0f, 1.0f);
            selfStack.damageItem(1, (Entity)player);
            return true;
        }
        return false;
    }
}

