

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IChargeableItem;
import ic2.energy.IEnergySource;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityBaseGenerator
extends TileEntityIC2Machine
implements IEnergySource {
    public int soundTicker;
    public boolean wasConverting = false;
    public int fuel = 0;
    public int storage = 0;
    public int maxStorage = this.getMaximumStorage();
    public int production = 5;

    public TileEntityBaseGenerator(int slots) {
        super(slots);
    }

    public abstract int getMaximumStorage();

    public abstract boolean gainFuel();

    public abstract boolean needsFuel();

    public abstract int gaugeFuelScaled(int var1);

    public int[] getChargeSlotPos() {
        return new int[]{65, 17};
    }

    public int[] getFuelSlotPos() {
        return new int[]{65, 53};
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.fuel = tag.getShort("fuel");
        this.storage = tag.getShort("storage");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("fuel", (short)this.fuel);
        tag.putShort("storage", (short)this.storage);
    }

    public int gaugeStorageScaled(int i) {
        return this.storage * i / this.maxStorage;
    }

    public boolean isConverting() {
        if (this.fuel <= 0) {
            return false;
        }
        
        
        if (this.wasConverting) {
            return this.storage < this.maxStorage;
        }
        return this.storage + this.production * 4 <= this.maxStorage;
    }

    @Override
    public void tick() {
        boolean newActive;
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasConverting = this.isConverting();
        boolean needsInvUpdate = false;
        if (wasConverting) {
            --this.fuel;
        }
        if (this.needsFuel() && this.gainFuel()) {
            needsInvUpdate = true;
        }
        if (wasConverting) {
            this.storage += this.production;
            ++this.soundTicker;
            if (this.soundTicker % this.getLoopingTime() == 0) {
                this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, this.getLoopSound(), 1.0f, 1.0f);
            }
        }
        if (this.storage > 0) {
            if (this.inventory[0] != null && this.inventory[0].getItem() instanceof IChargeableItem && this.inventory[0].getMetadata() > 0) {
                int used = ((IChargeableItem)this.inventory[0].getItem()).giveEnergyTo(this.inventory[0], this.storage, 1);
                this.storage -= used;
                needsInvUpdate = used > 0;
            } else {
                int output = this.production;
                if (output > this.storage) {
                    output = this.storage;
                }
                this.storage -= output;
                if (output > 0) {
                    output = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, output);
                }
                this.storage += output;
            }
        }
        if (this.storage > this.maxStorage) {
            this.storage = this.maxStorage;
        }
        boolean finalActive = this.isConverting();
        this.wasConverting = finalActive;
        if (this.active != (newActive = finalActive)) {
            this.active = newActive;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public abstract String getGuiTexture();

    public abstract String getGuiTitleKey();

    public int getLoopingTime() {
        return 48;
    }

    public String getLoopSound() {
        return "random.fizz";
    }

    @Override
    public int getMaxEnergyOutput() {
        return this.production;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return true;
    }
}

