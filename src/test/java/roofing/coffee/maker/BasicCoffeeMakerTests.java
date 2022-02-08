package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WaterReservoir;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Pot;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Reservoir;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlate;

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

    private static CoffeeMakerProperties props;

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeAll
    static void initProps() {
        CoffeeMakerProperties.Clock clock = new CoffeeMakerProperties.Clock(60L, TimeUnit.SECONDS);
        Pot pot = new Pot(10);
        Reservoir reservoir = new Reservoir(1);
        WarmerPlate warmerPlate = new WarmerPlate(10);

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
    void testFullBrew() {
        // When
        int fillLvl = props.getPotMaxCapacityCups() + WaterReservoir.COFFEE_POT_MAX_CAPACITY_OFFSET;
        subject.fill(fillLvl);
        subject.pressBrewButton();

        // + 5 in order to avoid any lag time in state propagation to various components
        for (int i = 0; i < fillLvl + 5; i++) {
            clock.tick();
        }

        // Then
        assertEquals(0, subject.cupsOfWater());
        assertFalse(subject.isBrewing());
        assertTrue(subject.isWarmerPlateOn());

        CoffeePot actualPot = subject.removePot();
        assertEquals(props.getPotMaxCapacityCups(), subject.cupsOfCoffee());
        assertEquals(props.getPotMaxCapacityCups(), actualPot.cupsOfCoffee());
        assertTrue(actualPot.isFull());
    }

    @Test
    void testFullBrewWithExtraTicks() {
        // When
        int fillLvl = props.getPotMaxCapacityCups() + WaterReservoir.COFFEE_POT_MAX_CAPACITY_OFFSET;
        clock.tick();
        clock.tick();
        subject.fill(fillLvl);

        clock.tick();
        clock.tick();
        subject.pressBrewButton();
        clock.tick();

        // + 5 in order to avoid any lag time in state propagation to various components
        for (int i = 0; i < fillLvl + 5; i++) {
            clock.tick();
        }

        // Then
        assertEquals(0, subject.cupsOfWater());
        assertFalse(subject.isBrewing());
        assertTrue(subject.isWarmerPlateOn());

        CoffeePot actualPot = subject.removePot();
        assertEquals(props.getPotMaxCapacityCups(), subject.cupsOfCoffee());
        assertEquals(props.getPotMaxCapacityCups(), actualPot.cupsOfCoffee());
        assertTrue(actualPot.isFull());
    }

    @Test
    void testBrewMoreWaterThanPotCanHold() {
        // When
        int fillLvl = props.getPotMaxCapacityCups() + WaterReservoir.COFFEE_POT_MAX_CAPACITY_OFFSET;
        int extraCupsOfWater = 5;
        subject.fill(fillLvl + extraCupsOfWater);
        subject.pressBrewButton();

        // + 5 in order to avoid any lag time in state propagation to various components
        for (int i = 0; i < fillLvl + extraCupsOfWater + 5; i++) {
            clock.tick();
        }

        // Then
        // -1 because state propagation takes 1 clock tick - i.e. the reservoir will know that the
        // pot is full 1 tick after the cycle in which it filled up. This is OK because coffee
        // makers produce slightly less coffee than the water used to brew it!
        assertEquals(extraCupsOfWater - 1, subject.cupsOfWater());
        assertFalse(subject.isBrewing());

        CoffeePot actualPot = subject.removePot();
        assertEquals(props.getPotMaxCapacityCups(), subject.cupsOfCoffee());
        assertEquals(props.getPotMaxCapacityCups(), actualPot.cupsOfCoffee());
        assertTrue(actualPot.isFull());
    }

    @Test
    void testOverFillEmptyCoffeeMaker() {
        int fillLvl = props.getPotMaxCapacityCups() + WaterReservoir.COFFEE_POT_MAX_CAPACITY_OFFSET;
        assertThrows(IllegalArgumentException.class,
                () -> subject.fill(fillLvl + 5)); // Fill beyond capacity
    }

    @Test
    void testOverFillPartiallyFilledCoffeeMaker() {
        int fillLvl = props.getPotMaxCapacityCups() + WaterReservoir.COFFEE_POT_MAX_CAPACITY_OFFSET;
        subject.fill(fillLvl - 5); // Partially fill
        
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