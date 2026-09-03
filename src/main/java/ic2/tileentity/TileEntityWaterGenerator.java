

package ic2.tileentity;

import ic2.energy.Direction;
import ic2.tileentity.TileEntityBaseGenerator;
import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;

public class TileEntityWaterGenerator
extends TileEntityBaseGenerator {
    public static final Random randomizer = new Random();
    public int ticker;
    public boolean initialized = false;
    public int maxWater = 2000;

    public TileEntityWaterGenerator() {
        super(2);
        this.production = 2;
        this.ticker = randomizer.nextInt(this.tickRate());
    }

    @Override
    public int getMaximumStorage() {
        return 100;
    }

    @Override
    public int gaugeFuelScaled(int i) {
        if (this.fuel <= 0) {
            return 0;
        }
        return this.fuel * i / this.maxWater;
    }

    static boolean isBucketState(ItemStack stack, NamespaceID state) {
        return stack != null && stack.getItem() == Items.BUCKET_IRON && state.equals((Object)ItemBucket.getState((ItemStack)stack));
    }

    @Override
    public boolean gainFuel() {
        if (this.inventory[1] != null && this.maxWater - this.fuel >= 500 && TileEntityWaterGenerator.isBucketState(this.inventory[1], ItemBucket.STATE_WATER)) {
            this.fuel += 500;
            this.inventory[1] = new ItemStack(Items.BUCKET_IRON);
            return true;
        }
        if (this.fuel <= 0) {
            this.flowPower();
        }
        return false;
    }


    public int getFluidCapacityMillibuckets() {
        return Math.max(0, (this.maxWater - this.fuel) * 2);
    }


    public int acceptWaterMillibuckets(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int capacity = this.getFluidCapacityMillibuckets();
        int accepted = Math.min(amount, capacity);
        if (accepted > 0) {
            this.fuel += accepted / 2;
            this.setChanged();
        }
        return accepted;
    }

    protected void flowPower() {
        int waterCount = 0;
        for (Direction dir : Direction.values()) {
            int z;
            int y;
            int x = this.tilePos.x() + dir.getXOffset();
            int id = this.worldObj.getBlockId(x, y = this.tilePos.y() + dir.getYOffset(), z = this.tilePos.z() + dir.getZOffset());
            if (id != Blocks.FLUID_WATER_FLOWING.id() && id != Blocks.FLUID_WATER_STILL.id()) continue;
            ++waterCount;
        }
        if (waterCount > 0) {
            this.storage += waterCount;
        }
    }

    public int tickRate() {
        return 20;
    }

    @Override
    public boolean needsFuel() {
        return this.fuel <= 0;
    }

    @Override
    public int[] getChargeSlotPos() {
        return new int[]{80, 17};
    }

    @Override
    public int[] getFuelSlotPos() {
        return new int[]{80, 53};
    }

    @Override
    public String getMachineName() {
        return "Water Mill";
    }

    @Override
    public String getGuiTexture() {
        return "GUIWaterGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.water_mill.name";
    }
}

