

package ic2.tileentity;

import ic2.IC2Items;
import ic2.tileentity.TileEntityBaseGenerator;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;

public class TileEntityGeoGenerator
extends TileEntityBaseGenerator {
    public int maxLava = 24000;

    public TileEntityGeoGenerator() {
        super(2);
        this.production = 10;
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
        return this.fuel * i / this.maxLava;
    }

    static boolean isBucketState(ItemStack stack, NamespaceID state) {
        return stack != null && stack.getItem() == Items.BUCKET_IRON && state.equals((Object)ItemBucket.getState((ItemStack)stack));
    }

    @Override
    public boolean gainFuel() {
        if (this.inventory[1] == null || this.maxLava - this.fuel < 2000) {
            return false;
        }
        if (TileEntityGeoGenerator.isBucketState(this.inventory[1], ItemBucket.STATE_LAVA)) {
            this.fuel += 2000;
            this.inventory[1] = new ItemStack(Items.BUCKET_IRON);
            return true;
        }
        if (this.inventory[1].getItem() == IC2Items.cellLava) {
            this.fuel += 2000;
            --this.inventory[1].stackSize;
            if (this.inventory[1].stackSize <= 0) {
                this.inventory[1] = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean needsFuel() {
        return this.fuel <= 0;
    }

    @Override
    public String getMachineName() {
        return "Geothermal Generator";
    }

    @Override
    public String getGuiTexture() {
        return "GUIGeoGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.geothermal.name";
    }
}

