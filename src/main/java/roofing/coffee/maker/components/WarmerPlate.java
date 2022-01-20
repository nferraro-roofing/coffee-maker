package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

public class WarmerPlate implements BusComponent<WarmerPlate> {

    // TODO: app setting
    // public in order to enable tests to spy into this value
    public static final int STAY_HOT_CYCLE_COUNT = 5;

    private int cyclesAfterBrewStopped = 0;
    private boolean hasPot = true;
    private boolean isHot = false;

    @Override
    public void readBusMessage(BusMessage message) {
        boolean reservoirIsBrewing = message.getReservoir().isBrewing();

        // < instead of <= because WarmerPlate naturally has a 1-tick lag time after brewing stops
        isHot = reservoirIsBrewing || cyclesAfterBrewStopped < STAY_HOT_CYCLE_COUNT;

        if (reservoirIsBrewing) {
            cyclesAfterBrewStopped = 0;

        } else if (cyclesAfterBrewStopped < STAY_HOT_CYCLE_COUNT) {
            cyclesAfterBrewStopped++;
        }
    }

    @Override
    public void refreshFrom(WarmerPlate other) {
        this.hasPot = other.hasPot();
        this.isHot = other.isHot();
        // No need to refresh cyclesAfterBrewStopped because this information is not important in a
        // bus message. It's an internal-only value.
    }

    @Override
    public void reset() {
        this.hasPot = true;
        this.isHot = false;
    }

    public void removePot() {
        hasPot = false;
    }

    public void replacePot() {
        hasPot = true;
    }

    public boolean isHot() {
        return isHot;
    }

    public boolean hasPot() {
        return hasPot;
    }
}