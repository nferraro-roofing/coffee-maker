package roofing.coffee.maker;

import roofing.coffee.maker.busses.Bus;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

/**
 * A creator of CoffeeMaker instances.
 * 
 * CoffeeMakerCreator purposefully does not provide a default constructor. Doing so forces clients
 * of this package to consider the ramifications of different Clocks. Clock speed and behavior which
 * is non-trivial consideration and can affect the performance and correctness of a CoffeeMaker
 * client.
 * 
 * @author nferraro-roofing
 *
 */
public final class CoffeeMakerCreator {

    private CoffeeMakerCreator() { /* Disable construction */ }

    public static final CoffeeMaker create() {
        Bus bus = new Bus();
        CoffeeMaker ret = create(bus);
        Clock.start(bus);
        return ret;
    }

    static final CoffeeMaker create(Bus bus) {
        WaterReservoir reservoir = new WaterReservoir();
        BrewButton button = new BrewButton();
        CoffeePot pot = new CoffeePot();
        WarmerPlate warmer = new WarmerPlate();

        bus.synchBusComponents(reservoir, button, pot, warmer);

        return new CoffeeMaker(bus, reservoir, button, pot, warmer);
    }
}