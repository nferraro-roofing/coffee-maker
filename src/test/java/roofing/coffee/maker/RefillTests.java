package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps;

/**
 * TODO: complete docs
 * 
 * difference between this and real usage: normally, we don't allow clients of coffee-maker to use a
 * Bus directly; instead, we use the Clock to automatically update the Bus in a different thread.
 * However, we must control the bus here so that we can traverse known execution paths.
 * 
 * @author nferraro-roofing
 *
 */
class RefillTests {

    private static CoffeeMakerProperties props;

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeAll
    static void initProps() {
        ClockProps clock = new ClockProps(60L, TimeUnit.SECONDS);
        PotProps pot = new PotProps(10);
        ReservoirProps reservoir = new ReservoirProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(10);

        // When
        props = new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);
    }

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder, props);
        clock = clockBuilder.build();
    }

    @Test
    void testFullReBrew() {
        // Fully fill the reservoir & brew all of the coffee
        brew(subject.getMaxWaterCapacityCups());
        assertFillLevel(0, props.getPotMaxCapacityCups(), true);

        // Fully empty the pot
        empty(subject.getMaxWaterCapacityCups());
        assertFillLevel(0, 0, false);

        // Fully fill the reservoir & brew all of the coffee again
        brew(subject.getMaxWaterCapacityCups());
        assertFillLevel(0, props.getPotMaxCapacityCups(), true);
    }

    @Test
    void testPartialReBrew() {
        // Fully fill the reservoir & brew all of the coffee
        brew(subject.getMaxWaterCapacityCups());
        assertFillLevel(0, props.getPotMaxCapacityCups(), true);

        // Empty some of the coffee, but not all of it
        int partialPourCups = 5;
        empty(partialPourCups);
        assertFillLevel(0, props.getPotMaxCapacityCups() - partialPourCups, false);

        brew(partialPourCups);
        assertFillLevel(0, props.getPotMaxCapacityCups(), true);
    }

    private void brew(int initialFill) {
        subject.fill(initialFill);
        subject.pressBrewButton();

        // + 2 in order to avoid any lag time in state propagation to coffee pot / brew button
        for (int i = 0; i < initialFill + 2; i++) {
            clock.tick();
        }
    }

    private void empty(int cupsToEmpty) {
        CoffeePot pot = subject.removePot();
        pot.pourOutCoffee(cupsToEmpty);
        subject.replacePot();
    }


    private void assertFillLevel(int cupsOfWater, int cupsOfCoffee, boolean isPotFull) {
        assertEquals(cupsOfWater, subject.cupsOfWater());

        CoffeePot actualPot = subject.removePot();
        assertEquals(cupsOfCoffee, subject.cupsOfCoffee());
        assertEquals(cupsOfCoffee, actualPot.cupsOfCoffee());
        assertEquals(isPotFull, actualPot.isFull());

        // Reset the pot in case of subsequent re-fills and re-brews
        subject.replacePot();
    }

    // TODO: Run test coverage
}