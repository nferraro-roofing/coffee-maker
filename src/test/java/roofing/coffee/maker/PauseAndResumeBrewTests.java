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
class PauseAndResumeBrewTests {

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder);
        clock = clockBuilder.build();
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

        // Pause brew
        subject.pressBrewButton();

        // Try and finish brewing. Since the brew button was pressed again, this shouldn't do
        // anything
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset; i++) {
            clock.tick();
        }

        // Then
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        CoffeePot actualPot = subject.removePot();
        assertEquals(clockCycleOffset, subject.cupsOfCoffee());
        assertEquals(clockCycleOffset, actualPot.cupsOfCoffee());
        assertFalse(actualPot.isFull());
    }

    @Test
    void testResumeBrewAfterPause() {
        // Given
        int initialBrewCycles = 5;
        int secondBrewCycles = 4;

        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // Partially brew
        for (int i = 0; i < initialBrewCycles; i++) {
            clock.tick();
        }

        // Pause brew
        subject.pressBrewButton();

        // Try and finish brewing. Since the brew button was pressed again, this shouldn't do
        // anything
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS - initialBrewCycles; i++) {
            clock.tick();
        }

        // Resume brew
        subject.pressBrewButton();

        // Brew a partial amount again. Only brew a partial amount in order to differentiate the
        // effect of these ticks vs the intended non-effect of the previous ticks.
        for (int i = 0; i < secondBrewCycles; i++) {
            clock.tick();
        }

        // Then
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS - initialBrewCycles - secondBrewCycles,
                subject.cupsOfWater());
        assertTrue(subject.isBrewing());

        // - 1 because there is a 1-cycle lag time between coffee that is brewed vs water in the
        // reservoir. E.g. when we resume brewing, the coffee pot needs 1 cycle to catch up
        CoffeePot actualPot = subject.removePot();
        assertEquals(initialBrewCycles + secondBrewCycles - 1, subject.cupsOfCoffee());
        assertEquals(initialBrewCycles + secondBrewCycles - 1, actualPot.cupsOfCoffee());
        assertFalse(actualPot.isFull());
    }


    @Test
    void testStopBrewByRemovingPot() {
        // Given
        int clockCycleOffset = 5;

        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // Partially brew
        for (int i = 0; i < clockCycleOffset; i++) {
            clock.tick();
        }

        CoffeePot actual = subject.removePot();

        // Try and finish brewing. Since the pot was removed, this shouldn't do
        // anything
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset; i++) {
            clock.tick();
        }

        // Then
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        assertEquals(clockCycleOffset, subject.cupsOfCoffee());
        assertEquals(clockCycleOffset, actual.cupsOfCoffee());
        assertFalse(actual.isFull());
    }

    @Test
    void testReplacePot() {
        // Given
        int clockCycleOffset = 5;
        int secondBrewCycles = 4;

        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // Partially brew
        for (int i = 0; i < clockCycleOffset; i++) {
            clock.tick();
        }

        CoffeePot actual = subject.removePot();

        // Try and finish brewing. Since the pot was removed, this shouldn't do
        // anything
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset; i++) {
            clock.tick();
        }

        subject.replacePot();

        // Brew a partial amount again. Only brew a partial amount in order to differentiate the
        // effect of these ticks vs the intended non-effect of the previous ticks.
        for (int i = 0; i < secondBrewCycles; i++) {
            clock.tick();
        }

        // Then
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS - clockCycleOffset, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        assertEquals(clockCycleOffset, subject.cupsOfCoffee());
        assertEquals(clockCycleOffset, actual.cupsOfCoffee());
        assertFalse(actual.isFull());
    }

    @Test
    void testFrequentPauseAndResume() {
        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton();

        // Then
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS - 3, subject.cupsOfWater());
        assertTrue(subject.isBrewing());

        assertEquals(2, subject.cupsOfCoffee());

        CoffeePot actualPot = subject.removePot();
        assertEquals(2, actualPot.cupsOfCoffee());
        assertFalse(actualPot.isFull());
    }

    // TODO: pause/unpause again and again and try to not let coffee pot catch up
}