package roofing.coffee.maker.plugins.properties;

/**
 * Throw InvalidClockTickDelayPropertyException when an application attempts to tune a CoffeeMaker's
 * Clock's tick delay with an invalid value.
 * 
 * <p>
 * InvalidClockTickDelayPropertyException acts primarily as convenience method of constructing an
 * IllegalArgumentException with a custom, standardized message for a specific exceptional scenario.
 * The message reads: "A coffee maker's clock tick rate is required and must be {@literal >} 0. The
 * provided value was %1d. Please correct this value and re-start the application.". The client
 * provides the erroneous value at construction time.
 * </p>
 * 
 * <p>
 * This messaging indicates that InvalidClockTickDelayPropertyException considers a clock tick rate
 * property as invalid when it is missing, 0, or negative.
 * </p>
 * 
 * @author nferraro-roofing
 *
 */
@SuppressWarnings("serial")
public class InvalidClockTickDelayPropertyException extends IllegalArgumentException {

    private static final String MESSAGE = "A coffee maker's clock tick rate is required and "
            + "must be > 0. The provided value was %1d. Please correct this value and re-start the "
            + "application.";

    /**
     * Construct an InvalidClockTickDelayPropertyException instance with the invalid
     * {@code providedTickRate} as evidence of a programmer's evil mist-tunings.
     * 
     * @param providedTickRate the value to interpolate into this exception's canned error message.
     */
    public InvalidClockTickDelayPropertyException(long providedTickRate) {
        super(String.format(MESSAGE, providedTickRate));
    }
}