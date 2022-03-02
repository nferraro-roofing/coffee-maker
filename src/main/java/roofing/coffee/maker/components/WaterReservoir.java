package roofing.coffee.maker.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.ToString;
import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

/**
 * WaterReservoir holds coffee, and enables the user to ultimately acquire this precious nectar.
 * 
 * <p>
 * A user must fill the WaterReservoir with at least one cup of coffee before brewing may begin via
 * {@code fill(int)}.
 * </p>
 * 
 * <p>
 * WaterReservoir lives in leagues with other CoffeeMaker "components". These components encapsulate
 * one portion of the logic which comprises a CoffeeMaker. All such components implement the
 * BusComponent interface, thus enabling them to inter-communicate. Similarly, each such component
 * also advertises a few custom, public methods that belong to no interface. These methods represent
 * actions that a CoffeeMaker exposes to the user, or data that a BusComponent may want to advertise
 * to other BusComponents.
 * </p>
 * 
 * <p>
 * WaterReservoir advertises the following methods for usage by a user:
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.WaterReservoir#fill(int)}</li>
 * <li>{@link roofing.coffee.maker.components.WaterReservoir#maxCapacityCups()}</li>
 * </ul>
 * 
 * <p>
 * WaterReservoir advertises data to other BusComponents via
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.WaterReservoir#cupsOfWater()}</li>
 * <li>{@link roofing.coffee.maker.components.WaterReservoir#isBrewing()}</li>
 * <li>{@link roofing.coffee.maker.components.WaterReservoir#isEmpty()}</li>
 * </ul>
 * 
 * @see roofing.coffee.maker.busses.BusComponent
 * 
 * @author nferraro-roofing
 *
 */
@ToString(includeFieldNames = true)
public class WaterReservoir implements BusComponent<WaterReservoir> {

    private static final Logger LOG = LoggerFactory.getLogger(WaterReservoir.class);
    private static final int COFFEE_POT_MAX_CAPACITY_OFFSET = 1;

    private final long ticksPerCupBrewed;

    /*
     * maxCapacityCups is an app setting (see CoffeeMakerProperties and CoffeeMakerCreator).
     * Therefore, it should be final. However, instances of WarmerPlate that are intended for use a
     * BusMessage won't know this value (see busMessageInstance()). As a result, the only way for
     * such instances to know about this value is from other instances post-creation time in
     * refreshFrom() - thus preventing this value from being final.
     */
    private int maxCapacityCups;
    private int cupsOfWater = 0;
    private boolean isBrewing = false;
    private long ticksSinceLastCupBrewed = 0;

    /**
     * Create an instance of a WaterReservoir to be used as within a bus message. T
     * 
     * <p>
     * This instance has no capacity and cannot brew any coffee. However, it copies the capacity of
     * any WaterReservoir provided as an argument to {@code refreshFrom(WaterReservoir)}.
     * </p>
     * 
     * @return a WaterReservoir that has no capacity and cannot brew any coffee.
     */
    public static WaterReservoir busMessageInstance() {
        return new WaterReservoir();
    }

    /**
     * Create an instance of a WaterReservoir to be used by a CoffeeMaker and interact with a user.
     * 
     * <p>
     * This instance has an initial capacity. However, it also copies the capacity of any
     * WaterReservoir provided as an argument to {@code refreshFrom(WaterReservoir)}. Instances of
     * WaterReservoir that react to {@code refreshFrom(WaterReservoir)} should likely use the no-arg
     * constructor (via {@code busMessageInstance()} instead, but this constructor poses no threat.
     * </p>
     * 
     * <p>
     * This constructor expresses this instance's max capacity in terms of its corresponding
     * CoffeePot's max capacity. In other words, {@code potMaxCapacityCups} is the max capacity of
     * the CoffeePot that will hold this WaterReservoir's brewed coffee. At construction time,
     * WaterReservoir adds an offset to this value. This offset is necessary because a CoffeeMaker
     * never brews 100% of its water into Coffee - it always loses some to evaporation (or ghosts?).
     * </p>
     * 
     * <p>
     * This instance removes one cup of water from itself after {@code tickPerCupBrewed} consecutive
     * calls to {@code readBusMessage()} (i.e. ticks of the clock) while brewing. Take care, as
     * {@code refreshFrom(WaterReservoir)} resets {@code tickPerCupBrewed} to match the other
     * WaterReservoir.
     * </p>
     * 
     * @param potMaxCapacityCups the max number of cups of <i>coffee</i> that this WaterReservoir's
     *        CoffeePot can hold
     * @param ticksPerCupBrewed the ticks of the clock while brewing required to remove one cup of
     *        water from to the reservoir.
     */
    public WaterReservoir(int potMaxCapacityCups, long ticksPerCupBrewed) {
        this.maxCapacityCups = potMaxCapacityCups + COFFEE_POT_MAX_CAPACITY_OFFSET;
        this.ticksPerCupBrewed = ticksPerCupBrewed;
    }

