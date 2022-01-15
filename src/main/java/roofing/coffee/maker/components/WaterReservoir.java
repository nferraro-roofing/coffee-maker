package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

public class WaterReservoir implements BusComponent<WaterReservoir> {

    /*
     * TODO: Should turn this into an app setting. The setting should specify a rate = e.g. cups /
     * TIME. WaterReservoir could become aware of the clock speed and adjust its cup brew rate per
     * time accordingly.
     */
    private static final int BREW_RATE = 1;

    private int cupsOfWater = 0;
    private boolean isBrewing = false;

    public WaterReservoir() {}

    @Override
    public void readBusMessage(BusMessage message) {
        isBrewing = message.getButton().isBrewRequested()
                && message.getWarmer().hasPot()
                && !message.getPot().isFull()
                && !isEmpty();

        int nextCupsOfWater = cupsOfWater - BREW_RATE;

        if (isBrewing && nextCupsOfWater >= 0) {
            cupsOfWater = nextCupsOfWater;
        }
    }

    @Override
    public void refreshFrom(WaterReservoir other) {
        this.cupsOfWater = other.cupsOfWater;
    }

    @Override
    public void reset() {
        cupsOfWater = 0;
        isBrewing = false;
    }

    public void fill(int cupsOfwater) {
        this.cupsOfWater += cupsOfwater;
    }

    public int cupsOfWater() {
        return cupsOfWater;
    }

    public boolean isBrewing() {
        return isBrewing;
    }

    public boolean isEmpty() {
        return cupsOfWater > 0;
    }

    protected int brewRate() {
        return BREW_RATE;
    }
}
