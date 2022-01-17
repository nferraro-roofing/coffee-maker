package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class CoffeeMakerTest {

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder);
        clock = clockBuilder.build();
    }

    @Test
    void testBrewAllWater() {
        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // + 1 because CoffeePot has a one clock cycle lag time - see CoffeePot javadoc
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS + 1; i++) {
            clock.tick();
        }

        // Then
        assertEquals(0, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, subject.cupsOfCoffee());
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, subject.removePot().cupsOfCoffee());
        assertTrue(subject.removePot().isFull());
    }

    @Test
    void testStopBrewByPressingButton() {
        // Given
        int clockCycleOffset = 5;

        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // Partially brew
        for (int i = 0; i < clockCycleOffset; i++) {
            clock.tick();
        }

        subject.pressBrewButton();

        // Try and finish brewing. Since the brew button was pressed again, this shouldn't do
        // anything
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset; i++) {
            clock.tick();
        }

        // Then
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        assertEquals(clockCycleOffset, subject.cupsOfCoffee());
        assertEquals(clockCycleOffset, subject.removePot().cupsOfCoffee());
        assertFalse(subject.removePot().isFull());
    }

    @Test
    void assertStateDuringBrew() {}
}