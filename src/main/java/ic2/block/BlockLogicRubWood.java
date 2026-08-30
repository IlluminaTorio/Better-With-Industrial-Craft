

package ic2.block;

import ic2.IC2Items;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicRubWood
extends BlockLogicLog {
    public BlockLogicRubWood(@NotNull Block<?> block) {
        super(block);
        block.setTicking(true);
    }

    public void onPlacedByMob(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @NotNull Mob mob, double xHit, double yHit) {
        world.setBlockDataNotify(tilePos, 0);
    }

    public void onPlacedOnSide(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, double xHit, double yHit) {
    }

    public static boolean hasSpot(int meta) {
        return meta >= 2 && meta % 6 >= 2;
    }

    public static boolean isWet(int meta) {
        return meta >= 6;
    }

    public void updateTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand, boolean isRandomTick) {
        if (world.isClientSide) {
            return;
        }
        int meta = world.getBlockData(tilePos);
        if (meta < 6) {
            return;
        }
        if (rand.nextInt(200) == 0) {
            world.setBlockDataNotify(tilePos, meta % 6);
        } else {
            world.scheduleBlockUpdate(tilePos, this.block, (long)this.tickRate());
        }
    }

    public int tickRate() {
        return 100;
    }

    public boolean treetapHarvest(@NotNull World world, @NotNull TilePosc pos, @Nullable Player player, @NotNull Side side, @Nullable ItemStack treetap) {
        int meta = world.getBlockData(pos);
        if (meta < 2 || meta % 6 != side.id) {
            return false;
        }
        if (meta < 6) {
            world.setBlockDataNotify(pos, meta + 6);
            this.ejectResin(world, pos.x(), pos.y(), pos.z(), side, world.rand.nextInt(3) + 1);
            world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5, "random.fizz", 1.0f, 1.0f);
            world.scheduleBlockUpdate(pos, this.block, (long)this.tickRate());
        } else {
            if (world.rand.nextInt(5) == 0) {
                world.setBlockDataNotify(pos, 1);
            }
            if (world.rand.nextInt(5) == 0) {
                this.ejectResin(world, pos.x(), pos.y(), pos.z(), side, 1);
                world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5, "random.fizz", 1.0f, 1.0f);
            }
        }
        if (treetap != null && player != null) {
            treetap.damageItem(1, (Entity)player);
        }
        return true;
    }

    private void ejectResin(World world, int x, int y, int z, Side side, int quantity) {
        double ex = (double)x + 0.5;
        double ey = (double)y + 0.5;
        double ez = (double)z + 0.5;
        switch (side) {
            case NORTH: {
                ez -= 0.75;
                break;
            }
            case SOUTH: {
                ez += 0.75;
                break;
            }
            case WEST: {
                ex -= 0.75;
                break;
            }
            case EAST: {
                ex += 0.75;
                break;
            }
            default: {
                ey += 0.75;
            }
        }
        for (int i = 0; i < quantity; ++i) {
            EntityItem item = new EntityItem(world, ex, ey, ez, new ItemStack(IC2Items.stickyResin));
            item.pickupDelay = 10;
            world.entityJoinedWorld((Entity)item);
        }
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
        if (dropCause == EnumDropCause.WORLD && world.rand.nextInt(6) == 0) {
            return new ItemStack[]{new ItemStack(IC2Items.stickyResin), new ItemStack(this.block)};
        }
        return new ItemStack[]{new ItemStack(this.block)};
    }

    public void onRemoved(@NotNull World world, @NotNull TilePosc tilePos, int data) {
        super.onRemoved(world, tilePos, data);
    }
}

