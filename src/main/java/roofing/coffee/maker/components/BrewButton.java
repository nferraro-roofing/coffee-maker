package roofing.coffee.maker.components;

public class BrewButton implements ClockedComponent {

    private final WaterReservoir reservoir;

    public BrewButton(WaterReservoir reservoir) {
        this.reservoir = reservoir;
    }

    private BrewRequestState brewState = BrewRequestState.NOT_REQUESTED;

    public void pressBrewButton() {
        // If we are idle, request for brewing.
        // Otherwise, the user must want us to stop brewing.
        brewState = brewState == BrewRequestState.NOT_REQUESTED
                ? BrewRequestState.REQUESTED
                : BrewRequestState.NOT_REQUESTED;
    }

    @Override
    public void update() {
        if (brewState == BrewRequestState.REQUESTED && reservoir.isBrewing()) {
            brewState = BrewRequestState.RECEIVED;
            
        } else if (brewState == BrewRequestState.RECEIVED) {
            brewState = BrewRequestState.NOT_REQUESTED;
        }
    }

    private enum BrewRequestState {

        NOT_REQUESTED, REQUESTED, RECEIVED;

    }
}