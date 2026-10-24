package ic2.tileentity;

import ic2.energy.EnergyNet;

public class TileEntityTransformerIV
extends TileEntityTransformer {
    public TileEntityTransformerIV() {
        super(512, 2048, 16384);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        if (this.redstone) {
            for (int i = 0; i < 3 && this.energy >= this.highOutput; ++i) {
                int send = this.highOutput;
                this.energy -= send;
                send = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
                this.energy += send;
            }
        } else {
            for (int i = 0; i < 12 && this.energy >= this.lowOutput; ++i) {
                int send = this.lowOutput;
                this.energy -= send;
                send = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
                this.energy += send;
            }
        }
    }
}
