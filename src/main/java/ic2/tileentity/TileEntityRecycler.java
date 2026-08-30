

package ic2.tileentity;

import ic2.IC2Items;
import ic2.tileentity.TileEntityElectricMachine;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class TileEntityRecycler
extends TileEntityElectricMachine {
    public TileEntityRecycler() {
        super(3, 1, 35, 32);
    }

    @Override
    public void operate() {
        if (!this.canOperate()) {
            return;
        }
        --this.inventory[0].stackSize;
        if (this.inventory[0].stackSize <= 0) {
            this.inventory[0] = null;
        }
        if (this.worldObj.rand.nextInt(TileEntityRecycler.recycleChance()) == 0) {
            if (this.inventory[2] == null) {
                this.inventory[2] = new ItemStack(IC2Items.scrap);
            } else {
                ++this.inventory[2].stackSize;
            }
        }
    }

    @Override
    public boolean canOperate() {
        if (this.inventory[0] == null) {
            return false;
        }
        if (this.inventory[2] != null && (this.inventory[2].getItem() != IC2Items.scrap || this.inventory[2].stackSize >= 64)) {
            return false;
        }
        return this.canRecycle(this.inventory[0]);
    }

    public boolean canRecycle(ItemStack itemstack) {
        if (itemstack.getItem() == Items.STICK) {
            return false;
        }
        return itemstack.getItem() != IC2Items.scrap;
    }

    @Override
    public ItemStack getResultFor(ItemStack itemstack) {
        return null;
    }

    @Override
    public String getMachineName() {
        return "Recycler";
    }

    @Override
    public String getLoopSound() {
        return "random.fizz";
    }

    @Override
    public int getLoopingTime() {
        return 45;
    }

    @Override
    public String getGuiTexture() {
        return "GUIRecycler.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.machine.recycler.name";
    }

    public static int recycleChance() {
        return 8;
    }
}

