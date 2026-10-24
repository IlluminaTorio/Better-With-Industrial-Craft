

package ic2.block;

import ic2.block.BlockLogicIC2Machine;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockModelIC2Machine
extends BlockModelStandard<BlockLogicIC2Machine> {
    private final IconCoordinate frontTexture;
    private final IconCoordinate frontActiveTexture;

    public BlockModelIC2Machine(Block<?> block, String top, String bottom, String side, String front, String frontActive) {
        super((Block<BlockLogicIC2Machine>)(Object)block);
        this.setTex("ic2:block/" + top, new Side[]{Side.TOP});
        this.setTex("ic2:block/" + bottom, new Side[]{Side.BOTTOM});
        this.setTex("ic2:block/" + side, new Side[]{Side.NORTH, Side.SOUTH, Side.EAST, Side.WEST});
        this.frontTexture = front != null ? TextureRegistry.getTexture((String)("ic2:block/" + front)) : null;
        this.frontActiveTexture = frontActive != null ? TextureRegistry.getTexture((String)("ic2:block/" + frontActive)) : null;
    }

    public BlockModelIC2Machine(Block<?> block, String allSides, String front, String frontActive) {
        this(block, allSides, allSides, allSides, front, frontActive);
    }

    @Nullable
    public IconCoordinate getBlockTexture(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
        int meta = source.getBlockData(tilePos);
        Direction facing = BlockLogicRotatable.getDirectionFromMeta((int)meta);
        if (side.direction() == facing && this.frontTexture != null) {
            TileEntity te = source.getTileEntity(tilePos);
            if (te instanceof TileEntityIC2Machine) {
                TileEntityIC2Machine machine = (TileEntityIC2Machine)te;
                if (machine.active && this.frontActiveTexture != null) {
                    return this.frontActiveTexture;
                }
            }
            return this.frontTexture;
        }
        return super.getBlockTexture(source, tilePos, side);
    }

    @Nullable
    public IconCoordinate getBlockTextureFromSideAndMetadata(@NotNull Side side, int metadata) {
        Direction facing = BlockLogicRotatable.getDirectionFromMeta((int)metadata);
        if (facing == Direction.DOWN || facing == Direction.UP) {
            facing = Direction.NORTH;
        }
        if (side.direction() == facing && this.frontTexture != null) {
            return this.frontTexture;
        }
        return super.getBlockTextureFromSideAndMetadata(side, metadata);
    }
}

