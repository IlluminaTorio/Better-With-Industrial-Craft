

package ic2.item.tool;

import ic2.block.BlockLogicCable;
import ic2.item.ItemIC2;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemInsulationCutter
extends ItemIC2 {
    public ItemInsulationCutter(String name, String namespaceId, int id) {
        super(name, namespaceId, id);
        this.setMaxDamage(80);
        this.setMaxStackSize(1);
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        BlockLogicCable cableLogic;
        boolean cut;
        if (world.isClientSide) {
            return false;
        }
        BlockLogic blockLogic = world.getBlockType(pos).getLogic();
        if (blockLogic instanceof BlockLogicCable && (cut = (cableLogic = (BlockLogicCable)blockLogic).cutInsulation(world, pos)) && player != null) {
            selfStack.damageItem(1, (Entity)player);
            return true;
        }
        return false;
    }
}

