package ic2.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic2.tileentity.TileEntityIridiumStone;

public class BlockLogicIridiumStone
extends BlockLogic {
    public BlockLogicIridiumStone(@NotNull Block<?> block) {
        super(block, net.minecraft.core.block.material.Materials.METAL);
    }

    public float getStrength(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @Nullable Player player) {
        if (player != null && !this.isOwner(world, tilePos, player)) {
            return 0.0f;
        }
        return super.getStrength(world, tilePos, side, player);
    }

    public boolean isOwner(World world, TilePosc pos, Player player) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityIridiumStone) {
            String owner = ((TileEntityIridiumStone)te).owner;
            return owner == null || owner.isEmpty() || owner.equals(player.username);
        }
        return true;
    }

    public int getPlacedData(@Nullable Player player, @NotNull ItemStack itemStack, @NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, double xHit, double yHit) {
        int data = super.getPlacedData(player, itemStack, world, tilePos, side, xHit, yHit);
        if (player != null) {
            TileEntity te = world.getTileEntity(tilePos);
            if (te instanceof TileEntityIridiumStone) {
                ((TileEntityIridiumStone)te).owner = player.username;
                ((TileEntityIridiumStone)te).setChanged();
            }
        }
        return data;
    }

    public net.minecraft.core.item.ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull net.minecraft.core.enums.EnumDropCause dropCause, @Nullable TileEntity tileEntity) {
        return new ItemStack[]{this.block.getDefaultStack()};
    }
}
