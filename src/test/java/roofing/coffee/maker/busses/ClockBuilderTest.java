package roofing.coffee.maker.busses;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import roofing.coffee.maker.CoffeeMaker;
import roofing.coffee.maker.CoffeeMakerCreator;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps;

/**
 * ClockBuilderTest tests portions of the ClockBuilder that other tests do not cover. The various
 * CoffeeMaker-oriented tests (roofing.coffee.maker.*Tests.java) cover happy-path scenarios;
 * therefore, this test class covers edge cases only.
 * 
 * @author nferraro-roofing
 *
 */
class ClockBuilderTest {

    static Stream<ClockBuilder> provideClockBuilders() {
        ClockBuilder missingBus = Clock.builder().withBus(new Bus());
        ClockBuilder missingCoffeeMaker =
                Clock.builder().withCoffeeMaker(createDummyCoffeeMaker());

        return Stream.of(missingBus, missingCoffeeMaker);
    }

    static CoffeeMaker createDummyCoffeeMaker() {
        ClockProps clock = new ClockProps(60L, TimeUnit.SECONDS);
        PotProps pot = new PotProps(10);
        ReservoirProps reservoir = new ReservoirProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(10);

        // When
        CoffeeMakerProperties props = new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);

        return CoffeeMakerCreator.create(props);
    }

    @ParameterizedTest
    @MethodSource("provideClockBuilders")
    void testMisConfiguredClockBuilder(ClockBuilder subject) {
        assertThrows(IllegalStateException.class, () -> subject.build());
    }

}