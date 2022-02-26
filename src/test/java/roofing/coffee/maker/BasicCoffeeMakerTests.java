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
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps;

/**
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
    void testFullBrew() {
        // When
        int fillLvl = subject.getMaxWaterCapacityCups();
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
        int fillLvl = subject.getMaxWaterCapacityCups();
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
    void testOverFillEmptyCoffeeMaker() {
        int fillLvl = subject.getMaxWaterCapacityCups();
        assertThrows(IllegalArgumentException.class,
                () -> subject.fill(fillLvl + 5)); // Fill beyond capacity
    }

    @Test
    void testOverFillPartiallyFilledCoffeeMaker() {
        int fillLvl = subject.getMaxWaterCapacityCups();
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