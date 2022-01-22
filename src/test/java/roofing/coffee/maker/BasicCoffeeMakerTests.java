package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WaterReservoir;

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
class BasicCoffeeMakerTests {

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder);
        clock = clockBuilder.build();
    }

    @Test
    void testFullBrew() {
        // When
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);
        subject.pressBrewButton();

        // + 5 in order to avoid any lag time in state propagation to various components
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS + 5; i++) {
            clock.tick();
        }

        // Then
        assertEquals(0, subject.cupsOfWater());
        assertFalse(subject.isBrewing());
        assertTrue(subject.isWarmerPlateOn());

        CoffeePot actualPot = subject.removePot();
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, subject.cupsOfCoffee());
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, actualPot.cupsOfCoffee());
        assertTrue(actualPot.isFull());
    }

    @Test
    void testFullBrewWithExtraTicks() {
        // When
        clock.tick();
        clock.tick();
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS);

        clock.tick();
        clock.tick();
        subject.pressBrewButton();
        clock.tick();

        // + 5 in order to avoid any lag time in state propagation to various components
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS + 5; i++) {
            clock.tick();
        }

        // Then
        assertEquals(0, subject.cupsOfWater());
        assertFalse(subject.isBrewing());
        assertTrue(subject.isWarmerPlateOn());

        CoffeePot actualPot = subject.removePot();
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, subject.cupsOfCoffee());
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, actualPot.cupsOfCoffee());
        assertTrue(actualPot.isFull());
    }

    @Test
    void testBrewMoreWaterThanPotCanHold() {
        // When
        int extraCupsOfWater = 5;
        subject.fill(CoffeePot.MAX_CAPACITY_CUPS + extraCupsOfWater);
        subject.pressBrewButton();

        // + 5 in order to avoid any lag time in state propagation to various components
        for (int i = 0; i < CoffeePot.MAX_CAPACITY_CUPS + extraCupsOfWater + 5; i++) {
            clock.tick();
        }

        // Then
        // -1 because state propagation takes 1 clock tick - i.e. the reservoir will know that the
        // pot is full 1 tick after the cycle in which it filled up. This is OK because coffee
        // makers produce slightly less coffee than the water used to brew it!
        assertEquals(extraCupsOfWater - 1, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        CoffeePot actualPot = subject.removePot();
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, subject.cupsOfCoffee());
        assertEquals(CoffeePot.MAX_CAPACITY_CUPS, actualPot.cupsOfCoffee());
        assertTrue(actualPot.isFull());
    }

    @Test
    void testOverFillEmptyCoffeeMaker() {
        assertThrows(IllegalArgumentException.class,
                () -> subject.fill(WaterReservoir.MAX_CAPACITY_CUPS + 5)); // Fill beyond capacity
    }

    @Test
    void testOverFillPartiallyFilledCoffeeMaker() {
        subject.fill(WaterReservoir.MAX_CAPACITY_CUPS - 5); // Partially fill
        
        assertThrows(IllegalArgumentException.class,
                () -> subject.fill(6)); // Fill beyond capacity
    }

    @Test
    void testRemovePotWhenAlreadyRemoved() {
        subject.removePot();
        assertThrows(IllegalStateException.class, () -> subject.removePot());
    }

    @Test
    void testReplacePotWhenAlreadyPlaced() {
        // Coffee maker initial state has a pot already in place
        assertThrows(IllegalStateException.class, () -> subject.replacePot());
    }
}