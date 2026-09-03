package ic2.block;

import ic2.block.BlockLogicIC2Machine;
import ic2.tileentity.TileEntityIC2Block;
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

public class BlockModelIC2MachineEx
extends BlockModelStandard<BlockLogicIC2Machine> {
    private final IconCoordinate topTexture;
    private final IconCoordinate bottomTexture;
    private final IconCoordinate sideTexture;
    private final IconCoordinate frontTexture;
    private final IconCoordinate frontActiveTexture;
    private final IconCoordinate topActiveTexture;
    private final IconCoordinate sideActiveTexture;
    private final boolean frontOnBack;

    public BlockModelIC2MachineEx(Block<?> block, String top, String bottom, String side, String front, String frontActive, String topActive, String sideActive, boolean frontOnBack) {
        super((Block<BlockLogicIC2Machine>)(Object)block);
        this.setTex("ic2:block/" + top, new Side[]{Side.TOP});
        this.setTex("ic2:block/" + bottom, new Side[]{Side.BOTTOM});
        this.setTex("ic2:block/" + side, new Side[]{Side.NORTH, Side.SOUTH, Side.EAST, Side.WEST});
        this.topTexture = TextureRegistry.getTexture("ic2:block/" + top);
        this.bottomTexture = TextureRegistry.getTexture("ic2:block/" + bottom);
        this.sideTexture = TextureRegistry.getTexture("ic2:block/" + side);
        this.frontTexture = front != null ? TextureRegistry.getTexture("ic2:block/" + front) : null;
        this.frontActiveTexture = frontActive != null ? TextureRegistry.getTexture("ic2:block/" + frontActive) : null;
        this.topActiveTexture = topActive != null ? TextureRegistry.getTexture("ic2:block/" + topActive) : null;
        this.sideActiveTexture = sideActive != null ? TextureRegistry.getTexture("ic2:block/" + sideActive) : null;
        this.frontOnBack = frontOnBack;
    }

    public BlockModelIC2MachineEx(Block<?> block, String top, String bottom, String side, String front, String frontActive) {
        this(block, top, bottom, side, front, frontActive, null, null, false);
    }

    @Nullable
    public IconCoordinate getBlockTexture(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
        int meta = source.getBlockData(tilePos);
        Direction facing = BlockLogicRotatable.getDirectionFromMeta(meta);
        boolean active = false;
        TileEntity te = source.getTileEntity(tilePos);
        if (te instanceof TileEntityIC2Block) {
            active = ((TileEntityIC2Block)te).active;
        }
        if (side == Side.TOP) {
            if (active && this.topActiveTexture != null) {
                return this.topActiveTexture;
            }
            return this.topTexture;
        }
        if (side == Side.BOTTOM) {
            return this.bottomTexture;
        }
        if (facing != null && side.direction() == facing && this.frontTexture != null) {
            if (active && this.frontActiveTexture != null) {
                return this.frontActiveTexture;
            }
            return this.frontTexture;
        }
        if (this.frontOnBack && facing != null && side.direction() == facing.opposite() && this.frontTexture != null) {
            return this.frontTexture;
        }
        if (active && this.sideActiveTexture != null) {
            return this.sideActiveTexture;
        }
        return this.sideTexture;
    }

    @Nullable
    public IconCoordinate getBlockTextureFromSideAndMetadata(@NotNull Side side, int metadata) {
        Direction facing = BlockLogicRotatable.getDirectionFromMeta((int)metadata);
        if (facing == Direction.DOWN || facing == Direction.UP) {
            facing = Direction.NORTH;
        }
        if (side != Side.TOP && side != Side.BOTTOM && facing != null && side.direction() == facing && this.frontTexture != null) {
            return this.frontTexture;
        }
        if (this.frontOnBack && side != Side.TOP && side != Side.BOTTOM && facing != null && side.direction() == facing.opposite() && this.frontTexture != null) {
            return this.frontTexture;
        }
        return super.getBlockTextureFromSideAndMetadata(side, metadata);
    }
}
