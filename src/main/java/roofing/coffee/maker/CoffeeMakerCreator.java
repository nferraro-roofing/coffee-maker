package roofing.coffee.maker;

import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Bus;
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
public class CoffeeMakerCreator {

    public CoffeeMaker create(boolean autoUpdateBus) {
        WaterReservoir reservoir = new WaterReservoir();
        BrewButton button = new BrewButton();
        CoffeePot pot = new CoffeePot();
        WarmerPlate warmer = new WarmerPlate();

        Bus bus = new Bus(reservoir, button, pot, warmer);

        if (autoUpdateBus) {
            Clock.start(bus);
        }

        return new CoffeeMaker(bus, reservoir, button, pot, warmer);
    }
}