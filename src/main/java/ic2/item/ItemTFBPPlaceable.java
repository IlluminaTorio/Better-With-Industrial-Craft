

package ic2.item;

import ic2.item.ItemIC2;
import ic2.tileentity.TileEntityTerraformer;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTFBPPlaceable
extends ItemIC2 {
    public ItemTFBPPlaceable(String name, String namespaceId, int id) {
        super(name, namespaceId, id);
        this.setMaxStackSize(1);
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        if (world.isClientSide) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityTerraformer) {
            TileEntityTerraformer terraformer = (TileEntityTerraformer)te;
            terraformer.ejectBlueprint();
            if (selfStack != null) {
                terraformer.setItem(0, selfStack.copy());
                --selfStack.stackSize;
                if (selfStack.stackSize <= 0) {
                    selfStack.stackSize = 0;
                }
            }
            return true;
        }
        return false;
    }
}

