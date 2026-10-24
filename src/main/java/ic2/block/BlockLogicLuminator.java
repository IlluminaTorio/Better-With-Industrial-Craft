

package ic2.block;

import ic2.tileentity.TileEntityLuminator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicTransparent;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicLuminator
extends BlockLogicTransparent {
    public BlockLogicLuminator(@NotNull Block<?> block) {
        super(block, Materials.GLASS);
    }

    public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
        if (world.isClientSide) {
            return true;
        }
        TileEntity te = world.getTileEntity(tilePos);
        if (te instanceof TileEntityLuminator) {
            TileEntityLuminator luminator = (TileEntityLuminator)te;
            luminator.switchStrength();
        }
        return true;
    }
}

