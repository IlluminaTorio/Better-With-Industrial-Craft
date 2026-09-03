package ic2.si;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySink;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.energy.simple.impl.TileEntityEnergyGenerator;


public class TileEntityEUToCatalystEnergy
extends TileEntityEnergyGenerator
implements IEnergySink {
        private boolean addedToEnergyNet = false;
        public int euBuffer = 0;


        public static final int EU_BUFFER = 512;


        public static final long UNIT_CAPACITY = 10000L;


        public static final long UNIT_PROVIDE = 1000L;

        public TileEntityEUToCatalystEnergy() {
                this.capacity = UNIT_CAPACITY;
                this.maxReceive = 0L;
                this.maxProvide = UNIT_PROVIDE;
        }

        @Override
        public boolean demandsEnergy() {

                return this.euBuffer < EU_BUFFER && this.getCapacityRemaining() > 0L;
        }

        @Override
        public int injectEnergy(Direction direction, int amount) {
                if (amount > SIEnergy.MAX_EU_INPUT && !ic2.IC2Config.voltageSystemOff()) {
                        amount = SIEnergy.MAX_EU_INPUT;
                }
                int space = EU_BUFFER - this.euBuffer;
                int accepted = Math.min(space, amount);
                this.euBuffer += accepted;
                return amount - accepted;
        }

        @Override
        public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
                return true;
        }

        public void tick() {
                if (this.worldObj != null && !this.worldObj.isClientSide && !this.addedToEnergyNet) {
                        EnergyNet.getForWorld(this.worldObj).addTileEntity((TileEntity)this);
                        this.addedToEnergyNet = true;
                }
                if (!this.worldObj.isClientSide && this.euBuffer > 0) {

                        long room = this.getCapacityRemaining() / SICatalystEnergy.EU_PER_UNIT;
                        int convert = (int)Math.min(this.euBuffer, room);
                        if (convert > 0) {
                                this.internalChangeEnergy((long)convert * SICatalystEnergy.EU_PER_UNIT);
                                this.euBuffer -= convert;
                        }
                }
                super.tick();
        }

        @Override
        public long internalChangeEnergy(long difference) {

                if (this.energy + difference < 0L) {
                        difference = -this.energy;
                }
                if (this.energy + difference > this.capacity) {
                        difference = this.capacity - this.energy;
                }
                return super.internalChangeEnergy(difference);
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
                return "tile.ic2.machine.converter_eu_to_energy.name";
        }
}
