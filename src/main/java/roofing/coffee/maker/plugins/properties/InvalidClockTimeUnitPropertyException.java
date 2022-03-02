package roofing.coffee.maker.plugins.properties;

/**
 * Throw InvalidClockTimeUnitPropertyException when an application attempts to tune a CoffeeMaker's
 * Clock's tick delay with an invalid value.
 * 
 * <p>
 * InvalidClockTimeUnitPropertyException acts primarily as convenience method of constructing an
 * IllegalArgumentException with a custom, standardized message for a specific exceptional scenario.
 * The message reads: "A coffee maker's clock time unit must be no coarser than TimeUnit.SECONDS.
 * The provided TimeUnit was %1s. Please correct this property value and re-start the application.".
 * The client provides the erroneous value at construction time.
 * </p>
 * 
 * <p>
 * This messaging indicates that InvalidClockTimeUnitPropertyException considers a time unit
 * property as invalid when it is {@literal >} {@code TimeUnit.SECONDS}.
 * </p>
 * 
 * @author nferraro-roofing
 *
 */
@SuppressWarnings("serial")
public class InvalidClockTimeUnitPropertyException extends RuntimeException {

    private static final String MESSAGE =
            "A coffee maker's clock time unit must be no coarser than TimeUnit.SECONDS. "
                    + "The provided TimeUnit was %1s. Please correct this property value and "
                    + "re-start the application.";

    /**
     * Construct an InvalidClockTimeUnitPropertyException instance with the invalid
     * {@code providedTimeUnit} as evidence of a programmer's evil mist-tunings.
     * 
     * @param providedTimeUnit the value to interpolate into this exception's canned error message.
     */
    public InvalidClockTimeUnitPropertyException(String providedTimeUnit) {
        super(String.format(MESSAGE, providedTimeUnit));
    }
}