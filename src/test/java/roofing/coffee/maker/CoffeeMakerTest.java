package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Bus;
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
class CoffeeMakerTest {

    @Test
    void testHappyPathBrew() {
        // Given
        Bus bus = new Bus();
        CoffeeMaker subject = CoffeeMakerCreator.create(bus);
        
        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS; i++) {
            bus.update();
        }

        // Then
        assertEquals(0, subject.cupsOfWater());
        assertEquals(0, subject.cupsOfCoffee());
        assertTrue(subject.isBrewComplete());
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, subject.removePot().cupsOfCoffee());
    }

    @Test
    void testIsBrewComplete() {
        assertTrue(true);
        // Coffee pot full
        // No more water
        // Press the button again
        // Remove the pot
    }

}