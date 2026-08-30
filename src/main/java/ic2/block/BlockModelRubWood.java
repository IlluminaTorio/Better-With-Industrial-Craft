

package ic2.block;

import ic2.block.BlockLogicRubWood;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockModelRubWood
extends BlockModelStandard<BlockLogicRubWood> {
    private final IconCoordinate side = TextureRegistry.getTexture((String)"ic2:block/rubber_wood_side");
    private final IconCoordinate dry = TextureRegistry.getTexture((String)"ic2:block/rubber_wood_dry");
    private final IconCoordinate wet = TextureRegistry.getTexture((String)"ic2:block/rubber_wood_wet");
    private final IconCoordinate top = TextureRegistry.getTexture((String)"ic2:block/rubber_wood_top");

    public BlockModelRubWood(Block<?> block) {
        super((Block<BlockLogicRubWood>)(Object)block);
    }

    public IconCoordinate getBlockTexture(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
        return this.getTextureForMeta(source.getBlockData(tilePos), side);
    }

    public IconCoordinate getBlockTextureFromSideAndMetadata(@NotNull Side side, int meta) {
        return this.getTextureForMeta(meta, side);
    }

    private IconCoordinate getTextureForMeta(int meta, Side side) {
        if (side == Side.TOP || side == Side.BOTTOM) {
            return this.top;
        }
        if (meta >= 2 && side.id == meta % 6) {
            return meta >= 6 ? this.wet : this.dry;
        }
        return this.side;
    }
}

