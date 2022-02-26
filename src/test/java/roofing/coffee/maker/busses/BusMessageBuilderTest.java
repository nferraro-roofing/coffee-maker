package roofing.coffee.maker.busses;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import roofing.coffee.maker.busses.BusMessage.BusMessageBuilder;
import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

/**
 * Tests for BusMessageTest logic in isolation.
 * 
 * Developers should stray away from such fine-grained tests unless certain execution paths are
 * tricky to test from coarser-grained perspective. In general, please write tests from a
 * feature-level perspective and use unit tests for edge cases or special cases only.
 * 
 * @author nferraro-roofing
 *
 */
class BusMessageBuilderTest {

    private static final WaterReservoir reservoir = new WaterReservoir(1, 1);
    private static final BrewButton button = new BrewButton();
    private static final CoffeePot pot = new CoffeePot(1, 1);
    private static final WarmerPlate warmer = new WarmerPlate(1);

    /**
     * Return a stream of arguments for testBrewButtonNotSet().
     * 
     * This method returns a stream of suppliers rather than a stream of BusMessageBuilder directly
     * because BusMessageBuilder is a singleton. Therefore, creating 4 separate instances actually
     * just creates four references to the same, mis-configured singleton. We must instead create &
     * configure the actual instance when the test itself runs.
     * 
     * @return Stream<Supplier<BusMessageBuilder>>
     */
    static Stream<Supplier<BusMessageBuilder>> provideBusMessageBuilders() {
        Supplier<BusMessageBuilder> missingButtonSupplier = () -> BusMessage.builder()
                .withCoffeePot(pot)
                .withWarmerPlate(warmer)
                .withWaterReservoir(reservoir);

        Supplier<BusMessageBuilder> missingCoffeePotSupplier = () -> BusMessage.builder()
                .withBrewButton(button)
                .withWarmerPlate(warmer)
                .withWaterReservoir(reservoir);

        Supplier<BusMessageBuilder> missingWarmerPlateSupplier = () -> BusMessage.builder()
                .withBrewButton(button)
                .withCoffeePot(pot)
                .withWaterReservoir(reservoir);

        Supplier<BusMessageBuilder> missingReservoir = () -> BusMessage.builder()
                .withBrewButton(button)
                .withCoffeePot(pot)
                .withWarmerPlate(warmer);

        return Stream.of(missingButtonSupplier,
                missingCoffeePotSupplier,
                missingWarmerPlateSupplier,
                missingReservoir);
    }

    @ParameterizedTest
    @MethodSource("provideBusMessageBuilders")
    void testMisConfiguredBusMessageBuilder(Supplier<BusMessageBuilder> subjectSupplier) {
        BusMessageBuilder subject = subjectSupplier.get();
        assertThrows(IllegalStateException.class, () -> subject.build());
    }
}