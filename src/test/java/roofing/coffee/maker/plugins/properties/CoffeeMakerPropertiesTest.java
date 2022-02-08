package roofing.coffee.maker.plugins.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Clock;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Pot;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Reservoir;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlate;

class CoffeeMakerPropertiesTest {

    @Test
    void testHappyPath() {
        // Given
        Clock clock = new Clock(1, TimeUnit.SECONDS);
        Pot pot = new Pot(1);
        Reservoir reservoir = new Reservoir(1);
        WarmerPlate warmerPlate = new WarmerPlate(1);

        // When
        CoffeeMakerProperties subject =
                new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);

        // Then
        assertEquals(1, subject.getClockTickDelay());
        assertEquals(TimeUnit.SECONDS, subject.getClockTickDelayUnit());
        assertEquals(1, subject.getPotMaxCapacityCups());
        assertEquals(60, subject.getReservoirTicksPerCupBrewed());
        assertEquals(60, subject.getWarmerPlateStayHotForTickLimit());
    }

    @Test
    void testCreateClockWithBadTickDelay() {
        // Given
        long tickDelay = -9223372036854775807L; // Invalid
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // When & Then
        InvalidClockTickDelayPropertyException thrown =
                assertThrows(InvalidClockTickDelayPropertyException.class,
                        () -> new Clock(tickDelay, timeUnit));
        assertEquals(
                "A coffee maker's clock tick rate is required and must be > 0. The "
                        + "provided value was -9223372036854775807. Please correct this value "
                        + "and re-start the application.",
                thrown.getMessage());
    }

    @Test
    void testCreateClockWithFewerThanOneTickPerSecond() {
        // Given tick rate of .6 per second - less than 1 tick per minute
        long tickDelay = 100;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // When & Then
        InvalidClockTicksPerMinuteException thrown =
                assertThrows(InvalidClockTicksPerMinuteException.class,
                        () -> new Clock(tickDelay, timeUnit));
        assertEquals("The provided combination of 100 tickDelay and SECONDS "
                + "delayUnit resulted in fewer than 1 tick per minute, but a coffee maker's "
                + "clock must tick at least once per minute. Please adjust these parameters "
                + "in order to increase the clock's tick rate.",
                thrown.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, names = {"MINUTES", "HOURS", "DAYS"})
    void testCreateClockWithBadTimeUnit(TimeUnit timeUnit) {
        // Given
        long tickDelay = 1; // Valid

        // When & Then
        InvalidClockTimeUnitPropertyException thrown =
                assertThrows(InvalidClockTimeUnitPropertyException.class,
                        () -> new Clock(tickDelay, timeUnit));
        assertEquals(
                "A coffee maker's clock time unit must be no coarser than TimeUnit.SECONDS. The "
                        + "provided TimeUnit was " + timeUnit
                        + ". Please correct this property value and re-start the application.",
                thrown.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, names = {"NANOSECONDS", "MICROSECONDS", "SECONDS"})
    void testCreateClockWithGoodTimeUnit(TimeUnit timeUnit) {
        // Given
        long tickDelay = 1; // Valid

        // When
        new Clock(tickDelay, timeUnit);

        // Then - as long as the above line doesn't throw an exception, pass
        assertTrue(true);
    }
}
