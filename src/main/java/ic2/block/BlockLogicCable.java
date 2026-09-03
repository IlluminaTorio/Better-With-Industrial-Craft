

package ic2.block;

import ic2.IC2Items;
import ic2.energy.IEnergyAcceptor;
import ic2.energy.IEnergyEmitter;
import ic2.item.ItemCablePlaceable;
import ic2.tileentity.TileEntityCable;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class BlockLogicCable
extends BlockLogic {
    public BlockLogicCable(@NotNull Block<?> block) {
        super(block, Materials.DECORATION);
        block.withEntity(TileEntityCable::new);
    }

    public static int getCableType(int meta) {
        return meta & 0xF;
    }

    public boolean isSolidRender() {
        return false;
    }

    public boolean isCubeShaped() {
        return false;
    }

    public boolean blocksLight() {
        return false;
    }

    public boolean isConnected(WorldSource source, TilePosc pos, Side side) {
        TileEntity te = source.getTileEntity(pos);
        boolean selfIsCable = te instanceof TileEntityCable;
        TilePos neighbor = pos.add(side.offsetX(), side.offsetY(), side.offsetZ(), new TilePos());
        TileEntity neighborTe = source.getTileEntity((TilePosc)neighbor);
        if (neighborTe == null) {
            return false;
        }
        if (neighborTe instanceof TileEntityCable) {
            TileEntityCable neighborCable = (TileEntityCable)neighborTe;
            if (!selfIsCable) {
                return true;
            }
            TileEntityCable self = (TileEntityCable)te;
            return self.color == 0 || neighborCable.color == 0 || self.color == neighborCable.color;
        }
        return neighborTe instanceof IEnergyAcceptor || neighborTe instanceof IEnergyEmitter;
    }

    @NotNull
    public AABBdc getBoundsFromState(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
        float thickness = this.getCableThickness(source.getBlockData(tilePos));
        double half = (double)thickness / 2.0;
        double min = 0.5 - half;
        double max = 0.5 + half;
        AABBd box = new AABBd(min, min, min, max, max, max);
        block8: for (Side side : Side.sides) {
            if (!this.isConnected(source, tilePos, side)) continue;
            switch (side) {
                case BOTTOM: {
                    box.setMin(box.minX, 0.0, box.minZ);
                    continue block8;
                }
                case TOP: {
                    box.setMax(box.maxX, 1.0, box.maxZ);
                    continue block8;
                }
                case NORTH: {
                    box.setMin(box.minX, box.minY, 0.0);
                    continue block8;
                }
                case SOUTH: {
                    box.setMax(box.maxX, box.maxY, 1.0);
                    continue block8;
                }
                case WEST: {
                    box.setMin(0.0, box.minY, box.minZ);
                    continue block8;
                }
                case EAST: {
                    box.setMax(1.0, box.maxY, box.maxZ);
                    continue block8;
                }
            }
        }
        return box;
    }

    @Nullable
    public AABBdc getCollisionAABB(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
        return this.getBoundsFromState(source, tilePos).translate((double)tilePos.x(), (double)tilePos.y(), (double)tilePos.z(), new AABBd());
    }

    public float getCableThickness(int meta) {
        float p = switch (BlockLogicCable.getCableType(meta)) {
            case 0 -> 6.0f;
            case 1 -> 4.0f;
            case 2 -> 3.0f;
            case 3 -> 5.0f;
            case 4 -> 6.0f;
            case 5 -> 6.0f;
            case 6 -> 8.0f;
            case 7 -> 10.0f;
            case 8 -> 12.0f;
            case 9 -> 4.0f;
            case 10 -> 5.0f;
            case 11 -> 12.0f;
            default -> 6.0f;
        };
        return p / 16.0f;
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
        if (dropCause == EnumDropCause.PICK_BLOCK) {
            int type = data & 0xF;
            return type >= 0 && type < IC2Items.cableItems.length && IC2Items.cableItems[type] != null
                ? new ItemStack[]{new ItemStack(IC2Items.cableItems[type], 1, 0)}
                : new ItemStack[]{new ItemStack(this.block, 1, type)};
        }
        if (dropCause == EnumDropCause.PROPER_TOOL || dropCause == EnumDropCause.EXPLOSION || dropCause == EnumDropCause.SILK_TOUCH) {
            return new ItemStack[]{new ItemStack((Item)IC2Items.cableItems[data & 0xF])};
        }
        return null;
    }

    public int getPlacedData(@Nullable Player player, @NotNull ItemStack itemStack, @NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, double xHit, double yHit) {
        Item item = itemStack.getItem();
        if (item instanceof ItemCablePlaceable) {
            ItemCablePlaceable cable = (ItemCablePlaceable)item;
            return cable.getCableType();
        }
        return 0;
    }

    public boolean paintCable(World world, TilePosc pos, int color) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable)te;
            return cable.changeColor(color);
        }
        return false;
    }

    public boolean cutInsulation(World world, TilePosc pos) {
        return false;
    }
}

