

package ic2.item;

import ic2.IC2Blocks;
import ic2.block.BlockLogicCable;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemCablePlaceable
extends Item {
    public final int cableType;

    public ItemCablePlaceable(String name, String namespaceId, int id, int cableType) {
        super(name, namespaceId, id);
        this.cableType = cableType;
    }

    public int getCableType() {
        return this.cableType;
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
        Block<BlockLogicCable> cableBlock;
        if (selfStack == null) {
            return false;
        }
        if (!world.canPlaceInsideBlock(blockPos)) {
            blockPos = blockPos.add(side.direction(), new TilePos());
        }
        if ((cableBlock = IC2Blocks.cable) == null) {
            return false;
        }
        if (world.canPlaceInsideBlock(blockPos) && world.setBlockTypeDataNotify(blockPos, cableBlock, this.cableType)) {
            world.playBlockSoundEffect((Entity)player, (double)((float)blockPos.x() + 0.5f), (double)((float)blockPos.y() + 0.5f), (double)((float)blockPos.z() + 0.5f), cableBlock, EnumBlockSoundEffectType.PLACE);
            selfStack.consumeItem(player);
            return true;
        }
        return false;
    }
}

