package roofing.coffee.maker.plugins.properties;

@SuppressWarnings("serial")
public class InvalidClockTickDelayPropertyException extends RuntimeException {

    private static final String MESSAGE = "A coffee maker's clock tick rate is required and "
            + "must be > 0. The provided value was %1d. Please correct this value and re-start the "
            + "application.";

    public InvalidClockTickDelayPropertyException(long providedTickRate) {
        super(String.format(MESSAGE, providedTickRate));
    }
}