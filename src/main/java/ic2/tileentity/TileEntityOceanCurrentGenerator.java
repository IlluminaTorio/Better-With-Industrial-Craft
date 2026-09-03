package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;

public class TileEntityOceanCurrentGenerator
extends TileEntityAdvWaterGenerator {
    public TileEntityOceanCurrentGenerator() {
        super((int)(5.0f * IC2Config.oceanCurrentOutput()) + 1);
    }

    @Override
    public int getUpdateSpeed() {
        return 10;
    }

    @Override
    public float getCurrentQuality() {
        int goodBlocks = 0;
        int otherGens = 0;
        int depth = IC2Config.oceanCurrentDepth();
        int area = IC2Config.oceanCurrentArea();
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        for (int j = 0; j < depth; ++j) {
            for (int i = -area; i <= area; ++i) {
                for (int k = -area; k <= area; ++k) {
                    if (j == 0 && i == 0 && k == 0) continue;
                    TileEntity te = this.worldObj.getTileEntity(x + i, y + j, z + k);
                    if (te instanceof TileEntityOceanCurrentGenerator) {
                        ++otherGens;
                        continue;
                    }
                    int id = this.worldObj.getBlockId(x + i, y + j, z + k);
                    if (id != Blocks.FLUID_WATER_STILL.id()) continue;
                    ++goodBlocks;
                }
            }
        }
        float multiplier = 1.0f / ((float)otherGens + 1.0f);
        float ratio = (float)goodBlocks / (float)(depth * (1 + 2 * area) * (1 + 2 * area));
        multiplier *= 1.2f * ratio * ratio - 0.2f * ratio;
        return Math.max(multiplier * IC2Config.oceanCurrentOutput(), 0.0f);
    }

    @Override
    public String getMachineName() {
        return "Ocean Current Generator";
    }

    @Override
    public int getMaxEnergyOutput() {
        return this.production;
    }
}
