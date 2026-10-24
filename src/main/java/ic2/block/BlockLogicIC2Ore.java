

package ic2.block;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import java.util.function.Supplier;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicIC2Ore
extends BlockLogic {
    public final Int2IntArrayMap variantMap = new Int2IntArrayMap();
    private final Supplier<ItemStack> rawDrop;

    public BlockLogicIC2Ore(@NotNull Block<?> block, @NotNull Block<?> parentBlock, @Nullable Supplier<ItemStack> rawDrop) {
        super(block, Materials.STONE);
        this.rawDrop = rawDrop;
        this.variantMap.put(parentBlock.id(), block.id());
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
        ItemStack[] itemStackArray;
        switch (dropCause) {
            case SILK_TOUCH:
            case PICK_BLOCK: {
                ItemStack[] itemStackArray2 = new ItemStack[1];
                itemStackArray = itemStackArray2;
                itemStackArray2[0] = new ItemStack(this.block);
                break;
            }
            case EXPLOSION:
            case PROPER_TOOL:
            case PISTON_CRUSH: {
                ItemStack raw;
                ItemStack itemStack = raw = this.rawDrop != null ? this.rawDrop.get() : null;
                if (raw == null) {
                    ItemStack[] itemStackArray3 = new ItemStack[1];
                    itemStackArray = itemStackArray3;
                    itemStackArray3[0] = new ItemStack(this.block);
                    break;
                }
                ItemStack[] itemStackArray4 = new ItemStack[1];
                itemStackArray = itemStackArray4;
                itemStackArray4[0] = raw.copy();
                break;
            }
            default: {
                itemStackArray = null;
            }
        }
        return itemStackArray;
    }
}

