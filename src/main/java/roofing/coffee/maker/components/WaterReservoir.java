package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

public class WaterReservoir implements BusComponent<WaterReservoir> {

    private final int maxCapacityCups;
    private final long ticksPerCupBrewed;

    private int cupsOfWater = 0;
    private boolean isBrewing = false;
    private long ticksSinceLastCupBrewed = 0;

    /**
     * Create an instance of a WaterReservoir to be used as within a bus message. This instance has
     * no capacity and cannot brew any coffee.
     * 
     * @return a WaterReservoir that has no capacity and cannot brew any coffee.
     */
    public static WaterReservoir busMessageInstance() {
        return new WaterReservoir();
    }

    public WaterReservoir(int maxCapacityCups, long ticksPerCupBrewed) {
        this.maxCapacityCups = maxCapacityCups;
        this.ticksPerCupBrewed = ticksPerCupBrewed;
    }

    private WaterReservoir() {
        this.maxCapacityCups = 0;
        this.ticksPerCupBrewed = 0;
    }
    
    @Override
    public void readBusMessage(BusMessage message) {
        isBrewing = message.getButton().isBrewRequested()
                && message.getWarmer().hasPot()
                && !message.getPot().isFull()
                && !isEmpty();

        if (isBrewing) {
            ticksSinceLastCupBrewed++;

            if (ticksSinceLastCupBrewed == ticksPerCupBrewed) {
                cupsOfWater++;
            }
        } else if (isEmpty()) { // Only reset state if we have nothing else to brew. Otherwise, we
                                // want to be able to return to where we left off - e.g. resume
                                // brewing after use removed and replaced the coffee pot
            ticksSinceLastCupBrewed = 0;
        }
    }

    @Override
    public void refreshFrom(WaterReservoir other) {
        this.cupsOfWater = other.cupsOfWater;
        this.isBrewing = other.isBrewing;
    }

    @Override
    public void reset() {
        cupsOfWater = 0;
        isBrewing = false;
    }

    public void fill(int cupsOfwater) {
        int nextCupsOfWater = this.cupsOfWater + cupsOfwater;

        if (nextCupsOfWater > maxCapacityCups) {
            throw new IllegalArgumentException(
                    String.format(
                            "Filling %1d cups of water would overfill the reservoir. The reservoir "
                                    + "currently contains %2d cups of water, and the max total "
                                    + "capacity is %3d cups.",
                            cupsOfwater,
                            this.cupsOfWater,
                            maxCapacityCups));
        }

        this.cupsOfWater += cupsOfwater;
    }

    public int cupsOfWater() {
        return cupsOfWater;
    }

    public boolean isBrewing() {
        return isBrewing;
    }

    public boolean isEmpty() {
        // This really should never go negative, but it doesn't hurt to check!
        return cupsOfWater <= 0;
    }
}
