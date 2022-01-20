package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;

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
class WarmerPlateTests {

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder);
        clock = clockBuilder.build();
    }

    @Test
    void testPotStaysHotAfterPause() {
        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton(); // Pause brew
        clock.tick(); // Propagate brew button press to Reservoir to stop brewing

        // Then
        // +1 in order to actually surpass the STAY_HOT_CYCLE_COUNT and turn the warmer plate off
        for (int i = 0; i < WarmerPlate.STAY_HOT_CYCLE_COUNT + 1; i++) {
            assertFalse(subject.isBrewing());
            assertTrue(subject.isWarmerPlateOn());

            clock.tick();
        }

        assertFalse(subject.isBrewing());
        assertFalse(subject.isWarmerPlateOn());
    }

    @Test
    void testInitialTicksDontMatter() {
        // When
        // Try to trick the WarmerPlate into thinking it should never be hot. +5 for good measure
        for (int i = 0; i < WarmerPlate.STAY_HOT_CYCLE_COUNT + 5; i++) {
            clock.tick();
        }

        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton(); // Pause brew
        clock.tick(); // Propagate brew button press to Reservoir to stop brewing

        // Then
        // +1 in order to actually surpass the STAY_HOT_CYCLE_COUNT and turn the warmer plate off
        for (int i = 0; i < WarmerPlate.STAY_HOT_CYCLE_COUNT + 1; i++) {
            assertFalse(subject.isBrewing());
            assertTrue(subject.isWarmerPlateOn());

            clock.tick();
        }

        assertFalse(subject.isBrewing());
        assertFalse(subject.isWarmerPlateOn());
    }

    @Test
    void testPotStaysHotAfterFullBrew() {
        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // Full brew. +1 because of one-tick lag time between components
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS + 1; i++) {
            clock.tick();
        }

        // Then
        // +1 in order to actually surpass the STAY_HOT_CYCLE_COUNT and turn the warmer plate off
        for (int i = 0; i < WarmerPlate.STAY_HOT_CYCLE_COUNT + 1; i++) {
            assertFalse(subject.isBrewing());
            assertTrue(subject.isWarmerPlateOn());

            clock.tick();
        }

        assertFalse(subject.isBrewing());
        assertFalse(subject.isWarmerPlateOn());
    }
}