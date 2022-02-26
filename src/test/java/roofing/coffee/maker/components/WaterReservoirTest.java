package roofing.coffee.maker.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests for WaterReservoir logic in isolation.
 * 
 * Developers should stray away from such fine-grained tests unless certain execution paths are
 * tricky to test from coarser-grained perspective. In general, please write tests from a
 * feature-level perspective and use unit tests for edge cases or special cases only.
 * 
 * @author nferraro-roofing
 *
 */
class WaterReservoirTest {

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
        WaterReservoir subject = new WaterReservoir(10, 60);

        // When
        String actual = subject.toString();

        // Then
        assertEquals(
                "WaterReservoir(ticksPerCupBrewed=60, maxCapacityCups=11, cupsOfWater=0, isBrewing=false, ticksSinceLastCupBrewed=0)",
                actual);
    }
}
