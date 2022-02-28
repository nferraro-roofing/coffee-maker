package roofing.coffee.maker.busses;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Builder;
import lombok.NonNull;
import roofing.coffee.maker.CoffeeMaker;

/**
 * A clock schedules messages for a CoffeeMaker's bus.
 * 
 * <p>
 * A clock ticks once after {@code period} has elapsed in {@code periodUnit} - e.g. the clock may
 * tick once after <i>10 seconds</i> elapse.
 * </p>
 * 
 * <p>
 * Upon each tick, the Clock requests the CoffeeMaker's current state via
 * {@link roofing.coffee.maker.CoffeeMaker#asBusMessage()} and then places that message on the Bus.
 * </p>
 * 
 * <p>
 * The clock starts ticking automatically upon a call to {@code start()}. Optionally, clients may
 * ignore this method and manually tick the clock via {@code tick()}.
 * </p>
 * 
 * <p>
 * Construct instances of a Clock via it's internal ClockBuilder class. The builder scheme may
 * appear odd here, but it is instrumental in enabling unit-testability of an otherwise asynchronous
 * and difficult to control package.
 * </p>
 * 
 * @author nferraro-roofing
 *
 */
@Builder
public class Clock {

    private static final Logger LOG = LoggerFactory.getLogger(Clock.class);

    @NonNull
    private final Bus bus;

    @NonNull
    private final CoffeeMaker coffeeMaker;

    /**
     * Schedules the clock to start ticking asynchronously and automatically.
     * 
     * <p>
     * A clock ticks once after {@code period} has elapsed in {@code periodUnit} - e.g. the clock
     * may tick once after <i>10 seconds</i> elapse.
     * </p>
     * 
     * @param period the number of units of time that must elapse between ticks of the clock
     * @param periodUnit the unit applied to {@code period}
     */
    public void start(long period, TimeUnit periodUnit) {
        LOG.debug("Clock starting with period {} and unit {}", period, periodUnit);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::tick, 0, period, periodUnit);
    }

    /**
     * Causes the Clock to request the CoffeeMaker's current state via
     * {@link roofing.coffee.maker.CoffeeMaker#asBusMessage()} and then place that message on the
     * Bus.
     */
    public void tick() {
        BusMessage message = coffeeMaker.asBusMessage();
        LOG.trace("Clock ticking. Sending message to bus: {}", message);
        bus.update(message);
    }
}