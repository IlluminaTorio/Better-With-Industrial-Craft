

package ic2.block;

import ic2.IC2Blocks;
import ic2.entity.EntityDynamite;
import ic2.item.ItemRemote;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicDynamite
extends BlockLogic {
    public BlockLogicDynamite(@NotNull Block<?> block) {
        super(block, Materials.EXPLOSIVE);
    }

    public boolean isSolidRender() {
        return false;
    }

    public boolean canPlaceAt(@NotNull World world, @NotNull TilePosc tilePos) {
        return this.canStay(world, tilePos);
    }

    public boolean canStay(@NotNull World world, @NotNull TilePosc tilePos) {
        return world.isBlockNormalCube((TilePosc)new TilePos(tilePos.x() - 1, tilePos.y(), tilePos.z())) || world.isBlockNormalCube((TilePosc)new TilePos(tilePos.x() + 1, tilePos.y(), tilePos.z())) || world.isBlockNormalCube((TilePosc)new TilePos(tilePos.x(), tilePos.y(), tilePos.z() - 1)) || world.isBlockNormalCube((TilePosc)new TilePos(tilePos.x(), tilePos.y(), tilePos.z() + 1)) || world.isBlockNormalCube((TilePosc)new TilePos(tilePos.x(), tilePos.y() - 1, tilePos.z()));
    }

    public void onPlacedByWorld(@NotNull World world, @NotNull TilePosc tilePos) {
        super.onPlacedByWorld(world, tilePos);
        if (!this.canStay(world, tilePos)) {
            this.dropAsItem(world, tilePos);
        }
    }

    public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
        if (this.block == IC2Blocks.dynamiteRemote && !ItemRemote.isThereRemote(tilePos.x(), tilePos.y(), tilePos.z())) {
            world.setBlockTypeNotify((TilePosc)new TilePos(tilePos.x(), tilePos.y(), tilePos.z()), IC2Blocks.dynamite);
        }
        if (!this.canStay(world, tilePos)) {
            this.dropAsItem(world, tilePos);
        }
    }

    private void dropAsItem(@NotNull World world, @NotNull TilePosc tilePos) {
        if (world.isClientSide) {
            return;
        }
        ItemStack stack = this.block.getDefaultStack();
        world.dropItem(tilePos, stack);
        world.setBlockTypeNotify((TilePosc)new TilePos(tilePos.x(), tilePos.y(), tilePos.z()), Blocks.AIR);
    }

    public void onDestroyedByPlayer(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, int data, @NotNull Player player, @Nullable Item item) {
        if (world.isClientSide) {
            return;
        }
        EntityDynamite dynamite = new EntityDynamite(world, (float)tilePos.x() + 0.5f, (float)tilePos.y() + 0.5f, (float)tilePos.z() + 0.5f, false);
        dynamite.fuse = 40;
        world.entityJoinedWorld((Entity)dynamite);
        world.playSoundAtEntity(null, (Entity)dynamite, "random.fuse", 1.0f, 1.0f);
    }

    public void onDestroyedByExplosion(@NotNull World world, @NotNull TilePosc tilePos) {
        if (world.isClientSide) {
            return;
        }
        EntityDynamite dynamite = new EntityDynamite(world, (float)tilePos.x() + 0.5f, (float)tilePos.y() + 0.5f, (float)tilePos.z() + 0.5f, false);
        dynamite.fuse = 5;
        world.entityJoinedWorld((Entity)dynamite);
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
        if (dropCause == EnumDropCause.PICK_BLOCK) {
            return new ItemStack[]{this.block.getDefaultStack()};
        }
        return null;
    }
}

