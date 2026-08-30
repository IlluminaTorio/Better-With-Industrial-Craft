

package ic2.block;

import ic2.block.BlockLogicCable;
import ic2.energy.IEnergyAcceptor;
import ic2.energy.IEnergyEmitter;
import ic2.tileentity.TileEntityCable;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class BlockModelCable
extends BlockModelStandard<BlockLogicCable> {
    public static final String[] CABLE_TEX_NAMES = new String[]{"cable_copper", "cable_copper_uninsulated", "cable_gold", "cable_gold_insulated", "cable_gold_insulated_2x", "cable_hv", "cable_hv_insulated", "cable_hv_insulated_2x", "cable_hv_insulated_4x", "cable_glass_fibre", "cable_tin"};

    public BlockModelCable(Block<BlockLogicCable> block) {
        super(block);
    }

    public static boolean isConnected(WorldSource source, TilePosc pos, Side side) {
        TilePos neighbor = pos.add(side.offsetX(), side.offsetY(), side.offsetZ(), new TilePos());
        TileEntity te = source.getTileEntity((TilePosc)neighbor);
        if (te == null) {
            return false;
        }
        if (te instanceof TileEntityCable) {
            return true;
        }
        return te instanceof IEnergyAcceptor || te instanceof IEnergyEmitter;
    }

    public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
        short s;
        int meta = worldSource.getBlockData(tilePos);
        float thickness = ((BlockLogicCable)this.block.getLogic()).getCableThickness(meta);
        float half = thickness / 2.0f;
        float center = 0.5f;
        TileEntity te = worldSource.getTileEntity(tilePos);
        if (te instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable)te;
            s = cable.color;
        } else {
            s = 0;
        }
        short color = s;
        AABBd bounds = new AABBd((double)(center - half), (double)(center - half), (double)(center - half), (double)(center + half), (double)(center + half), (double)(center + half));
        renderBlocks.renderStandardBlock(tessellator, worldSource, (BlockModelStandard)this, (AABBdc)bounds, tilePos);
        for (Side side : Side.sides) {
            if (!BlockModelCable.isConnected(worldSource, tilePos, side)) continue;
            bounds.setMin((double)(center - half), (double)(center - half), (double)(center - half)).setMax((double)(center + half), (double)(center + half), (double)(center + half));
            BlockModelCable.applySide(bounds, side, half, center);
            renderBlocks.renderStandardBlock(tessellator, worldSource, (BlockModelStandard)this, (AABBdc)bounds, tilePos);
        }
        return true;
    }

    private static void applySide(AABBd bounds, Side side, double half, double center) {
        switch (side) {
            case BOTTOM: {
                bounds.setMin(center - half, 0.0, center - half).setMax(center + half, center - half, center + half);
                break;
            }
            case TOP: {
                bounds.setMin(center - half, center + half, center - half).setMax(center + half, 1.0, center + half);
                break;
            }
            case NORTH: {
                bounds.setMin(center - half, center - half, 0.0).setMax(center + half, center + half, center - half);
                break;
            }
            case SOUTH: {
                bounds.setMin(center - half, center - half, center + half).setMax(center + half, center + half, 1.0);
                break;
            }
            case WEST: {
                bounds.setMin(0.0, center - half, center - half).setMax(center - half, center + half, center + half);
                break;
            }
            case EAST: {
                bounds.setMin(center + half, center - half, center - half).setMax(1.0, center + half, center + half);
                break;
            }
        }
    }

    @NotNull
    public IconCoordinate getBlockTexture(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
        short s;
        int meta = source.getBlockData(tilePos);
        TileEntity te = source.getTileEntity(tilePos);
        if (te instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable)te;
            s = cable.color;
        } else {
            s = 0;
        }
        short color = s;
        return this.getBlockTextureFromSideAndMetadata(side, meta, color);
    }

    @NotNull
    public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int meta, int color) {
        int type = meta & 0xF;
        String name = CABLE_TEX_NAMES[type];
        return TextureRegistry.getTexture((String)("ic2:block/cable/" + name + "_" + color));
    }

    public boolean shouldItemRender3d() {
        return false;
    }

    public void renderStandalone(@NotNull TessellatorGeneral tessellator, int metadata, byte lightIndex) {
        int type = metadata & 0xF;
        String name = CABLE_TEX_NAMES[type];
        IconCoordinate tex = TextureRegistry.hasTexture((String)("ic2:item/" + name)) ? TextureRegistry.getTexture((String)("ic2:item/" + name)) : this.getBlockTextureFromSideAndMetadata(Side.NORTH, metadata, 0);
        tessellator.startDrawingQuads();
        tessellator.offsetTranslation(-0.5, -0.5, -0.5);
        tessellator.setLightmapCoord1i((int)lightIndex);
        tessellator.setNormal(0.0f, 0.0f, -1.0f);
        tessellator.addVertexWithUV(0.0, 0.0, 0.0, tex.getIconUMax(), tex.getIconVMax());
        tessellator.addVertexWithUV(0.0, 1.0, 0.0, tex.getIconUMax(), tex.getIconVMin());
        tessellator.addVertexWithUV(1.0, 1.0, 0.0, tex.getIconUMin(), tex.getIconVMin());
        tessellator.addVertexWithUV(1.0, 0.0, 0.0, tex.getIconUMin(), tex.getIconVMax());
        tessellator.draw();
        tessellator.offsetTranslation(0.5, 0.5, 0.5);
    }
}

