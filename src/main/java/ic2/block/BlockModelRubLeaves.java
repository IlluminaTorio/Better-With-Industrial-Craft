

package ic2.block;

import ic2.block.BlockLogicRubLeaves;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.enums.LeavesQuality;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBdc;

public class BlockModelRubLeaves
extends BlockModelStandard<BlockLogicRubLeaves> {
    protected final IconCoordinate fastTexture;
    protected final IconCoordinate fancyTexture;

    public BlockModelRubLeaves(Block<?> block, String leavesTex) {
        super((Block<BlockLogicRubLeaves>)(Object)block);
        this.setAllTextures(leavesTex);
        this.fastTexture = TextureRegistry.getTexture((String)leavesTex);
        this.fancyTexture = TextureRegistry.getTexture((String)(leavesTex + "_fancy"));
    }

    @Nullable
    public IconCoordinate getBlockTextureFromSideAndMetadata(@NotNull Side side, int data) {
        return GameSettings.LEAVES_QUALITY.value != LeavesQuality.FAST ? this.fancyTexture : this.fastTexture;
    }

    public boolean shouldSideBeRendered(@NotNull WorldSource source, @NotNull AABBdc bounds, @NotNull TilePosc tilePos, @NotNull Side side) {
        if (GameSettings.LEAVES_QUALITY.value == LeavesQuality.FAST && source.getBlockType(tilePos) == this.block) {
            return false;
        }
        return super.shouldSideBeRendered(source, bounds, tilePos, side);
    }
}

