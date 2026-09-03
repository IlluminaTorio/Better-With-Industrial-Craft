package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;

public abstract class TileEntityAdvWaterGenerator
extends TileEntityCustomGenerator {
    public int ticker;
    public float currentQuality = 0.0f;

    public TileEntityAdvWaterGenerator(int production) {
        super(0);
        this.production = production;
        this.maxStorage = 8000;
        this.ticker = this.getUpdateSpeed();
    }

    public abstract int getUpdateSpeed();

    public abstract float getCurrentQuality();

    @Override
    public void updateGeneration() {
        ++this.ticker;
        if (this.ticker > this.getUpdateSpeed()) {
            this.ticker = 0;
            this.currentQuality = this.getCurrentQuality();
        }
        int gain = (int)((float)this.production * this.currentQuality);
        if (gain > 0 && this.storage + gain <= this.maxStorage) {
            this.storage += gain;
        }
    }

    @Override
    public boolean isGenerating() {
        return this.currentQuality > 0.0f;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.currentQuality = tag.getFloat("currentQuality");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putFloat("currentQuality", this.currentQuality);
    }
}
