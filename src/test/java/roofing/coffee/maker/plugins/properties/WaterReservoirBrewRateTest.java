package roofing.coffee.maker.plugins.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps;

/**
 * WaterReservoirPropertiesTest aims to test settings that affect the WaterReservoir's brew rate.
 * 
 * This test class primarily differentiates between parameters that work together to not "waste"
 * clock cycles and parameters that do "waste" clock cycles - respectively referred to as evenly
 * divisible parameters and NOT evenly divisible parameters.
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

    static Stream<Arguments> provideEvenlyDivisibleParameters() {
        return Stream.of(
                // 600000000 ticks / minute & 4 cups / min brew rate = 150000000 ticks / cup brewed
                Arguments.of(100, TimeUnit.NANOSECONDS, 4, 150000000L),

                // 1200000 ticks / minute & 3 cups / min brew rate = 400000 ticks / cup brewed
                Arguments.of(50, TimeUnit.MICROSECONDS, 3, 400000),

                // 6000 ticks / minute & 2 cups / min brew rate = 3000 ticks / cup brewed
                Arguments.of(10, TimeUnit.MILLISECONDS, 2, 3000),

                // 12 ticks / minute & 1 cups / min brew rate = 12 ticks / cup brewed
                Arguments.of(5, TimeUnit.SECONDS, 1, 12),

                // 1 tick per second & 1 cup / min brew rate = 1 tick / cup brewed
                Arguments.of(60, TimeUnit.SECONDS, 1, 1));
    }

    static Stream<Arguments> provideNotEvenlyDivisibleParameters() {
        return Stream.of(
                // 6e10 ticks / minute & 7 cups / min brew rate = 1714285714.29 ticks / cup brewed,
                // then truncate
                Arguments.of(5, TimeUnit.NANOSECONDS, 7, 1714285714L),

                // 6e7 ticks / minute & 7 cups / min brew rate = 1714285.71429 ticks / cup brewed,
                // then truncate
                Arguments.of(5, TimeUnit.MICROSECONDS, 7, 1714285),

                // 6e4 ticks / minute & 7 cups / min brew rate = 1714.2857 ticks / cup brewed, then
                // truncate
                Arguments.of(5, TimeUnit.MILLISECONDS, 7, 1714),

                // 12 ticks / minute & 7 cups / min brew rate = 1.7142857 ticks / cup brewed, then
                // truncate
                Arguments.of(5, TimeUnit.SECONDS, 7, 1));
    }

    /*
     * Create a CoffeeMakerProperties with parameters that affect
     * CoffeeMakerProperties::getReservoirTicksPerCupBrewed
     */
    private CoffeeMakerProperties createProperties(long clockTickDelay,
            TimeUnit clockDelayUnit,
            int cupsPerMinuteBrewRate) {

        // We care about these
        ClockProps clock = new ClockProps(clockTickDelay, clockDelayUnit);
        ReservoirProps reservoir = new ReservoirProps(cupsPerMinuteBrewRate);

        // Dummy values here
        PotProps pot = new PotProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(1);

        return new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);
    }

}
