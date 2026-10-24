

package ic2.block;

import ic2.worldgen.WorldFeatureRubberTree;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicSaplingBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicRubSapling
extends BlockLogicSaplingBase {
    public BlockLogicRubSapling(@NotNull Block<?> block) {
        super(block);
    }

    public void growTree(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random random) {
        WorldFeatureRubberTree tree = new WorldFeatureRubberTree();
        world.setBlockType(tilePos, Blocks.AIR);
        if (!tree.grow(world, tilePos.x(), tilePos.y(), tilePos.z(), random)) {
            world.setBlockType(tilePos, this.block);
        }
    }
}

