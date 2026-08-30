

package ic2.tileentity;

import ic2.tileentity.TileEntityBaseGenerator;
import ic2.tileentity.TileEntityIronFurnace;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;

public class TileEntityGenerator
extends TileEntityBaseGenerator {
    public int itemFuelTime = 0;

    public TileEntityGenerator() {
        super(2);
        this.production = 5;
    }

    @Override
    public int getMaximumStorage() {
        return 4000;
    }

    @Override
    public int gaugeFuelScaled(int i) {
        int r;
        if (this.fuel <= 0) {
            return 0;
        }
        if (this.itemFuelTime <= 0) {
            this.itemFuelTime = this.fuel;
        }
        if ((r = this.fuel * i / this.itemFuelTime) > i) {
            r = i;
        }
        return r;
    }

    static boolean isBucketState(ItemStack stack, NamespaceID state) {
        return stack != null && stack.getItem() == Items.BUCKET_IRON && state.equals((Object)ItemBucket.getState((ItemStack)stack));
    }

    @Override
    public boolean gainFuel() {
        if (this.inventory[1] == null) {
            return false;
        }
        if (TileEntityGenerator.isBucketState(this.inventory[1], ItemBucket.STATE_LAVA)) {
            return false;
        }
        int value = TileEntityIronFurnace.getFuelValueFor(this.inventory[1]) / 2;
        if (value <= 0) {
            return false;
        }
        this.fuel += value;
        this.itemFuelTime = value;
        if (this.inventory[1].getItem().hasContainerItem()) {
            this.inventory[1] = new ItemStack(this.inventory[1].getItem().getContainerItem());
        } else {
            --this.inventory[1].stackSize;
        }
        if (this.inventory[1] == null || this.inventory[1].stackSize <= 0) {
            this.inventory[1] = null;
        }
        return true;
    }

    @Override
    public boolean needsFuel() {
        return this.fuel <= 0;
    }

    @Override
    public boolean isConverting() {
        return this.fuel > 0;
    }

    @Override
    public String getMachineName() {
        return "Generator";
    }

    @Override
    public String getGuiTexture() {
        return "GUIGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.generator.name";
    }
}