    private WaterReservoir() {
        this.maxCapacityCups = 0;
        this.ticksPerCupBrewed = 0;
    }
    
    /**
     * Update the WaterReservoir internal state based on the provided {@code message}.
     * 
     * <p>
     * WaterReservoir removes water from itself after {@code tickPerCupBrewed} (provided at
     * construction time) consecutive calls to {@code readBusMessage()} (i.e. ticks of the clock)
     * while brewing.
     * </p>
     * 
     * <p>
     * A WaterReservoir brews coffee when its is not empty, its corresponding CoffeePot is not full,
     * and the user has requested coffee by pressing the BrewButton.
     * </p>
     * 
     * @param message a snapshot of the CoffeeMaker's other BusComponents.
     */
    @Override
    public void readBusMessage(BusMessage message) {
        isBrewing = message.getButton().isBrewRequested()
                && message.getWarmer().hasPot()
                && !message.getPot().isFull()
                && !isEmpty();

        if (isBrewing) {
            LOG.trace("Increment reservoir's clock tick counter ({}) by 1. Ticks required to reset "
                    + "and remove a cup of water: {}", ticksSinceLastCupBrewed, ticksPerCupBrewed);

            ticksSinceLastCupBrewed++;

            if (ticksSinceLastCupBrewed == ticksPerCupBrewed) {
                cupsOfWater--;
                ticksSinceLastCupBrewed = 0;
            }
        } else if (isEmpty()) { // Only reset state if we have nothing else to brew. Otherwise, we
                                // want to be able to return to where we left off - e.g. resume
                                // brewing after use removed and replaced the coffee pot
            ticksSinceLastCupBrewed = 0;
        }
    }

    /**
     * Set this WaterReservoir's internal state to match that of {@code other}.
     * 
     * @param other the other WaterReservoir to copy
     */
    @Override
    public void refreshFrom(WaterReservoir other) {
        this.cupsOfWater = other.cupsOfWater;
        this.isBrewing = other.isBrewing;
        this.maxCapacityCups = other.maxCapacityCups;
    }

    /**
     * Reset this WaterReservoir internal state back to its initial state.
     * 
     */
    @Override
    public void reset() {
        // Purposefully omit maxCapacityCups & ticksPerCupBrewed, as they are intended as
        // application properties and do not really reflect internal state.
        cupsOfWater = 0;
        isBrewing = false;
    }

    /**
     * Enable the user to fill water into this WaterReservoir.
     * 
     * <p>
     * Add {@code cupsOfwater} to this WaterReservoir's current cups of water. If doing so would
     * over-fill the WaterReservoir (exceed {@code maxCapcityCups} - provided at construction time),
     * then throw an IllegalArgumentException.
     * </p>
     * 
     * @param cupsOfwater the number of cups of water to fill into this WaterReservoir
     * @throws IllegalArgumentException when over-filled
     */
    public void fill(int cupsOfwater) {
        LOG.debug("Filling the water reservoir with {} cups of water. Current level: {}",
                cupsOfwater,
                this.cupsOfWater);
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

    /**
     * Returns the cups of water that currently reside within this WaterReservoir.
     * 
     * @return the cups of water that currently reside within this WaterReservoir.
     */
    public int cupsOfWater() {
        return cupsOfWater;
    }

    /**
     * Returns true if this WaterReservoir is brewing coffee; false otherwise.
     * 
     * <p>
     * A WaterReservoir brews coffee when its is not empty, its corresponding CoffeePot is not full,
     * and the user has requested coffee by pressing the BrewButton.
     * </p>
     * 
     * @return true if this WaterReservoir is brewing coffee; false otherwise.
     */
    public boolean isBrewing() {
        return isBrewing;
    }

    /**
     * Returns the maximum cups of water that this WaterReservoir may hold.
     * 
     * @return the maximum cups of water that this WaterReservoir may hold.
     */
    public int maxCapacityCups() {
        return maxCapacityCups;
    }

    /**
     * Return true if this WaterReservoir does not contain any water; false otherwise.
     * 
     * @return true if this WaterReservoir does not contain any water; false otherwise.
     */
    public boolean isEmpty() {
        // This really should never go negative, but it doesn't hurt to check!
        return cupsOfWater <= 0;
    }
}
