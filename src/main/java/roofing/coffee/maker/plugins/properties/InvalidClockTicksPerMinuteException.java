package roofing.coffee.maker.plugins.properties;

import java.util.concurrent.TimeUnit;

/**
 * Throw InvalidClockTicksPerMinuteException when an application attempts to tune a CoffeeMaker's
 * Clock's ticks per minute with an invalid value.
 * 
 * <p>
 * InvalidClockTicksPerMinuteException acts primarily as convenience method of constructing an
 * IllegalArgumentException with a custom, standardized message for a specific exceptional scenario.
 * The message reads: "The provided combination of %1d tickDelay and %2s delayUnit resulted in fewer
 * than 1 tick per minute, but a coffee maker's clock must tick at least once per minute. Please
 * adjust these parameters in order to increase the clock's tick rate". The client provides the
 * erroneous value at construction time.
 * </p>
 * 
 * <p>
 * This messaging indicates that InvalidClockTicksPerMinuteException considers a clock ticks per
 * minute as invalid when it ticks slower than once per minute.
 * </p>
 * 
 * @author nferraro-roofing
 *
 */
@SuppressWarnings("serial")
public class InvalidClockTicksPerMinuteException extends IllegalArgumentException {

    private static final String MESSAGE = "The provided combination of %1d tickDelay and %2s "
            + "delayUnit resulted in fewer than 1 tick per minute, but a coffee maker's clock "
            + "must tick at least once per minute. Please adjust these parameters in order to "
            + "increase the clock's tick rate.";

    /**
     * Construct an InvalidClockTicksPerMinuteException instance with the invalid
     * {@code providedTickRate} and {@code providedTimeUnit} as evidence of a programmer's evil
     * mist-tunings.
     * 
     * @param providedTickRate the value to interpolate into the first value of this exception's
     *        canned error message.
     * @param providedTimeUnit the value to interpolate into the second value of this exception's
     *        canned error message.
     */
    public InvalidClockTicksPerMinuteException(long providedTickRate, TimeUnit providedTimeUnit) {
        super(String.format(MESSAGE, providedTickRate, providedTimeUnit));
    }
}