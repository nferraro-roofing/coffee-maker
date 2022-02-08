package roofing.coffee.maker.plugins.properties;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class InvalidClockTicksPerMinuteException extends RuntimeException {

    private static final String MESSAGE = "The provided combination of %1d tickDelay and %2s "
            + "delayUnit resulted in fewer than 1 tick per minute, but a coffee maker's clock "
            + "must tick at least once per minute. Please adjust these parameters in order to "
            + "increase the clock's tick rate.";

    public InvalidClockTicksPerMinuteException(long providedTickRate, TimeUnit providedTimeUnit) {
        super(String.format(MESSAGE, providedTickRate, providedTimeUnit));
    }
}