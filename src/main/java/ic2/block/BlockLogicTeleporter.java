

package ic2.block;

import ic2.IC2Items;
import ic2.block.BlockLogicIC2Machine;
import ic2.tileentity.TileEntityTeleporter;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicTeleporter
extends BlockLogicIC2Machine {
    public BlockLogicTeleporter(@NotNull Block<?> block) {
        super(block, -1);
    }

    private boolean holdingTransmitter(Player player) {
        ItemStack held = player.getCurrentEquippedItem();
        return held != null && held.getItem() == IC2Items.frequencyTransmitter;
    }

    @Override
    public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
        if (world.isClientSide) {
            return true;
        }
        if (this.holdingTransmitter(player)) {
            TileEntity te = world.getTileEntity(tilePos);
            if (te instanceof TileEntityTeleporter) {
                TileEntityTeleporter teleporter = (TileEntityTeleporter)te;
                teleporter.setFrequency(player.getCurrentEquippedItem(), player);
            }
            return true;
        }
        return super.onInteracted(world, tilePos, player, side, xHit, yHit);
    }

    public void onAttacked(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @NotNull Side side, double xHit, double yHit) {
        TileEntity te;
        super.onAttacked(world, tilePos, player, side, xHit, yHit);
        if (world.isClientSide) {
            return;
        }
        if (this.holdingTransmitter(player) && (te = world.getTileEntity(tilePos)) instanceof TileEntityTeleporter) {
            TileEntityTeleporter teleporter = (TileEntityTeleporter)te;
            teleporter.getFrequency(player.getCurrentEquippedItem(), player);
        }
    }
}

