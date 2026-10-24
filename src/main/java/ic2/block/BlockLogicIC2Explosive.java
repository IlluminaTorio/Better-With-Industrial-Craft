

package ic2.block;

import ic2.entity.EntityIC2Explosive;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemFireStriker;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockLogicIC2Explosive
extends BlockLogic {
    public final boolean canExplodeByHand;

    public BlockLogicIC2Explosive(@NotNull Block<?> block, boolean canExplodeByHand) {
        super(block, Materials.EXPLOSIVE);
        this.canExplodeByHand = canExplodeByHand;
    }

    @NotNull
    public abstract EntityIC2Explosive getExplosionEntity(@NotNull World var1, float var2, float var3, float var4);

    public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemFireStriker) {
            this.prime(world, tilePos);
            held.damageItem(1, (Entity)player);
            return true;
        }
        return false;
    }

    public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
        if (world.hasNeighborSignal(tilePos)) {
            this.prime(world, tilePos);
        }
    }

    public void onPlacedByWorld(@NotNull World world, @NotNull TilePosc tilePos) {
        super.onPlacedByWorld(world, tilePos);
        if (world.hasNeighborSignal(tilePos)) {
            this.prime(world, tilePos);
        }
    }

    public void onDestroyedByPlayer(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, int data, @NotNull Player player, @Nullable Item item) {
        if (!world.isClientSide && (this.canExplodeByHand || (data & 1) != 0)) {
            this.prime(world, tilePos);
        }
    }

    public void onDestroyedByExplosion(@NotNull World world, @NotNull TilePosc tilePos) {
        if (world.isClientSide) {
            return;
        }
        EntityIC2Explosive explosive = this.getExplosionEntity(world, (float)tilePos.x() + 0.5f, (float)tilePos.y() + 0.5f, (float)tilePos.z() + 0.5f);
        explosive.fuse = world.rand.nextInt(explosive.fuse / 4) + explosive.fuse / 8;
        world.entityJoinedWorld((Entity)explosive);
    }

    public void prime(@NotNull World world, @NotNull TilePosc tilePos) {
        if (world.isClientSide) {
            return;
        }
        EntityIC2Explosive explosive = this.getExplosionEntity(world, (float)tilePos.x() + 0.5f, (float)tilePos.y() + 0.5f, (float)tilePos.z() + 0.5f);
        world.setBlockTypeNotify((TilePosc)new TilePos(tilePos.x(), tilePos.y(), tilePos.z()), Blocks.AIR);
        world.entityJoinedWorld((Entity)explosive);
        world.playSoundAtEntity(null, (Entity)explosive, "random.fuse", 1.0f, 1.0f);
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
        ItemStack[] itemStackArray;
        if (this.canExplodeByHand) {
            return null;
        }
        switch (dropCause) {
            case PICK_BLOCK:
            case EXPLOSION:
            case PROPER_TOOL:
            case SILK_TOUCH:
            case PISTON_CRUSH: {
                ItemStack[] itemStackArray2 = new ItemStack[1];
                itemStackArray = itemStackArray2;
                itemStackArray2[0] = this.block.getDefaultStack();
                break;
            }
            default: {
                itemStackArray = null;
            }
        }
        return itemStackArray;
    }
}

