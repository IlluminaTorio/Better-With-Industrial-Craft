package ic2.tileentity;

import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IChargeableItem;
import ic2.energy.IEnergySource;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityCustomGenerator
extends TileEntityIC2Machine
implements IEnergySource {
    public int storage = 0;
    public int maxStorage = 8000;
    public int production = 5;

    public TileEntityCustomGenerator(int slots) {
        super(slots);
    }

    public int gaugeStorageScaled(int i) {
        return this.storage * i / this.maxStorage;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasActive = this.active;
        boolean needsInvUpdate = false;
        this.updateGeneration();
        if (this.storage > this.maxStorage) {
            this.storage = this.maxStorage;
        }
        if (this.storage > 0) {
            if (this.inventory.length > 0 && this.inventory[0] != null && this.inventory[0].getItem() instanceof IChargeableItem && this.inventory[0].getMetadata() > 0) {
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
        boolean isActive = this.isGenerating();
        if (this.active != isActive) {
            this.active = isActive;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public abstract void updateGeneration();

    public abstract boolean isGenerating();

    @Override
    public int getMaxEnergyOutput() {
        return this.production;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return true;
    }
}
