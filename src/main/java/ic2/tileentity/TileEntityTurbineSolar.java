package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherRain;

public class TileEntityTurbineSolar
extends TileEntityCustomGenerator {
    public static final int maxHeat = 24000;
    public static final int maxWater = 40000;
    public int heat = 0;
    public int water = 0;
    public int waterCounter = 0;
    public int counter = 99;
    public boolean sunIsVisible = false;

    public TileEntityTurbineSolar() {
        super(2);
        this.production = 10;
        this.maxStorage = 100;
    }

    public float heatPercent() {
        return (float)this.heat / (float)maxHeat;
    }

    public int gaugeWaterScaled(int i) {
        return this.water * i / maxWater;
    }

    public void updateSunVisibility() {
        if (!this.worldObj.isDaytime()) {
            this.sunIsVisible = false;
            return;
        }
        Weather weather = this.worldObj.getWeatherManager().getCurrentWeather();
        if (weather instanceof WeatherRain) {
            this.sunIsVisible = false;
            return;
        }
        for (int y = this.tilePos.y() + 1; y < this.worldObj.getHeightBlocks(); ++y) {
            int id = this.worldObj.getBlockId(this.tilePos.x(), y, this.tilePos.z());
            if (id == 0 || id == Blocks.GLASS.id() || id == Blocks.GLASS_STEEL.id()) continue;
            this.sunIsVisible = false;
            return;
        }
        this.sunIsVisible = true;
    }

    @Override
    public void updateGeneration() {
        ++this.counter;
        if (this.counter >= 100) {
            this.counter = 0;
            this.updateSunVisibility();
        }
        if (this.waterCounter < 100) {
            ++this.waterCounter;
        }
        if (this.needsWater() && this.waterCounter >= 100) {
            this.gainFuel();
        }
        this.water -= Math.round(20.0f * this.heatPercent());
        if (this.water < 0) {
            this.water = 0;
        }
        int eu = (int)((float)this.water * this.heatPercent() / 4000.0f * IC2Config.turbineSolarOutput());
        if (eu > 0 && this.storage + eu <= this.maxStorage) {
            this.storage += eu;
        }
        if (this.sunIsVisible) {
            this.heat += 2;
            if (this.heat > maxHeat) {
                this.heat = maxHeat;
            }
        } else {
            --this.heat;
            if (this.heat < 0) {
                this.heat = 0;
            }
        }
    }

    @Override
    public boolean isGenerating() {
        return this.sunIsVisible;
    }

    public boolean needsWater() {
        return this.water + 2000 <= maxWater;
    }

    public boolean gainFuel() {
        if (this.inventory[1] != null) {
            if (TileEntityTurbineSolar.isBucketState(this.inventory[1], ItemBucket.STATE_WATER)) {
                this.inventory[1] = new ItemStack(Items.BUCKET_IRON);
                this.water += 2000;
                return true;
            }
            if (this.inventory[1].getItem() == IC2Items.cellWater) {
                --this.inventory[1].stackSize;
                if (this.inventory[1].stackSize <= 0) {
                    this.inventory[1] = null;
                }
                this.water += 2000;
                return true;
            }
        }
        return this.getWaterFromChests();
    }

    public boolean getWaterFromChests() {
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        return this.getWaterFromChest(x + 1, y, z) || this.getWaterFromChest(x - 1, y, z) || this.getWaterFromChest(x, y, z + 1) || this.getWaterFromChest(x, y, z - 1) || this.getWaterFromChest(x, y - 1, z) || this.getWaterFromChest(x, y + 1, z);
    }

    public boolean getWaterFromChest(int x, int y, int z) {
        if (!this.needsWater()) {
            return false;
        }
        TileEntity te = this.worldObj.getTileEntity(x, y, z);
        if (!(te instanceof Container)) {
            return false;
        }
        Container chest = (Container)te;
        for (int i = 0; i < chest.getContainerSize(); ++i) {
            ItemStack stack = chest.getItem(i);
            if (stack == null) continue;
            if (TileEntityTurbineSolar.isBucketState(stack, ItemBucket.STATE_WATER)) {
                chest.setItem(i, new ItemStack(Items.BUCKET_IRON));
                this.water += 2000;
                return true;
            }
            if (stack.getItem() != IC2Items.cellWater) continue;
            chest.setItem(i, null);
            this.water += 2000;
            return true;
        }
        return false;
    }

    static boolean isBucketState(ItemStack stack, NamespaceID state) {
        return stack != null && stack.getItem() == Items.BUCKET_IRON && state.equals(ItemBucket.getState(stack));
    }

    @Override
    public String getMachineName() {
        return "Turbine Solar";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.heat = tag.getInteger("heat");
        this.water = tag.getInteger("water");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("heat", this.heat);
        tag.putInt("water", this.water);
    }
}
