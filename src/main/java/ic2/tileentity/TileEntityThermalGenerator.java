package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.util.collection.NamespaceID;

public class TileEntityThermalGenerator
extends TileEntityBaseGenerator {
    public int lava = 0;
    public int ambientHeat = 0;
    public int counter = 199;
    public int maxLava = 30000;

    public TileEntityThermalGenerator() {
        super(2);
        this.production = 25;
    }

    @Override
    public int getMaximumStorage() {
        return 4000;
    }

    public int gaugeLavaScaled(int i) {
        if (this.lava <= 0) {
            return 0;
        }
        return this.lava * i / this.maxLava;
    }

    public String getAmbientHeatEU() {
        return Float.toString((float)this.ambientHeat / 2000.0f);
    }

    public boolean significantAmbientHeat() {
        return (float)this.ambientHeat / 2000.0f > 0.1f;
    }

    public boolean hasLava() {
        return this.lava > 0;
    }

    @Override
    public int gaugeFuelScaled(int i) {
        return this.gaugeLavaScaled(i);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        ++this.counter;
        if (this.counter >= 200) {
            this.counter = 0;
            this.ambientHeat = this.getAmbientHeat();
        }
        if (!this.hasLava() && this.ambientHeat > 0) {
            int gain = (int)((float)this.ambientHeat / 2000.0f * IC2Config.thermalGeneratorPassiveOutput());
            if (gain > 0 && this.storage + gain <= this.maxStorage) {
                this.storage += gain;
                this.setChanged();
            }
        }
    }

    public int getAmbientHeat() {
        int heat = 0;
        int baseX = this.tilePos.x();
        int baseY = this.tilePos.y();
        int baseZ = this.tilePos.z();
        for (int x = baseX - 7; x <= baseX + 7; ++x) {
            for (int y = baseY - 7; y <= baseY + 7; ++y) {
                for (int z = baseZ - 7; z <= baseZ + 7; ++z) {
                    Material material = this.worldObj.getBlockMaterial(x, y, z);
                    if (material == Materials.LAVA) {
                        heat += 10;
                        continue;
                    }
                    if (material == Materials.FIRE) {
                        heat += 5;
                        continue;
                    }
                    if (material == Materials.STONE || material == Materials.BASALT || material == Materials.LIMESTONE || material == Materials.GRANITE || material == Materials.PERMAFROST || material == Materials.MARBLE || material == Materials.SLATE || material == Materials.NETHERRACK || material == Materials.METAL || material == Materials.IRON) {
                        heat += 2;
                        continue;
                    }
                    if (material == Materials.ICE) {
                        heat -= 3;
                        continue;
                    }
                    if (material == Materials.WATER || material == Materials.TOP_SNOW) {
                        heat -= 5;
                        continue;
                    }
                    if (material != Materials.AIR) continue;
                    heat -= 10;
                }
            }
        }
        if (heat < 0) {
            heat = 0;
        }
        return heat;
    }

    @Override
    public boolean gainFuel() {
        if (this.inventory[1] == null || this.maxLava - this.lava < 2000) {
            return false;
        }
        if (TileEntityThermalGenerator.isBucketState(this.inventory[1], ItemBucket.STATE_LAVA)) {
            this.lava += 2000;
            this.inventory[1] = new ItemStack(Items.BUCKET_IRON);
            return true;
        }
        if (this.inventory[1].getItem() == IC2Items.cellLava) {
            this.lava += 2000;
            --this.inventory[1].stackSize;
            if (this.inventory[1].stackSize <= 0) {
                this.inventory[1] = null;
            }
            return true;
        }
        return false;
    }

    static boolean isBucketState(ItemStack stack, NamespaceID state) {
        return stack != null && stack.getItem() == Items.BUCKET_IRON && state.equals(ItemBucket.getState(stack));
    }

    @Override
    public boolean needsFuel() {
        return this.lava <= 0;
    }

    @Override
    public boolean isConverting() {
        return this.hasLava();
    }

    @Override
    public int[] getChargeSlotPos() {
        return new int[]{65, 17};
    }

    @Override
    public int[] getFuelSlotPos() {
        return new int[]{65, 53};
    }

    @Override
    public String getMachineName() {
        return "Thermal Generator";
    }

    @Override
    public String getGuiTexture() {
        return "GUIThermalGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.thermal.name";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.lava = tag.getInteger("lava");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("lava", this.lava);
    }
}
