package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import ic2.energy.EnergyNet;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityWoodGasserElec
extends TileEntityElecMachine {
    public static final int maxInput = 32;
    public int progress = 0;
    public int gasAmount = 0;
    public static final int operationLength = 130;
    public int gasPerBucket = 160;

    public TileEntityWoodGasserElec() {
        super(5, 1, 500, maxInput);
    }

    public float getGas() {
        return (float)this.gasAmount / (float)(18 * this.gasPerBucket);
    }

    public int gaugeGasScaled(int i) {
        return this.gasAmount * i / (18 * this.gasPerBucket);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / operationLength;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasOperating = this.isActive();
        boolean needsInvUpdate = this.provideEnergy();
        if (this.hasEnergy() && this.canRun()) {
            ++this.progress;
            if (this.gasAmount < this.gasPerBucket * 18) {
                ++this.gasAmount;
                needsInvUpdate = true;
            }
            this.energy -= (int)(3.0f * IC2Config.woodGasserElecEu());
            if (this.progress >= operationLength) {
                this.progress = 1;
                this.smelt();
                needsInvUpdate = true;
            }
        } else {
            this.progress = 0;
        }
        if (this.fillCell()) {
            needsInvUpdate = true;
        }
        if (this.active != this.isActive()) {
            this.active = this.isActive();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public void smelt() {
        if (!this.canRun()) {
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

    public boolean isActive() {
        return this.progress > 0;
    }

    public float getChargeLevel() {
        float f = (float)this.energy / (float)(this.maxEnergy - maxInput + 1);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    public boolean hasEnergy() {
        return this.energy >= (int)(3.0f * IC2Config.woodGasserElecEu());
    }

    public boolean canRun() {
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

    public ItemStack getResultFor(ItemStack itemstack) {
        if (itemstack == null) {
            return null;
        }
        return TileEntityWoodGasser.WoodGasserRecipes.getSmeltingResult(itemstack);
    }

    public boolean canFill() {
        if (this.inventory[3] == null || this.gasAmount < this.gasPerBucket) {
            return false;
        }
        return this.inventory[3].getItem() == IC2Items.cellEmpty && (this.inventory[4] == null || this.inventory[4].getItem() == IC2Items.woodGasCell && this.inventory[4].stackSize < this.inventory[4].getMaxStackSize());
    }

    public boolean fillCell() {
        if (!this.canFill()) {
            return false;
        }
        this.gasAmount -= this.gasPerBucket;
        if (this.inventory[4] == null) {
            this.inventory[4] = new ItemStack(IC2Items.woodGasCell);
        } else {
            ++this.inventory[4].stackSize;
        }
        --this.inventory[3].stackSize;
        if (this.inventory[3].stackSize <= 0) {
            this.inventory[3] = null;
        }
        return true;
    }

    @Override
    public String getMachineName() {
        return "Electric Wood Gasifier";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
        this.gasAmount = tag.getInteger("gas");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
        tag.putInt("gas", this.gasAmount);
    }

    @Override
    public int[] getQuickGrabSlots() {
        return new int[]{2, 4};
    }
}
