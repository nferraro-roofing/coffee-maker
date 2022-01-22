package roofing.coffee.maker;

import roofing.coffee.maker.busses.Bus;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties;

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

    public static final CoffeeMaker create(CoffeeMakerProperties properties) {
        ClockBuilder clockBuilder = Clock.builder();
        CoffeeMaker coffeeMaker = create(clockBuilder, properties);

        Clock clockProps = properties.getClockProperties();
        clockBuilder.build().start(clockProps.getTickDelay(), clockProps.getDelayUnit());
        return coffeeMaker;
    }

    static final CoffeeMaker create(ClockBuilder clockBuilder, CoffeeMakerProperties properties) {
        WaterReservoir reservoir = new WaterReservoir();
        BrewButton button = new BrewButton();
        CoffeePot pot = new CoffeePot(properties.getPotProperties().getMaxCapacityCups());
        WarmerPlate warmer = new WarmerPlate();

        Bus bus = new Bus(reservoir, button, pot, warmer);
        CoffeeMaker coffeeMaker = new CoffeeMaker(reservoir, button, pot, warmer);

        clockBuilder.withBus(bus).withCoffeeMaker(coffeeMaker);

        return coffeeMaker;
    }
}