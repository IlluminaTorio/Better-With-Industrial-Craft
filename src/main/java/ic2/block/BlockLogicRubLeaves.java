

package ic2.block;

import ic2.IC2Items;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicRubLeaves
extends BlockLogicLeavesBase {
    public BlockLogicRubLeaves(@NotNull Block<?> block, @NotNull Block<?> sapling) {
        super(block, Materials.LEAVES, sapling);
    }

    public void beginDecay(@NotNull World world, @NotNull TilePosc pos) {
        int meta = world.getBlockData(pos);
        if (!BlockLogicLeavesBase.isPermanent((int)meta)) {
            world.setBlockData(pos, BlockLogicLeavesBase.setDecaying((int)meta, (boolean)true));
        }
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
        if (dropCause == EnumDropCause.PICK_BLOCK || dropCause == EnumDropCause.SILK_TOUCH) {
            return new ItemStack[]{new ItemStack(this.block)};
        }
        if (world.rand.nextInt(35) == 0) {
            return new ItemStack[]{new ItemStack(this.sapling)};
        }
        if (world.rand.nextInt(120) == 0) {
            return new ItemStack[]{new ItemStack(IC2Items.stickyResin)};
        }
        return null;
    }
}

