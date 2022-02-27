package roofing.coffee.maker.plugins.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps;

class CoffeeMakerPropertiesTest {
    
    @Test
    void testHappyPath() {
        // Given
        ClockProps clock = new ClockProps(1, TimeUnit.SECONDS);
        PotProps pot = new PotProps(1);
        ReservoirProps reservoir = new ReservoirProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(1);

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

    @ParameterizedTest
    @MethodSource("provideNullProperties")
    void testNullProps(ClockProps clock,
            PotProps pot,
            ReservoirProps reservoir,
            WarmerPlateProps warmerPlate) {

        // When & Then
        assertThrows(NullPointerException.class,
                () -> new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate));
    }

    @Test
    void testCreateClockWithBadTickDelay() {
        // Given
        long tickDelay = -9223372036854775807L; // Invalid
        TimeUnit timeUnit = TimeUnit.SECONDS;

        // When & Then
        InvalidClockTickDelayPropertyException thrown =
                assertThrows(InvalidClockTickDelayPropertyException.class,
                        () -> new ClockProps(tickDelay, timeUnit));
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
                        () -> new ClockProps(tickDelay, timeUnit));
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
                        () -> new ClockProps(tickDelay, timeUnit));
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
        new ClockProps(tickDelay, timeUnit);

        // Then - as long as the above line doesn't throw an exception, pass
        assertTrue(true);
    }
    
    /**
     * Ensure that the messaging of {@code toString()) is appropriate for a logging message.
     * 
     * It may seem odd to unit test {@code toString()), as it implements has no real logic. However,
     * {@code toString()) may come into play during logging time. We want to ensure that any 
     * downstream tools (or even just developer / production support eyes) that hook into our 
     * enjoy a consistent experience.
     * 
     * This may be overkill, though.
     * 
     */
    @Test
    void testToString() {
        // Given
        ClockProps clock = new ClockProps(60L, TimeUnit.SECONDS);
        PotProps pot = new PotProps(10);
        ReservoirProps reservoir = new ReservoirProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(10);

        // When
        CoffeeMakerProperties subject =
                new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);

        // When
        String actual = subject.toString();

        // Then
        assertEquals(
                "CoffeeMakerProperties(clock=CoffeeMakerProperties.ClockProps(tickDelay=60, delayUnit=SECONDS, ticksPerMinute=1), pot=CoffeeMakerProperties.PotProps(maxCapacityCups=10), reservoir=CoffeeMakerProperties.ReservoirProps(cupsPerMinuteBrewRate=1), warmerPlate=CoffeeMakerProperties.WarmerPlateProps(stayHotDurationMinutes=10), getReservoirTicksPerCupBrewed=1, getWarmerPlateStayHotForTickLimit=10)",
                actual);
    }

    static Stream<Arguments> provideNullProperties() {
        return Stream.of(
                Arguments.of(
                    null, // ClockProps
                    new PotProps(1),
                    new ReservoirProps(1),
                    new WarmerPlateProps(1)),
                Arguments.of(
                    new ClockProps(1, TimeUnit.SECONDS),
                    null, // PotProps
                    new ReservoirProps(1),
                    new WarmerPlateProps(1)),
                Arguments.of(
                    new ClockProps(1, TimeUnit.SECONDS),
                    new PotProps(1),
                    null, // ReservoirProps
                    new WarmerPlateProps(1)),
                Arguments.of(
                    new ClockProps(1, TimeUnit.SECONDS),
                    new PotProps(1),
                    new ReservoirProps(1),
                    null)); // WarmerPlateProps
    }
}
