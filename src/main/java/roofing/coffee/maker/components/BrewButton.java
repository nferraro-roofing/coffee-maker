package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

public class BrewButton implements BusComponent<BrewButton> {

    private BrewRequestState brewState = BrewRequestState.NOT_REQUESTED;

    @Override
    public void readBusMessage(BusMessage message) {
        if (brewState == BrewRequestState.REQUESTED && message.getReservoir().isBrewing()) {
            brewState = BrewRequestState.RECEIVED;

        } else if (brewState == BrewRequestState.RECEIVED) {
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