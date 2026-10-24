package ic2.tileentity;

import ic2.IC2Config;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherRain;
import net.minecraft.core.world.weather.WeatherStorm;

public class TileEntityWaveGenerator
extends TileEntityAdvWaterGenerator {
    public TileEntityWaveGenerator() {
        super(50);
    }

    @Override
    public int getUpdateSpeed() {
        return 60;
    }

    @Override
    public float getCurrentQuality() {
        float dis;
        int x = 0;
        int z = 0;
        Direction facing = this.getFacing();
        int dx = facing == null ? 0 : facing.offsetX();
        int dz = facing == null ? 1 : facing.offsetZ();
        int baseX = this.tilePos.x();
        int baseY = this.tilePos.y();
        int baseZ = this.tilePos.z();
        for (dis = 0.0f; this.worldObj.getBlockId(baseX + (x += dx), baseY, baseZ + (z += dz)) == Blocks.FLUID_WATER_STILL.id() && !(dis >= (float)IC2Config.waveGeneratorMaxDistance()); dis += 1.0f) {
        }
        Weather weather = this.worldObj.getWeatherManager().getCurrentWeather();
        if (weather instanceof WeatherStorm && this.worldObj.rand.nextFloat() > 0.9f) {
            dis *= 50.0f;
        } else if (weather instanceof WeatherRain) {
            dis *= 2.0f;
        }
        return dis * IC2Config.waveGeneratorOutput() / 200.0f / 16.0f;
    }

    public Direction getFacing() {
        return ic2.block.BlockLogicIC2Machine.getFacing(this.worldObj, this.tilePos);
    }

    @Override
    public String getMachineName() {
        return "Wave Generator";
    }
}
