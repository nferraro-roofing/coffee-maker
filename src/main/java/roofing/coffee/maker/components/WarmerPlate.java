package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

public class WarmerPlate implements BusComponent<WarmerPlate> {

    private boolean hasPot = true;
    private boolean isHot = false;

    @Override
    public void readBusMessage(BusMessage message) {
        // No need to check if the pot is actually there. The WaterReservoir
        // should not brew coffee if the pot is not present!
        isHot = message.getReservoir().isBrewing();
    }

    @Override
    public void refreshFrom(WarmerPlate other) {
        this.hasPot = other.hasPot();
        this.isHot = other.isHot();
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