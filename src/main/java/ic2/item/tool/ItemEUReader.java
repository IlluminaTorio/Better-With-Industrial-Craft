

package ic2.item.tool;

import ic2.energy.EnergyNet;
import ic2.item.ItemIC2;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemEUReader
extends ItemIC2 {
    public ItemEUReader(String name, String namespaceId, int id) {
        super(name, namespaceId, id);
        this.setMaxStackSize(1);
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        if (world.isClientSide) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
            long conducted = EnergyNet.getForWorld(world).getTotalEnergyConducted(te);
            if (player != null) {
                player.sendMessage("Total energy conducted: " + conducted + " EU");
            }
            return true;
        }
        return false;
    }
}

