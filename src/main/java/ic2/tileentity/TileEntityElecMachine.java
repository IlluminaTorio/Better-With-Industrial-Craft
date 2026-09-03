

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.energy.Direction;
import ic2.energy.IEnergySink;
import ic2.item.ItemBattery;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public abstract class TileEntityElecMachine
extends TileEntityIC2Machine
implements IEnergySink {
    public int energy = 0;
    public int fuelSlot;
    public int maxEnergy;
    public int maxInput;
    public boolean active = false;

    public TileEntityElecMachine(int slots, int fuelSlot, int maxEnergy, int maxInput) {
        super(slots);
        this.fuelSlot = fuelSlot;
        this.maxEnergy = maxEnergy;
        this.maxInput = maxInput;
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

    public boolean provideEnergy() {
        if (this.inventory[this.fuelSlot] != null) {
            ItemStack stack = this.inventory[this.fuelSlot];
            if (stack.getItem() instanceof ItemBattery) {
                this.energy += ((ItemBattery)stack.getItem()).getEnergyFrom(stack, this.maxEnergy - this.energy, 1);
                return true;
            }
            if (stack.getItem() == Items.DUST_REDSTONE) {
                this.energy = this.maxEnergy;
                --this.inventory[this.fuelSlot].stackSize;
                if (this.inventory[this.fuelSlot].stackSize <= 0) {
                    this.inventory[this.fuelSlot] = null;
                }
                return true;
            }
            if (stack.getItem() == IC2Items.singleUseBattery) {
                int room = this.maxEnergy - this.energy;
                if (room > 1000) {
                    room = 1000;
                }
                if (room > 0) {
                    this.energy += room;
                    --this.inventory[this.fuelSlot].stackSize;
                    if (this.inventory[this.fuelSlot].stackSize <= 0) {
                        this.inventory[this.fuelSlot] = null;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean demandsEnergy() {

        if (ic2.IC2Config.voltageSystemOff()) {
            return this.energy < this.maxEnergy;
        }
        return this.energy < this.maxEnergy - this.maxInput;
    }

    @Override
    public int injectEnergy(Direction direction, int amount) {

        if (!ic2.IC2Config.voltageSystemOff() && amount > this.maxInput) {
            if (this.explodesOnOverload()) {
                IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
                return 0;
            }
            amount = this.maxInput;
        }
        int space = this.maxEnergy - this.energy;
        if (space >= amount) {
            this.energy += amount;
            return 0;
        }
        this.energy = this.maxEnergy;
        return amount - space;
    }

    public boolean explodesOnOverload() {
        return false;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public int[] getQuickGrabSlots() {
        return this.getContainerSize() >= 3 ? new int[]{this.getContainerSize() - 1} : new int[0];
    }
}

