package roofing.coffee.maker.plugins.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Clock;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Pot;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.Reservoir;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlate;

/**
 * WaterReservoirPropertiesTest aims to test settings that affect the WaterReservoir's brew rate.
 * 
 * This test class primarily differentiates between parameters that work together to not waste clock
 * cycles and parameters that do "waste" clock cycles - respectively referred to as evenly divisible
 * parameters and NOT evenly divisible parameters.
 * 
 * For example, a clock with clockTickRate of 1 and TimUnit.SECONDS will tick 60 times per minute,
 * thus utilizing all 60 ticks evenly. If the WaterReservoir's cupsPerMinuteBrewRate is 2, then we
 * use 30 ticks in order to brew one cup of coffee without wasting any coffee.
 * 
 * Conversely, a clock with clockTickRate of 7 and TimUnit.SECONDS will tick 8.5 times per minute,
 * thus "wasting" .5 ticks in a minute of brewing. We can compound the issue by further "wasting"
 * .83 ticks with a WaterReservoir cupsPerMinuteBrewRate of 3.
 * 
 * This concept applies to the WaterReservoir's cupsPerMinuteBrewRate
 * 
 * This nuance is important because the WaterReservoir is only aware of a per-minute, non-fractional
 * brew rate.
 * 
 * @author nferraro-roofing
 *
 */
class WaterReservoirBrewRateTest {

    @ParameterizedTest
    @MethodSource("provideEvenlyDivisibleParameters")
    void testEvenlyDivisibleTicksPerCupBrewed(long inputClockTickRate,
            TimeUnit inputTimeUnit,
            int inputCupsPerMinuteBrewRate,
            long expectedTickPerCup) {

        // When
        CoffeeMakerProperties subject =
                createProperties(inputClockTickRate, inputTimeUnit, inputCupsPerMinuteBrewRate);

        // Then
        assertEquals(expectedTickPerCup, subject.getReservoirTicksPerCupBrewed());
    }

    @ParameterizedTest
    @MethodSource("provideNotEvenlyDivisibleParameters")
    void testNotEvenlyDivisibleTicksPerCupBrewed(long inputClockTickRate,
            TimeUnit inputTimeUnit,
            int inputCupsPerMinuteBrewRate,
            long expectedTickPerCup) {

        // When
        CoffeeMakerProperties subject =
                createProperties(inputClockTickRate, inputTimeUnit, inputCupsPerMinuteBrewRate);

        // Then
        assertEquals(expectedTickPerCup, subject.getReservoirTicksPerCupBrewed());
    }

    void testClockTicksSlowerThanOneCupPerMinute() {}

    // TODO: tick rate of clock very slow - slower than once per minute?
    // TODO: assertEquals(2, subject.getReservoirMaxCapacityCups());


    static Stream<Arguments> provideEvenlyDivisibleParameters() {
        // TODO: comment and format like the NOT even provider
        return Stream.of(Arguments.of(1, TimeUnit.NANOSECONDS, 4, 15000000000L),
                Arguments.of(1, TimeUnit.MICROSECONDS, 3, 20000000),
                Arguments.of(10, TimeUnit.MILLISECONDS, 2, 3000),
                Arguments.of(5, TimeUnit.SECONDS, 1, 12));
    }

    static Stream<Arguments> provideNotEvenlyDivisibleParameters() {
        return Stream.of(
            // 6e10 ticks / second & 7 cups / min brew rate = 1714285714.29 ticks / cup brewed,
            // then truncate
            Arguments.of(5, TimeUnit.NANOSECONDS, 7, 1714285714L),
                
            // 6e7 ticks / second & 7 cups / min brew rate = 1714285.71429 ticks / cup brewed,
            // then truncate
            Arguments.of(5, TimeUnit.MICROSECONDS, 7, 1714285),
                
            // 6e4 ticks / second & 7 cups / min brew rate = 1714.2857 ticks / cup brewed, then
            // truncate
            Arguments.of(5, TimeUnit.MILLISECONDS, 7, 1714),
                
            // 12 ticks / second & 7 cups / min brew rate = 1.7142857 ticks / cup brewed, then
            // truncate
            Arguments.of(5, TimeUnit.SECONDS, 7, 1));
    }

    /*
     * Create a CoffeeMakerProperties with parameters that affect
     * CoffeeMakerProperties::getReservoirMaxCapacityCups
     */
    private CoffeeMakerProperties createProperties(long clockTickDelay,
            TimeUnit clockDelayUnit,
            int cupsPerMinuteBrewRate) {

        // We care about these
        Clock clock = new Clock(clockTickDelay, clockDelayUnit);
        Reservoir reservoir = new Reservoir(1, cupsPerMinuteBrewRate);

        // Dummy values here
        Pot pot = new Pot(1);
        WarmerPlate warmerPlate = new WarmerPlate(1);

        return new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);
    }

}
