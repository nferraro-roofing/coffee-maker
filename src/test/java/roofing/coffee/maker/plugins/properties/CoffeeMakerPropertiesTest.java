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
        Reservoir reservoir = new Reservoir(1, 1);
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
        assertEquals(2, subject.getReservoirMaxCapacityCups());
    }

    @Test
    void testClockCreateClockWithBadTickDelay() {
        // Given
        long tickDelay = -9223372036854775807L; // Invalid
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // When & Then
        InvalidClockTickDelayPropertyException thrown =
                assertThrows(InvalidClockTickDelayPropertyException.class,
                        () -> new Clock(tickDelay, timeUnit));
        assertEquals(
                "A coffee maker's clock tick rate is required and must be > 0. The provided value was -9223372036854775807. Please correct this value and re-start the application.",
                thrown.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, names = {"MINUTES", "HOURS", "DAYS"})
    void testClockCreateClockWithBadTimeUnit(TimeUnit timeUnit) {
        // Given
        long tickDelay = 1; // Valid

        // When & Then
        InvalidClockTimeUnitPropertyException thrown =
                assertThrows(InvalidClockTimeUnitPropertyException.class,
                        () -> new Clock(tickDelay, timeUnit));
        assertEquals(
                "A coffee maker's clock time unit must be no coarser than TimeUnit.SECONDS. The provided TimeUnit was "
                        + timeUnit
                        + ". Please correct this property value and re-start the application.",
                thrown.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, names = {"NANOSECONDS", "MICROSECONDS", "SECONDS"})
    void testClockCreateClockWithGoodTimeUnit(TimeUnit timeUnit) {
        // Given
        long tickDelay = 1; // Valid

        // When
        new Clock(tickDelay, timeUnit);

        // Then - as long as the above line doesn't throw an exception, pass
        assertTrue(true);
    }
}
