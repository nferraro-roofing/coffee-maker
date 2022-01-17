package roofing.coffee.maker.components;

import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

public class CoffeePot implements BusComponent<CoffeePot> {

    // TODO: app setting
    // public in order to enable tests to spy into this value
    public static final int MAX_CAPACITY_CUPS = 12;

    private int cupsOfCoffee = 0;

    @Override
    public void readBusMessage(BusMessage message) {
        if (message.getReservoir().isBrewing()) {
            // If the reservoir does not stop brewing at the appropriate time, it's possible that
            // the pot over fills! The pot cannot stop the reservoir from doing this, of course
            cupsOfCoffee += message.getReservoir().brewRate();
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

    // Cannot do this if the warmer plate says i'm still in the coffee pot!
    public void pourOutCoffee(int cups) {
        cupsOfCoffee = cups >= cupsOfCoffee ? 0 : cupsOfCoffee - cups;
    }

    public int cupsOfCoffee() {
        return cupsOfCoffee;
    }

    public boolean isFull() {
        // Should never be greater than, but it doesn't hurt to add the check here!
        return cupsOfCoffee >= MAX_CAPACITY_CUPS;
    }
}