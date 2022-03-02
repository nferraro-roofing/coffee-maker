package roofing.coffee.maker.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.ToString;
import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

/**
 * BrewButton enables a user to instruct his or her CoffeeMaker to brew some Coffee.
 * 
 * <p>
 * When a user presses the BrewButton and the CoffeeMaker is not brewing Coffee, he/she instructs
 * the CoffeeMaker to brew all of its water into Coffee. The user may halt the brewing process by
 * pressing the button again before the CoffeeMaker finishes brewing. "Instruct" is a keyword here.
 * The CoffeeMaker will start brewing only if all components involved agree that it can. The
 * BrewButton is only one such component; additionally, the CoffeePot must not be full and the
 * WaterReservoir must contain water.
 * </p>
 * 
 * <p>
 * BrewButton lives in leagues with other CoffeeMaker "components". These components encapsulate one
 * portion of the logic which comprises a CoffeeMaker. All such components implement the
 * BusComponent interface, thus enabling them to inter-communicate. Similarly, each such component
 * also advertises a few custom, public methods that belong to no interface. These methods represent
 * actions that a CoffeeMaker exposes to the user, or data that a BusComponent may want to advertise
 * to other BusComponents.
 * </p>
 * 
 * <p>
 * BrewButton advertises the following methods for usage by a user:
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.BrewButton#pressBrewButton()}</li>
 * </ul>
 * 
 * <p>
 * BrewButton advertises data to other BusComponents via
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.BrewButton#isBrewRequested()}</li>
 * </ul>
 * 
 * @see roofing.coffee.maker.busses.BusComponent
 * @see roofing.coffee.maker.components.CoffeePot
 * @see roofing.coffee.maker.components.WaterReservoir
 * @author nferraro-roofing
 *
 */
@ToString(includeFieldNames = true)
public class BrewButton implements BusComponent<BrewButton> {

    private static final Logger LOG = LoggerFactory.getLogger(BrewButton.class);

    private BrewRequestState brewState = BrewRequestState.NOT_REQUESTED;

    /**
     * Update the BrewButton's internal state based on the provided {@code message}.
     * 
     * <p>
     * BrewButton traverses three states - requested, received, and not requested. This scheme
     * enables the BrewButton to determine the difference between a user who wants to start a brew
     * vs a user who wants to pause a brew.
     * </p>
     * 
     * @param message a snapshot of the CoffeeMaker's other BusComponents.
     */
    @Override
    public void readBusMessage(BusMessage message) {
        if (brewState == BrewRequestState.REQUESTED && message.getReservoir().isBrewing()) {
            LOG.debug("Brew requested and WaterReservoir is brewing. "
                    + "Set BrewButton's state to RECEIVED");

            brewState = BrewRequestState.RECEIVED;

        } else if (brewState == BrewRequestState.RECEIVED && !message.getReservoir().isBrewing()) {
            LOG.debug("Brew request received and WaterReservoir is NOT brewing. "
                    + "Re-setting BrewButton's state to NOT_REQUESTED");

            brewState = BrewRequestState.NOT_REQUESTED;
        }
    }

    /**
     * Set this BrewButton's internal state to match that of {@code other}.
     * 
     * @param other the other BrewButton to copy
     */
    @Override
    public void refreshFrom(BrewButton other) {
        this.brewState = other.state();
    }

    /**
     * Reset this BrewButton's internal state back to its initial state.
     * 
     */
    @Override
    public void reset() {
        this.brewState = BrewRequestState.NOT_REQUESTED;
    }

    /**
     * Enable the user to request that the CoffeeMaker brew some Coffee.
     * 
     */
    public void pressBrewButton() {
        // If we are idle, request for brewing.
        // Otherwise, the user must want us to stop brewing.
        LOG.debug("Brew button has been pressed and the current state is: {}", brewState);
        brewState = brewState == BrewRequestState.NOT_REQUESTED
                ? BrewRequestState.REQUESTED
                : BrewRequestState.NOT_REQUESTED;
    }

    /**
     * Returns true if the user has instructed the CoffeeMaker to brew some coffee; returns false
     * otherwise.
     * 
     * @return true if the user has instructed the CoffeeMaker to brew some coffee; false otherwise.
     */
    public boolean isBrewRequested() {
        return brewState != BrewRequestState.NOT_REQUESTED;
    }

    /*
     * Called by other BrewButton instances, hence the private protection level.
     */
    private BrewRequestState state() {
        return brewState;
    }

    private enum BrewRequestState {

        NOT_REQUESTED, REQUESTED, RECEIVED;

    }
}