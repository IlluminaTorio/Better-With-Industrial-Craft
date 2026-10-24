

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Items;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IChargeableItem;
import ic2.energy.IEnergySink;
import ic2.energy.IEnergySource;
import ic2.item.ItemBattery;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityElectricBlock
extends TileEntityIC2Machine
implements IEnergySink,
IEnergySource {
    public int tier;
    public int output;
    public int maxStorage;
    public int energy = 0;

    public TileEntityElectricBlock(int tier, int output, int maxStorage) {
        super(2);
        this.tier = tier;
        this.output = output;
        this.maxStorage = maxStorage;
    }

    public abstract String getGuiTitleKey();

    public String getNameByTier() {
        return switch (this.tier) {
            case 1 -> "BatBox";
            case 2 -> "MFE";
            case 3 -> "MFSU";
            default -> null;
        };
    }

    public int gaugeEnergyScaled(int i) {
        return this.energy * i / this.maxStorage;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.energy = tag.getInteger("energy");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("energy", this.energy);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean needsInvUpdate = false;
        if (this.energy > 0 && this.inventory[0] != null && this.inventory[0].getItem() instanceof IChargeableItem && this.inventory[0].getMetadata() > 0) {
            int sent = ((IChargeableItem)this.inventory[0].getItem()).giveEnergyTo(this.inventory[0], this.energy, this.tier);
            this.energy -= sent;
            boolean bl = needsInvUpdate = sent > 0;
        }
        if (this.demandsEnergy() && this.inventory[1] != null) {
            int gain;
            if (this.inventory[1].getItem() instanceof ItemBattery) {
                gain = ((ItemBattery)this.inventory[1].getItem()).getEnergyFrom(this.inventory[1], this.maxStorage - this.energy, this.tier);
                this.energy += gain;
                needsInvUpdate = gain > 0;
            } else {
                gain = 0;
                if (this.inventory[1].getItem() == Items.DUST_REDSTONE) {
                    gain = 500;
                } else if (this.inventory[1].getItem() == IC2Items.singleUseBattery) {
                    gain = 1000;
                }
                if (gain > 0 && gain <= this.maxStorage - this.energy) {
                    --this.inventory[1].stackSize;
                    if (this.inventory[1].stackSize <= 0) {
                        this.inventory[1] = null;
                    }
                    this.energy += gain;
                }
            }
        }
        if (this.energy >= this.output && !this.worldObj.hasNeighborSignal((TilePosc)this.tilePos) || this.energy >= this.maxStorage) {
            int send = this.output;
            this.energy -= send;
            send = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
            this.energy += send;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    @Override
    public boolean demandsEnergy() {

        if (ic2.IC2Config.voltageSystemOff()) {
            return this.energy < this.maxStorage;
        }
        return this.energy < this.maxStorage - this.output;
    }

    @Override
    public int injectEnergy(Direction direction, int amount) {

        if (!ic2.IC2Config.voltageSystemOff() && amount > this.output) {
            amount = this.output;
        }
        int space = this.maxStorage - this.energy;
        if (space >= amount) {
            this.energy += amount;
            return 0;
        }
        this.energy = this.maxStorage;
        return amount - space;
    }

    @Override
    public int getMaxEnergyOutput() {
        return this.output;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return true;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }
}

