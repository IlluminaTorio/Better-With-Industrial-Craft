package ic2.si;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySource;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.simple.impl.TileEntityEnergyDevice;


public class TileEntityCatalystEnergyToEU
extends TileEntityEnergyDevice
implements IEnergySource {
        private boolean addedToEnergyNet = false;
        public int euStored = 0;

        
        public static final int EU_BUFFER = 512;

        
        public static final long UNIT_RECEIVE = 1000L;

        
        public static final long UNIT_CAPACITY = 10000L;

        public TileEntityCatalystEnergyToEU() {
                this.capacity = UNIT_CAPACITY;
                this.maxReceive = UNIT_RECEIVE;
                this.maxProvide = 0L;
        }

        @Override
        public boolean canProvide(@NotNull Direction dir) {
                return false;
        }

        @Override
        public long receiveEnergy(@NotNull Direction dir, long energy) {
                
                if (this.euStored >= EU_BUFFER) {
                        return 0L;
                }
                return super.receiveEnergy(dir, energy);
        }

        @Override
        public int getMaxEnergyOutput() {
                return SICatalystEnergy.MAX_EU_OUTPUT;
        }

        @Override
        public boolean emitsEnergyTo(TileEntity receiver, ic2.energy.Direction direction) {
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
                
                if (this.energy >= SICatalystEnergy.EU_PER_UNIT && this.euStored < EU_BUFFER) {
                        int want = EU_BUFFER - this.euStored;
                        int units = (int)Math.min(this.energy, (long)(want * SICatalystEnergy.EU_PER_UNIT));
                        int eu = units / SICatalystEnergy.EU_PER_UNIT;
                        if (eu > 0) {
                                this.internalChangeEnergy((long)(-eu * SICatalystEnergy.EU_PER_UNIT));
                                this.euStored += eu;
                        }
                }
                
                if (this.euStored > 0) {
                        int output = Math.min(this.euStored, SICatalystEnergy.MAX_EU_OUTPUT);
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
                return "tile.ic2.machine.converter_energy_to_eu.name";
        }
}
