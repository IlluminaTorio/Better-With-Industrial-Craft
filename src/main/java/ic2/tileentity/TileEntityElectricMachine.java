

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.tileentity.TileEntityElecMachine;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityElectricMachine
extends TileEntityElecMachine {
    public int soundTicker;
    public int progress = 0;
    public int energyConsume;
    public int operationLength;

    public TileEntityElectricMachine(int slots, int energyConsume, int operationLength, int maxInput) {
        super(slots, 1, energyConsume * operationLength + maxInput - 1, maxInput);
        this.energyConsume = energyConsume;
        this.operationLength = operationLength;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / this.operationLength;
    }

    public int gaugeFuelScaled(int i) {
        if (this.energy <= 0) {
            return 0;
        }
        int r = this.energy * i / (this.operationLength * this.energyConsume);
        if (r > i) {
            r = i;
        }
        return r;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean isOperating = this.isOperating();
        boolean needsInvUpdate = false;
        if (this.energy > 0 && this.canOperate()) {
            this.energy -= this.energyConsume;
        }
        if (this.energy <= this.energyConsume * this.operationLength && this.canOperate()) {
            needsInvUpdate = this.provideEnergy();
        }
        if (isOperating && this.canOperate()) {
            ++this.soundTicker;
            if (this.soundTicker % this.getLoopingTime() == 0) {
                this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, this.getLoopSound(), 1.0f, 1.0f);
            }
            ++this.progress;
            if (this.progress >= this.operationLength) {
                this.progress = 0;
                this.operate();
                needsInvUpdate = true;
            }
        }
        if (!this.canOperate()) {
            this.progress = 0;
        }
        if (this.active != isOperating) {
            this.active = isOperating;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public void operate() {
        if (!this.canOperate()) {
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

    public boolean isOperating() {
        return this.energy > 0 && this.canOperate();
    }

    public boolean canOperate() {
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

    public abstract ItemStack getResultFor(ItemStack var1);

    public abstract String getLoopSound();

    public abstract int getLoopingTime();

    public abstract String getGuiTexture();

    public abstract String getGuiTitleKey();
}

