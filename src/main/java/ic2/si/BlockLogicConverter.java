

package ic2.si;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import org.jetbrains.annotations.NotNull;

public class BlockLogicConverter
extends BlockLogic {
    public BlockLogicConverter(@NotNull Block<?> block) {
        super(block, Materials.METAL);
    }
}

