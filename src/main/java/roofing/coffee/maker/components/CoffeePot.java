package roofing.coffee.maker.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.ToString;
import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

/**
 * CoffeePot holds coffee, and enables the user to ultimately acquire this precious nectar.
 * 
 * <p>
 * A user may acquire some Coffee from the CoffeePot by first removing the CoffeePot from the
 * CoffeeMaker and then pouring out some cups of coffee via {@code pourOutCoffee(int)}.
 * </p>
 * 
 * <p>
 * CoffeePot lives in leagues with other CoffeeMaker "components". These components encapsulate one
 * portion of the logic which comprises a CoffeeMaker. All such components implement the
 * BusComponent interface, thus enabling them to inter-communicate. Similarly, each such component
 * also advertises a few custom, public methods that belong to no interface. These methods represent
 * actions that a CoffeeMaker exposes to the user, or data that a BusComponent may want to advertise
 * to other BusComponents.
 * </p>
 * 
 * <p>
 * CoffeePot advertises the following methods for usage by a user:
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.CoffeePot#pourOutCoffee(int)}</li>
 * <li>{@link roofing.coffee.maker.components.CoffeePot#cupsOfCoffee()}</li>
 * <li>{@link roofing.coffee.maker.components.CoffeePot#isFull()}</li>
 * </ul>
 * 
 * <p>
 * CoffeePot advertises data to other BusComponents via
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.CoffeePot#cupsOfCoffee()}</li>
 * <li>{@link roofing.coffee.maker.components.CoffeePot#isFull()}</li>
 * </ul>
 * 
 * @see roofing.coffee.maker.busses.BusComponent
 * 
 * @author nferraro-roofing
 *
 */
@ToString(includeFieldNames = true)
public class CoffeePot implements BusComponent<CoffeePot> {

    private static final Logger LOG = LoggerFactory.getLogger(CoffeePot.class);

    /*
     * maxCapacityCups and ticksPerCupBrewed are settings (see CoffeeMakerProperties and
     * CoffeeMakerCreator) and should therefore be final. However, instances of WarmerPlate that are
     * intended for use a BusMessage won't know this value (see busMessageInstance()). As a result,
     * the only way for such instances to know about this value is from other instances
     * post-creation time in refreshFrom() - thus preventing this value from being final.
     */
    private int maxCapacityCups;
    private long ticksPerCupBrewed;
    private int cupsOfCoffee = 0;
    private long ticksSinceLastCupBrewed = 0;

    /**
     * Create an instance of a CoffeePot to be used as within a bus message.
     * 
     * <p>
     * This instance has no initial capacity. It copies the capacity of any CoffeePot provided as an
     * argument to {@code refreshFrom(CoffeePot)}.
     * </p>
     * 
     * @return a CoffeePot that has no capacity.
     */
    public static CoffeePot busMessageInstance() {
        return new CoffeePot();
    }

    /**
     * Create an instance of a CoffeePot to be used by a CoffeeMaker and interact with a user.
     * 
     * <p>
     * This instance has an initial capacity. However, it also copies the capacity of any CoffeePot
     * provided as an argument to {@code refreshFrom(CoffeePot)}. Instances of CoffeePot that react
     * to {@code refreshFrom(CoffeePot)} should likely use the no-arg constructor (via
     * {@code busMessageInstance()} but this constructor poses no threat.
     * </p>
     * 
     * <p>
     * This instance adds one cup of coffee to itself after {@code tickPerCupBrewed} consecutive
     * calls to {@code readBusMessage()} (i.e. ticks of the clock) while brewing. Take care, as
     * {@code refreshFrom(CoffeePot)} resets {@code tickPerCupBrewed} to match the other CoffeePot.
     * </p>
     * 
     * @param maxCapacityCups the max number of cups of coffee that this CoffeePot can hold
     * @param ticksPerCupBrewed the ticks of the clock while brewing required to add one cup of
     *        Coffee to the pot.
     */
    public CoffeePot(int maxCapacityCups, long ticksPerCupBrewed) {
        this.maxCapacityCups = maxCapacityCups;
        this.ticksPerCupBrewed = ticksPerCupBrewed;
    }

    private CoffeePot() {
        maxCapacityCups = 0;
        ticksPerCupBrewed = 0;
    }

    /**
     * Update the CoffeePot's internal state based on the provided {@code message}.
     * 
     * <p>
     * CoffeePot adds coffee to itself after {@code tickPerCupBrewed} (provided at construction
     * time) consecutive calls to {@code readBusMessage()} (i.e. ticks of the clock) while brewing.
     * The CoffeePot refers to the WaterReservoir within the provided BusMessage to determine if the
     * CoffeeMaker is brewing coffee or not.
     * </p>
     * 
     * @param message a snapshot of the CoffeeMaker's other BusComponents.
     */
    @Override
    public void readBusMessage(BusMessage message) {
        WaterReservoir waterReservoir = message.getReservoir();

        if (waterReservoir.isBrewing()) {
            LOG.trace("Increment pot's clock tick counter ({}) by 1. Ticks required to reset "
                    + "and brew a cup of coffee: {}",
                    ticksSinceLastCupBrewed,
                    maxCapacityCups);
            ticksSinceLastCupBrewed++;

            if (ticksSinceLastCupBrewed == ticksPerCupBrewed) {
                ticksSinceLastCupBrewed = 0;
                int nextCupsOfCoffee = cupsOfCoffee + 1;

                if (nextCupsOfCoffee <= maxCapacityCups) {
                    LOG.debug("Brewed a cup of coffee! The coffee pot's current level is now {}",
                            nextCupsOfCoffee);

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

    /**
     * Set this CoffeePot's internal state to match that of {@code other}.
     * 
     * @param other the other CoffeePot to copy
     */
    @Override
    public void refreshFrom(CoffeePot other) {
        this.cupsOfCoffee = other.cupsOfCoffee;
        this.maxCapacityCups = other.maxCapacityCups;
        this.ticksPerCupBrewed = other.ticksPerCupBrewed;
    }

    /**
     * Reset this CoffeePot internal state back to its initial state.
     * 
     */
    @Override
    public void reset() {
        // Purposefully omit maxCapacityCups & ticksPerCupBrewed, as they are intended as
        // application properties and do not really reflect internal state.
        this.cupsOfCoffee = 0;
    }

    /**
     * Enable the user to pour out {@code cups} of coffee.
     * 
     * <p>
     * If {@code cups} exceeds the CoffeePot's current capacity, simply empty the CoffeePot
     * completely.
     * </p>
     * 
     * @param cups the cups of coffee to pour out
     */
    public void pourOutCoffee(int cups) {
        cupsOfCoffee = cups >= cupsOfCoffee ? 0 : cupsOfCoffee - cups;
        LOG.debug("Pouring out {} cups of coffee from the pot. New cups: {}", cups, cupsOfCoffee);
    }

    /**
     * Returns the current cups of coffee that reside within this CoffeePot
     * 
     * @return the current cups of coffee that reside within this CoffeePot
     */
    public int cupsOfCoffee() {
        return cupsOfCoffee;
    }

    /**
     * Returns true when this CoffeePot currently holds its max capacity of coffee.
     * 
     * <p>
     * A CoffeePot's maximum capacity is set at construction time.
     * </p>
     * 
     * @return true when this CoffeePot currently holds its max capacity of coffee; false otherwise.
     */
    public boolean isFull() {
        // Should never be greater than, but it doesn't hurt to add the check here!
        return cupsOfCoffee >= maxCapacityCups;
    }
}