

package ic2.tileentity;

import ic2.tileentity.TileEntityBaseGenerator;
import java.util.Random;

public class TileEntityWindGenerator
extends TileEntityBaseGenerator {
    public static final Random randomizer = new Random();
    public double subproduction = 0.0;
    public double substorage = 0.0;
    public int ticker;
    public boolean initialized = false;
    public int obscuratedBlockCount = 0;
    public static int windStrength = 10;

    public TileEntityWindGenerator() {
        super(2);
        this.production = 5;
        this.ticker = randomizer.nextInt(this.tickRate());
    }

    @Override
    public int getMaximumStorage() {
        return 100;
    }

    @Override
    public int gaugeFuelScaled(int i) {
        return (int)((double)i * this.subproduction / 3.0);
    }

    @Override
    public boolean gainFuel() {
        ++this.ticker;
        if (this.ticker % this.tickRate() == 0 || !this.initialized) {
            if (this.ticker % (8 * this.tickRate()) != 0 || !this.initialized) {
                this.updateObscuratedBlockCount();
                this.initialized = true;
            }
            this.subproduction = 0.6666666666666666 * (double)windStrength * (double)(this.tilePos.y() - this.obscuratedBlockCount) / 550.0;
            if (this.subproduction < 0.0) {
                this.subproduction = 0.0;
            }
            this.production = (int)this.subproduction;
        }
        this.substorage += this.subproduction;
        while (this.substorage >= 1.0) {
            this.substorage -= 1.0;
            ++this.storage;
        }
        return true;
    }

    public void updateObscuratedBlockCount() {
        this.obscuratedBlockCount = -1;
        for (int x = -4; x < 5; ++x) {
            for (int y = -2; y < 5; ++y) {
                for (int z = -4; z < 5; ++z) {
                    if (this.worldObj.getBlockId(x + this.tilePos.x(), y + this.tilePos.y(), z + this.tilePos.z()) == 0) continue;
                    ++this.obscuratedBlockCount;
                }
            }
        }
    }

    public int tickRate() {
        return 128;
    }

    @Override
    public boolean needsFuel() {
        return true;
    }

    @Override
    public int[] getChargeSlotPos() {
        return new int[]{80, 26};
    }

    @Override
    public String getMachineName() {
        return "Wind Mill";
    }

    @Override
    public String getGuiTexture() {
        return "GUIWindGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.wind_mill.name";
    }

    public static void updateWind() {
        int upChance = 10;
        int downChance = 10;
        if (windStrength > 20) {
            upChance -= windStrength - 20;
        }
        if (windStrength < 10) {
            downChance -= 10 - windStrength;
        }
        if (randomizer.nextInt(100) <= upChance) {
            ++windStrength;
            return;
        }
        if (randomizer.nextInt(100) <= downChance) {
            --windStrength;
        }
    }
}

