package ic2.tileentity;

import ic2.net.MachineEventMessage;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;

public class TileEntityWeatherController
extends TileEntityElecMachine
implements MachineEventMessage.MachineEventHandler {
    public static final int SLOT_ENERGY = 0;
    public static final int EVENT_CLEAR = 0;
    public static final int EVENT_RAIN = 1;
    public static final int EVENT_STORM = 2;
    public static final int COST_CLEAR = 2000000;
    public static final int COST_RAIN = 4000000;
    public static final int COST_STORM = 8000000;
    public static final long WEATHER_DURATION = 7200L;

    public TileEntityWeatherController() {
        super(3, 0, 10000000, 4096);
    }

    public int getEventCost(int event) {
        if (event == EVENT_CLEAR) {
            return COST_CLEAR;
        }
        if (event == EVENT_RAIN) {
            return COST_RAIN;
        }
        if (event == EVENT_STORM) {
            return COST_STORM;
        }
        return 0;
    }

    public int gaugeEnergyScaled(int i) {
        if (this.maxEnergy <= 0 || this.energy <= 0) {
            return 0;
        }
        int r = this.energy * i / this.maxEnergy;
        return r > i ? i : r;
    }

    @Override
    public void handleEvent(int event) {
        if (this.worldObj == null || this.worldObj.isClientSide) {
            return;
        }
        Weather target = this.weatherForEvent(event);
        if (target == null || this.worldObj.getWorldType().hasCeiling()) {
            return;
        }
        if (this.worldObj.getWeatherManager().getCurrentWeather() == target) {
            return;
        }
        int cost = this.getEventCost(event);
        if (this.energy < cost) {
            return;
        }
        this.energy -= cost;
        this.worldObj.getWeatherManager().overrideWeather(target, WEATHER_DURATION, 1.0f);
        this.setChanged();
    }

    private Weather weatherForEvent(int event) {
        if (event == EVENT_CLEAR) {
            return Weathers.OVERWORLD_CLEAR;
        }
        if (event == EVENT_RAIN) {
            return Weathers.OVERWORLD_RAIN;
        }
        if (event == EVENT_STORM) {
            return Weathers.OVERWORLD_STORM;
        }
        return null;
    }

    @Override
    public String getMachineName() {
        return "Weather Controller";
    }
}
