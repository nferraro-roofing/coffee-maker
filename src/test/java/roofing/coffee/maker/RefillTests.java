package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.CoffeePot;

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

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder);
        clock = clockBuilder.build();
    }

    @Test
    void testFullReBrew() {
        // Fully fill the reservoir & brew all of the coffee
        brew(CoffeePot.MAX_CAPACITY_CUPS);
        assertFillLevel(0, CoffeePot.MAX_CAPACITY_CUPS, true);

        // Fully empty the pot
        empty(CoffeePot.MAX_CAPACITY_CUPS);
        assertFillLevel(0, 0, false);

        // Fully fill the reservoir & brew all of the coffee again
        brew(CoffeePot.MAX_CAPACITY_CUPS);
        assertFillLevel(0, CoffeePot.MAX_CAPACITY_CUPS, true);
    }

    @Test
    void testPartialReBrew() {
        // Fully fill the reservoir & brew all of the coffee
        brew(CoffeePot.MAX_CAPACITY_CUPS);
        assertFillLevel(0, CoffeePot.MAX_CAPACITY_CUPS, true);

        // Empty some of the coffee, but not all of it
        int partialPourCups = 5;
        empty(partialPourCups);
        assertFillLevel(0, CoffeePot.MAX_CAPACITY_CUPS - partialPourCups, false);

        brew(partialPourCups);
        assertFillLevel(0, CoffeePot.MAX_CAPACITY_CUPS, true);
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