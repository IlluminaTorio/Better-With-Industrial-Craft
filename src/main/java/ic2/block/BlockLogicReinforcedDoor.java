package ic2.block;

import java.util.function.Supplier;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.item.Item;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicReinforcedDoor
extends BlockLogicDoor {
    public BlockLogicReinforcedDoor(@NotNull Block<?> block, @NotNull Material material, boolean isTop, boolean requireTool, @Nullable Supplier<Item> droppedItem) {
        super(block, material, isTop, requireTool, droppedItem);
    }

    @Override
    public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
        TilePos queryPos = new TilePos();
        if (this.isTop) {
            Block<?> b = world.getBlockType(tilePos.down(queryPos));
            if (!(b.getLogic() instanceof BlockLogicDoor)) {
                world.setBlockTypeNotify(tilePos, Blocks.AIR);
            }
            return;
        }
        TilePos upperPos = tilePos.up(queryPos);
        Block<?> otherBlock = world.getBlockType(upperPos);
        if (!(otherBlock.getLogic() instanceof BlockLogicDoor)) {
            world.setBlockTypeNotify(tilePos, Blocks.AIR);
            return;
        }
        if (block.isSignalSource()) {
            boolean hasSignal = world.hasNeighborSignal(tilePos) || world.hasNeighborSignal(upperPos);
            this.onPoweredBlockChange(world, tilePos, hasSignal);
        }
    }
}
