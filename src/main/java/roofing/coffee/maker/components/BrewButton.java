package roofing.coffee.maker.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.ToString;
import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

@ToString(includeFieldNames = true)
public class BrewButton implements BusComponent<BrewButton> {

    private static final Logger LOG = LoggerFactory.getLogger(BrewButton.class);

    private BrewRequestState brewState = BrewRequestState.NOT_REQUESTED;

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

    @Override
    public void refreshFrom(BrewButton other) {
        this.brewState = other.state();
    }

    @Override
    public void reset() {
        this.brewState = BrewRequestState.NOT_REQUESTED;
    }

    public void pressBrewButton() {
        // If we are idle, request for brewing.
        // Otherwise, the user must want us to stop brewing.
        LOG.debug("Brew button has been pressed and the current state is: {}", brewState);
        brewState = brewState == BrewRequestState.NOT_REQUESTED
                ? BrewRequestState.REQUESTED
                : BrewRequestState.NOT_REQUESTED;
    }

    public boolean isBrewRequested() {
        return brewState != BrewRequestState.NOT_REQUESTED;
    }

    private BrewRequestState state() {
        return brewState;
    }

    private enum BrewRequestState {

        NOT_REQUESTED, REQUESTED, RECEIVED;

    }
}