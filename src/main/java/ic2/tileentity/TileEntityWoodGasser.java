package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Items;
import ic2.tileentity.TileEntityIronFurnace;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityWoodGasser
extends TileEntityIC2Machine {
    public int fuel = 0;
    public int maxFuel = 0;
    public int progress = 0;
    public int gasAmount = 0;
    public static final int operationLength = 160;
    public int gasPerBucket = 160;

    public TileEntityWoodGasser() {
        super(4);
    }

    public float getGas() {
        return (float)this.gasAmount / (float)(9 * this.gasPerBucket);
    }

    public int gaugeGasScaled(int i) {
        return this.gasAmount * i / (9 * this.gasPerBucket);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / operationLength;
    }

    public int gaugeFuelScaled(int i) {
        if (this.maxFuel == 0) {
            return 0;
        }
        return this.fuel * i / this.maxFuel;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasBurning = this.isBurning();
        boolean needsInvUpdate = false;
        if (this.fuel <= 0 && this.canBurn()) {
            this.fuel = this.maxFuel = TileEntityWoodGasser.getFuelValueFor(this.inventory[1]);
            if (this.fuel > 0) {
                if (this.inventory[1].getItem().hasContainerItem()) {
                    this.inventory[1] = new ItemStack(this.inventory[1].getItem().getContainerItem());
                } else {
                    --this.inventory[1].stackSize;
                }
                if (this.inventory[1].stackSize <= 0) {
                    this.inventory[1] = null;
                }
                needsInvUpdate = true;
            }
        }
        if (this.isBurning() && this.canBurn()) {
            ++this.progress;
            if (this.gasAmount < this.gasPerBucket * 9) {
                ++this.gasAmount;
                needsInvUpdate = true;
            }
            if (this.progress >= operationLength) {
                this.progress = 0;
                this.smelt();
                needsInvUpdate = true;
            }
        } else {
            this.progress = 0;
        }
        if (this.fuel > 0) {
            --this.fuel;
        }
        if (this.fillCell()) {
            needsInvUpdate = true;
        }
        if (wasBurning != this.isBurning() || this.active != this.isBurning()) {
            this.active = this.isBurning();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public void smelt() {
        if (!this.canBurn()) {
            return;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[0]);
        if (this.inventory[2] == null) {
            this.inventory[2] = itemstack.copy();
        } else {
            this.inventory[2].stackSize += itemstack.stackSize;
        }
        if (this.inventory[0].getItem().hasContainerItem()) {
            this.inventory[0] = new ItemStack(this.inventory[0].getItem().getContainerItem());
        } else {
            --this.inventory[0].stackSize;
        }
        if (this.inventory[0].stackSize <= 0) {
            this.inventory[0] = null;
        }
    }

    public boolean isBurning() {
        return this.fuel > 0;
    }

    public boolean canBurn() {
        if (this.inventory[0] == null) {
            return false;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[0]);
        if (itemstack == null) {
            return false;
        }
        if (this.inventory[2] == null) {
            return true;
        }
        if (!this.inventory[2].isItemEqual(itemstack)) {
            return false;
        }
        return this.inventory[2].stackSize + itemstack.stackSize <= this.inventory[2].getMaxStackSize();
    }

    public static int getFuelValueFor(ItemStack itemstack) {
        return TileEntityIronFurnace.getFuelValueFor(itemstack);
    }

    public ItemStack getResultFor(ItemStack itemstack) {
        if (itemstack == null) {
            return null;
        }
        return WoodGasserRecipes.getSmeltingResult(itemstack);
    }

    public boolean canFill() {
        if (this.inventory[3] == null || this.gasAmount < this.gasPerBucket) {
            return false;
        }
        return this.inventory[3].getItem() == IC2Items.cellEmpty && (this.inventory[2] == null || this.inventory[2].getItem() == IC2Items.woodGasCell && this.inventory[2].stackSize < 64);
    }

    public boolean fillCell() {
        if (!this.canFill()) {
            return false;
        }
        this.gasAmount -= this.gasPerBucket;
        if (this.inventory[2] == null) {
            this.inventory[2] = new ItemStack(IC2Items.woodGasCell);
        } else {
            ++this.inventory[2].stackSize;
        }
        --this.inventory[3].stackSize;
        if (this.inventory[3].stackSize <= 0) {
            this.inventory[3] = null;
        }
        return true;
    }

    @Override
    public String getMachineName() {
        return "Wood Gasifier";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.fuel = tag.getShort("fuel");
        this.maxFuel = tag.getShort("maxFuel");
        this.progress = tag.getShort("progress");
        this.gasAmount = tag.getInteger("gas");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("fuel", (short)this.fuel);
        tag.putShort("maxFuel", (short)this.maxFuel);
        tag.putShort("progress", (short)this.progress);
        tag.putInt("gas", this.gasAmount);
    }

    public static class WoodGasserRecipes {
        public static final Map<Integer, ItemStack> RECIPES = new HashMap<Integer, ItemStack>();

        public static ItemStack getSmeltingResult(ItemStack stack) {
            return RECIPES.get(stack.getItem().id);
        }
    }
}
