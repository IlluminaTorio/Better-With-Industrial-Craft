

package ic2.si;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySource;
import java.util.ArrayList;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.fluids.impl.tile.TileEntityFluidContainer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public class TileEntitySignalumToEU
extends TileEntityFluidContainer
implements IEnergySource {
    private boolean addedToEnergyNet = false;
    public int euStored = 0;

    public TileEntitySignalumToEU() {
        this.fluidContents = new FluidStack[1];
        this.fluidCapacity = new int[1];


        this.fluidCapacity[0] = SIEnergy.TANK_CAPACITY;
        this.transferSpeed = SIEnergy.TRANSFER;
        this.acceptedFluids = new ArrayList();
        for (FluidStack fluidStack : this.fluidContents) {
            this.acceptedFluids.add(new ArrayList());
        }
        ((ArrayList)this.acceptedFluids.get(0)).addAll(SIEnergy.acceptedEnergy());
        for (sunsetsatellite.catalyst.core.util.Direction dir : sunsetsatellite.catalyst.core.util.Direction.values()) {
            this.fluidConnections.put(dir, Connection.INPUT);
            this.activeFluidSlots.put(dir, 0);
        }
    }

    @Override
    public int getMaxEnergyOutput() {
        return SIEnergy.MAX_EU_OUTPUT;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return true;
    }

    public void tick() {
        if (this.worldObj != null && !this.worldObj.isClientSide && !this.addedToEnergyNet) {
            EnergyNet.getForWorld(this.worldObj).addTileEntity((TileEntity)this);
            this.addedToEnergyNet = true;
        }
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }


        if (this.euStored < SIEnergy.EU_BUFFER) {
            FluidStack stack = this.getFluidInSlot(0);
            if (stack != null && stack.amount > 0) {
                int want = SIEnergy.EU_BUFFER - this.euStored;
                int mB = Math.min(stack.amount, Math.max(1, want / SIEnergy.EU_PER_MB));
                if (mB > stack.amount) {
                    mB = stack.amount;
                }
                stack.amount -= mB;
                if (stack.amount <= 0) {
                    this.setFluidInSlot(0, null);
                }
                this.euStored += mB * SIEnergy.EU_PER_MB;
            }
        }
        if (this.euStored > 0) {
            int output = Math.min(this.euStored, SIEnergy.MAX_EU_OUTPUT);
            this.euStored -= output;
            if (output > 0) {
                this.euStored += EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, output);
            }
        }
    }

    public void invalidate() {
        super.invalidate();
        if (this.addedToEnergyNet && this.worldObj != null) {
            EnergyNet net = EnergyNet.getForWorld(this.worldObj);
            if (net != null) {
                net.removeTileEntity((TileEntity)this);
            }
            this.addedToEnergyNet = false;
        }
    }

    public void readAdditionalData(@NotNull CompoundTag tag) {
        super.readAdditionalData(tag);
        this.euStored = tag.getInteger("euStored");
        this.addedToEnergyNet = tag.getBoolean("ic2net");
    }

    public void writeAdditionalData(@NotNull CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("euStored", this.euStored);
        tag.putBoolean("ic2net", this.addedToEnergyNet);
    }

    public String getNameTranslationKey() {
        return "tile.ic2.machine.converter_catalyst_to_eu.name";
    }
}
