package roofing.coffee.maker.busses;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import roofing.coffee.maker.TestTimeCoffeeMakerCreator;
import roofing.coffee.maker.busses.Clock.ClockBuilder;

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
        ClockBuilder missingBus = Clock.builder().bus(new Bus());
        ClockBuilder missingCoffeeMaker =
                Clock.builder().coffeeMaker(TestTimeCoffeeMakerCreator.create());

        return Stream.of(missingBus, missingCoffeeMaker);
    }

    @ParameterizedTest
    @MethodSource("provideClockBuilders")
    void testMisConfiguredClockBuilder(ClockBuilder subject) {
        assertThrows(NullPointerException.class, () -> subject.build());
    }

    @Test
    void testNullBus() {
        ClockBuilder subject = Clock.builder();
        assertThrows(NullPointerException.class, () -> subject.bus(null));
    }

    @Test
    void testNullCoffeeMaker() {
        ClockBuilder subject = Clock.builder();
        assertThrows(NullPointerException.class, () -> subject.coffeeMaker(null));
    }
}