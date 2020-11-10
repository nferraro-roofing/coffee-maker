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
 * WarmerPlatePropertiesTests aims to test settings that affect the WarmerPlate's "Stay Hot
 * Duration" - i.e. the duration after brewing during which the warmer plate stays hot. The
 * WarmerPlate uses the clock to determine when to turn itself off.
 * 
 * @author nferraro-roofing
 *
 */
class WarmerPlatePropertiesTests {

    @ParameterizedTest
    @MethodSource("provideStayHotDurationParameters")
    void testGetWarmerPlateStayHotForTickLimit(long inputClockTickRate,
            TimeUnit inputTimeUnit,
            int inputStayHotDurationMinutes,
            long expectedTickPerCup) {

        // When
        CoffeeMakerProperties subject =
                createProperties(inputClockTickRate, inputTimeUnit, inputStayHotDurationMinutes);

        // Then
        assertEquals(expectedTickPerCup, subject.getWarmerPlateStayHotForTickLimit());
    }

    static Stream<Arguments> provideStayHotDurationParameters() {
        return Stream.of(
                // 12000 ticks / minute & 2 minutes stay hot duration = 24000 ticks stay hot
                // duration
                Arguments.of(5, TimeUnit.MILLISECONDS, 2, 24000),

                // 12 ticks / minute & 7 mins stay hot duration = stay hot for 84 ticks
                Arguments.of(5, TimeUnit.SECONDS, 7, 84));
    }

    /*
     * Create a CoffeeMakerProperties with parameters that affect
     * CoffeeMakerProperties::getWarmerPlateStayHotForTickLimit
     */
    private CoffeeMakerProperties createProperties(long clockTickDelay,
            TimeUnit clockDelayUnit,
            int stayHotDurationMinutes) {

        // We care about these
        ClockProps clock = new ClockProps(clockTickDelay, clockDelayUnit);
        ReservoirProps reservoir = new ReservoirProps(1);

        // Dummy values here
        PotProps pot = new PotProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(stayHotDurationMinutes);

        return new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);
    }

}
