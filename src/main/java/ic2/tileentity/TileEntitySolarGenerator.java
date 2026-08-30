

package ic2.tileentity;

import ic2.tileentity.TileEntityBaseGenerator;
import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherRain;

public class TileEntitySolarGenerator
extends TileEntityBaseGenerator {
    public int ticker;
    public boolean initialized = false;
    public boolean sunIsVisible = false;

    public TileEntitySolarGenerator() {
        super(1);
        this.production = 1;
        this.ticker = new Random().nextInt(this.tickRate());
    }

    @Override
    public int getMaximumStorage() {
        return 100;
    }

    @Override
    public int gaugeFuelScaled(int i) {
        return i;
    }

    @Override
    public boolean gainFuel() {
        if (this.ticker++ % this.tickRate() == 0 || !this.initialized) {
            this.updateSunVisibility();
            this.initialized = true;
        }
        if (this.sunIsVisible) {
            ++this.storage;
            return true;
        }
        return false;
    }

    public void updateSunVisibility() {
        
        if (!this.worldObj.isDaytime()) {
            this.sunIsVisible = false;
            return;
        }
        Weather weather = this.worldObj.getWeatherManager().getCurrentWeather();
        if (weather instanceof WeatherRain) {
            this.sunIsVisible = false;
            return;
        }
        for (int y = this.tilePos.y() + 1; y < this.worldObj.getHeightBlocks(); ++y) {
            int id = this.worldObj.getBlockId(this.tilePos.x(), y, this.tilePos.z());
            if (id == 0 || id == Blocks.GLASS.id() || id == Blocks.GLASS_STEEL.id()) continue;
            this.sunIsVisible = false;
            return;
        }
        this.sunIsVisible = true;
    }

    public int tickRate() {
        return 4;
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
        return "Solar Panel";
    }

    @Override
    public String getGuiTexture() {
        return "GUISolarGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.solar_panel.name";
    }
}

