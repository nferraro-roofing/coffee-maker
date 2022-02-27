package roofing.coffee.maker.busses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import roofing.coffee.maker.TestTimeCoffeeMakerCreator;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.components.BrewButton;

/**
 * Tests for Clock logic in isolation.
 * 
 * Developers should stray away from such fine-grained tests unless certain execution paths are
 * tricky to test from coarser-grained perspective. In general, please write tests from a
 * feature-level perspective and use unit tests for edge cases or special cases only.
 * 
 * @author nferraro-roofing
 *
 */
class ClockTest {

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
        ClockBuilder subject =
                Clock.builder()
                        .coffeeMaker(TestTimeCoffeeMakerCreator.create())
                        .bus(new Bus(new BrewButton())); // At least one BusComponent so the
                                                         // toString() message is meaningful. Select
                                                         // BrewButton because it's the simplest
                                                         // component to instantiate

        // When
        String actual = subject.toString();

        // Then
        assertEquals(
                "Clock.ClockBuilder(bus=Bus(synchedComponents=[BrewButton(brewState=NOT_REQUESTED)]), coffeeMaker=CoffeeMaker(reservoir=WaterReservoir(ticksPerCupBrewed=1, maxCapacityCups=11, cupsOfWater=0, isBrewing=false, ticksSinceLastCupBrewed=0), button=BrewButton(brewState=NOT_REQUESTED), pot=CoffeePot(maxCapacityCups=10, ticksPerCupBrewed=1, cupsOfCoffee=0, ticksSinceLastCupBrewed=0), warmer=WarmerPlate(stayHotTickLimit=10, cyclesAfterBrewStopped=0, hasPot=true, isHot=false)))",
                actual);
    }

}
