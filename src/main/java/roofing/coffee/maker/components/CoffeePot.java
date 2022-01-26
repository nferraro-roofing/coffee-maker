package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

/**
 * Like the WaterReservoir, the CoffeePot also relies on the clock in order to fill itself. However,
 * there is a 1-cycle lag time between WaterReservoir.isBrewing() becoming true and the clock's next
 * tick; thus, the CoffeePot will be 1 cycle behind until 1 cycle after brewing stops. E.g.
 * 
 * <ol>
 * <li>Fill reservoir with 12 cups of water</li>
 * <li>Clock ticks. Reservoir contains 11 cups of water; coffee pot has 0 cups of coffee and has
 * fallen behind by one tick.</li>
 * <li>Clock ticks again. Reservoir contains 10 cups of water; coffee pot has 1 cup of coffee.</li>
 * <li>Pause brew by removing the coffee pot or pressing the brew button.</li>
 * <li>Clock ticks again. Reservoir contains 10 cups of water; coffee pot has 2 cups of coffee. The
 * pot caught up.</li>
 * <li>Resume brew.</li>
 * <li>Clock ticks again. Reservoir contains 9 cups of water; coffee pot has 2 cup of coffee, and
 * has fallen behind again.</li>
 * </ol>
 * 
 * This lag time occurs because the water reservoir must begin brewing in order for the pot to begin
 * to fill. But the pot cannot know that the reservoir is brewing until the cycle after the
 * reservoir begins brewing.
 * 
 * This lag time should be of no real-world consequence. Unit tests against a coffee maker must
 * understand this lag time.
 * 
 * @author nferraro-roofing
 *
 */
public class CoffeePot implements BusComponent<CoffeePot> {

    private final int maxCapacityCups;
    private final long ticksPerCupBrewed;

    private int cupsOfCoffee = 0;
    private long ticksSinceLastCupBrewed = 0;

    /**
     * Create an instance of a CoffeePot to be used as within a bus message. This instance has no
     * capacity.
     * 
     * @return a CoffeePot that has no capacity.
     */
    public static CoffeePot busMessageInstance() {
        return new CoffeePot();
    }

    public CoffeePot(int maxCapacityCups, long ticksPerCupBrewed) {
        this.maxCapacityCups = maxCapacityCups;
        this.ticksPerCupBrewed = ticksPerCupBrewed;
    }

    private CoffeePot() {
        maxCapacityCups = 0;
        ticksPerCupBrewed = 0;
    }

    @Override
    public void readBusMessage(BusMessage message) {
        WaterReservoir waterReservoir = message.getReservoir();

        if (waterReservoir.isBrewing()) {
            ticksSinceLastCupBrewed++;

            if (ticksSinceLastCupBrewed == ticksPerCupBrewed) {
                int nextCupsOfCoffee = cupsOfCoffee + 1;

                if (message.getReservoir().isBrewing() && nextCupsOfCoffee <= maxCapacityCups) {
                    cupsOfCoffee = nextCupsOfCoffee;
                }
            }
        } else if (waterReservoir.isEmpty()) {
            // Only reset state if we have nothing else to brew. Otherwise, we
            // want to be able to return to where we left off - e.g. resume
            // brewing after use removed and replaced the coffee pot
            ticksSinceLastCupBrewed = 0;
        }
    }

    @Override
    public void refreshFrom(CoffeePot other) {
        this.cupsOfCoffee = other.cupsOfCoffee;
    }

    @Override
    public void reset() {
        this.cupsOfCoffee = 0;
    }

    public void pourOutCoffee(int cups) {
        cupsOfCoffee = cups >= cupsOfCoffee ? 0 : cupsOfCoffee - cups;
    }

    public int cupsOfCoffee() {
        return cupsOfCoffee;
    }

    public boolean isFull() {
        // Should never be greater than, but it doesn't hurt to add the check here!
        return cupsOfCoffee >= maxCapacityCups;
    }
}