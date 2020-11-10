package roofing.coffee.maker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
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
class WarmerPlateTests {

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
    void testPotStaysHotAfterPause() {
        // When
        subject.fill(subject.getMaxWaterCapacityCups());
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton(); // Pause brew
        clock.tick(); // Propagate brew button press to Reservoir to stop brewing

        // Then
        // +1 in order to actually surpass the STAY_HOT_CYCLE_COUNT and turn the warmer plate off
        for (int i = 0; i < props.getWarmerPlateStayHotForTickLimit() + 1; i++) {
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
        for (int i = 0; i < props.getWarmerPlateStayHotForTickLimit() + 5; i++) {
            clock.tick();
        }

        subject.fill(subject.getMaxWaterCapacityCups());
        subject.pressBrewButton();

        clock.tick();
        subject.pressBrewButton(); // Pause brew
        clock.tick(); // Propagate brew button press to Reservoir to stop brewing

        // Then
        // +1 in order to actually surpass the STAY_HOT_CYCLE_COUNT and turn the warmer plate off
        for (int i = 0; i < props.getWarmerPlateStayHotForTickLimit() + 1; i++) {
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
        subject.fill(subject.getMaxWaterCapacityCups());
        subject.pressBrewButton();

        // Full brew. +1 because of one-tick lag time between components
        for (int i = 0; i < subject.getMaxWaterCapacityCups() + 1; i++) {
            clock.tick();
        }

        // Then
        // +1 in order to actually surpass the STAY_HOT_CYCLE_COUNT and turn the warmer plate off
        for (int i = 0; i < props.getWarmerPlateStayHotForTickLimit() + 1; i++) {
            assertFalse(subject.isBrewing());
            assertTrue(subject.isWarmerPlateOn());

            clock.tick();
        }

        assertFalse(subject.isBrewing());
        assertFalse(subject.isWarmerPlateOn());
    }
}