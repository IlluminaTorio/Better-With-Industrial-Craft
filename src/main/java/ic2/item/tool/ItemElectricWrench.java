

package ic2.item.tool;

import ic2.block.BlockLogicIC2Machine;
import ic2.item.ElectricItem;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemElectricWrench
extends ElectricItem {
    public ItemElectricWrench(String name, String namespaceId, int id) {
        super(name, namespaceId, id, 1, 50, 250);
        this.setMaxDamage(202);
        this.setMaxStackSize(1);
    }

    public boolean onUseOnBlock(ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        if (world.isClientSide) {
            return false;
        }
        BlockLogic blockLogic = world.getBlockType(pos).getLogic();
        if (blockLogic instanceof BlockLogicIC2Machine) {
            BlockLogicIC2Machine machineLogic = (BlockLogicIC2Machine)blockLogic;
            boolean result = machineLogic.dismantleWithWrench(world, pos, player);
            if (result) {
                this.use(selfStack, 1);
            }
            return result;
        }
        return false;
    }

    public boolean isDamagable() {
        return false;
    }
}

