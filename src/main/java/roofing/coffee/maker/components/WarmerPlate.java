package roofing.coffee.maker.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.ToString;
import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

/**
 * WarmerPlate holds a CoffeePot and warms the coffee residing therein.
 * 
 * <p>
 * A user may not affect a WarmerPlate (it's hot!). A WarmerPlate's internal state reacts only to
 * other CoffeeMaker component state changes. However, the user may check the WarmerPlate to
 * determine if it's hot or not.
 * </p>
 * 
 * <p>
 * WarmerPlate lives in leagues with other CoffeeMaker "components". These components encapsulate
 * one portion of the logic which comprises a CoffeeMaker. All such components implement the
 * BusComponent interface, thus enabling them to inter-communicate. Similarly, each such component
 * also advertises a few custom, public methods that belong to no interface. These methods represent
 * actions that a CoffeeMaker exposes to the user, or data that a BusComponent may want to advertise
 * to other BusComponents.
 * </p>
 * 
 * <p>
 * WarmerPlate advertises the following methods for usage by a user:
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.WarmerPlate#isHot()}</li>
 * </ul>
 * 
 * <p>
 * WarmerPlate advertises data to other BusComponents and the CoffeeMaker itself via
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.WarmerPlate#hasPot()}</li>
 * <li>{@link roofing.coffee.maker.components.WarmerPlate#removePot()}</li>
 * <li>{@link roofing.coffee.maker.components.WarmerPlate#replacePot()}</li>
 * </ul>
 * 
 * @see roofing.coffee.maker.busses.BusComponent
 * 
 * @author nferraro-roofing
 *
 */
@ToString(includeFieldNames = true)
public class WarmerPlate implements BusComponent<WarmerPlate> {

    private static final Logger LOG = LoggerFactory.getLogger(WarmerPlate.class);

    /*
     * stayHotTickLimit is an app setting (see CoffeeMakerProperties and CoffeeMakerCreator).
     * Therefore, it should be final. However, instances of WarmerPlate that are intended for use a
     * BusMessage won't know this value (see busMessageInstance()). As a result, the only way for
     * such instances to know about this value is from other instances post-creation time in
     * refreshFrom() - thus preventing this value from being final.
     */
    private long stayHotTickLimit;

    private int cyclesAfterBrewStopped = 0;
    private boolean hasPot = true;
    private boolean isHot = false;

    /**
     * Create an instance of a WarmerPlate to be used as within a bus message.
     * 
     * <p>
     * This instance does not stay hot after brewing completes, as this behavior depends on a
     * constructor value - {@code stayHotTickLimit}. However, this instance copies this value from
     * any WarmerPlate provided as an argument to {@code refreshFrom(WarmerPlate)}.
     * </p>
     * 
     * @return a WarmerPlate intended for use within a BusMessage.
     */
    public static WarmerPlate busMessageInstance() {
        return new WarmerPlate();
    }

    /**
     * Create an instance of a WarmerPlate to be used by a CoffeeMaker and display information to
     * the user.
     * 
     * <p>
     * This instance remains hot after brewing completes until {@code stayHotTickLimit} ticks of the
     * clock (calls to {@code readBusMessage()}). This instance copies this value from any
     * WarmerPlate provided as an argument to {@code refreshFrom(WarmerPlate)}.
     * </p>
     * 
     * @param stayHotTickLimit ticks of the clock that elapse after brewing during which the
     *        WarmerPlate remains hot.
     */
    public WarmerPlate(long stayHotTickLimit) {
        this.stayHotTickLimit = stayHotTickLimit;
    }

    private WarmerPlate() {
        this.stayHotTickLimit = 0;
    }

    /**
     * Update the WarmerPlate's internal state based on the provided {@code message}.
     * 
     * <p>
     * The WarmerPlate is always hot while brewing coffee. After brewing completes, this WarmerPlate
     * remains hot until {@code stayHotTickLimit} ticks of the clock elapse (set at construction
     * time).
     * </p>
     * 
     * @param message a snapshot of the CoffeeMaker's other BusComponents.
     */
    @Override
    public void readBusMessage(BusMessage message) {
        boolean reservoirIsBrewing = message.getReservoir().isBrewing();

        // < instead of <= because WarmerPlate naturally has a 1-tick lag time after brewing stops
        isHot = reservoirIsBrewing || cyclesAfterBrewStopped < stayHotTickLimit;

        if (reservoirIsBrewing) {
            cyclesAfterBrewStopped = 0;

        } else if (cyclesAfterBrewStopped < stayHotTickLimit) {
            cyclesAfterBrewStopped++;
        }

        LOG.trace("WarmerPlate after reading a BusMessage: isHot? {}, cyclesAfterBrewStopped? {}",
                isHot,
                cyclesAfterBrewStopped);
    }

    /**
     * Set this WarmerPlate's internal state to match that of {@code other}.
     * 
     * @param other the other WarmerPlate to copy
     */
    @Override
    public void refreshFrom(WarmerPlate other) {
        this.hasPot = other.hasPot();
        this.isHot = other.isHot();
        this.stayHotTickLimit = other.stayHotTickLimit;
        // No need to refresh cyclesAfterBrewStopped because this information is not important in a
        // bus message. It's an internal-only value.
    }

    /**
     * Reset this WarmerPlate internal state back to its initial state.
     * 
     */
    @Override
    public void reset() {
        // Purposefully omit stayHotTickLimit, as it is intended as an application
        // property and does not really reflect internal state.
        this.hasPot = true;
        this.isHot = false;
    }

    /**
     * Remove the CoffeePot from the WarmerPlate.
     * 
     */
    public void removePot() {
        hasPot = false;
    }

    /**
     * Replace the CoffeePot onto the WarmerPlate.
     * 
     */
    public void replacePot() {
        hasPot = true;
    }

    /**
     * Returns true if the WarmerPlate is hot; false otherwise.
     * 
     * @return true if the WarmerPlate is hot; false otherwise.
     */
    public boolean isHot() {
        return isHot;
    }

    /**
     * Returns true if a CoffeePot resides atop the WarmerPlate; false otherwise.
     * 
     * <p>
     * Calls to {@code removePot()} and {@code replacePot()} respectively remove and replace the
     * CoffeePot from the WarmerPlate.
     * </p>
     * 
     * @return true if a CoffeePot resides atop the WarmerPlate; false otherwise.
     */
    public boolean hasPot() {
        return hasPot;
    }
}