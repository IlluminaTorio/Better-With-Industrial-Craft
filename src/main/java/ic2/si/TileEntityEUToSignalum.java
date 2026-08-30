

package ic2.si;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySink;
import java.util.ArrayList;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.fluids.impl.tile.TileEntityFluidContainer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public class TileEntityEUToSignalum
extends TileEntityFluidContainer
implements IEnergySink {
    private boolean addedToEnergyNet = false;
    public int euBuffer = 0;

    public TileEntityEUToSignalum() {
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
            this.fluidConnections.put(dir, Connection.OUTPUT);
            this.activeFluidSlots.put(dir, 0);
        }
    }

    @Override
    public boolean demandsEnergy() {
        
        
        return this.euBuffer < SIEnergy.EU_BUFFER && this.getRemainingCapacity(0) > 0;
    }

    @Override
    public int injectEnergy(Direction direction, int amount) {
        
        if (amount > SIEnergy.MAX_EU_INPUT && !ic2.IC2Config.voltageSystemOff()) {
            IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
            return 0;
        }
        int space = SIEnergy.EU_BUFFER * 2 - this.euBuffer;
        int accepted = Math.min(space, amount);
        this.euBuffer += accepted;
        return amount - accepted;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    public void tick() {
        int space;
        int mB;
        if (this.worldObj != null && !this.worldObj.isClientSide && !this.addedToEnergyNet) {
            EnergyNet.getForWorld(this.worldObj).addTileEntity((TileEntity)this);
            this.addedToEnergyNet = true;
        }
        if (this.euBuffer >= SIEnergy.EU_PER_MB && !this.worldObj.isClientSide
                        && (mB = Math.min(space = this.getRemainingCapacity(0), this.euBuffer / SIEnergy.EU_PER_MB)) > 0) {
            
            this.insertFluid(0, SIEnergy.energyStack(mB));
            this.euBuffer -= mB * SIEnergy.EU_PER_MB;
        }
        super.tick();
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
        this.euBuffer = tag.getInteger("euBuffer");
        this.addedToEnergyNet = tag.getBoolean("ic2net");
    }

    public void writeAdditionalData(@NotNull CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("euBuffer", this.euBuffer);
        tag.putBoolean("ic2net", this.addedToEnergyNet);
    }

    public String getNameTranslationKey() {
        return "tile.ic2.machine.converter_eu_to_catalyst.name";
    }
}
