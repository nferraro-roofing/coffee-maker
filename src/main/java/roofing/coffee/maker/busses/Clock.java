package roofing.coffee.maker.busses;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Builder;
import lombok.NonNull;
import roofing.coffee.maker.CoffeeMaker;

@Builder
public class Clock {

    private static final Logger LOG = LoggerFactory.getLogger(Clock.class);

    @NonNull
    private final Bus bus;

    @NonNull
    private final CoffeeMaker coffeeMaker;

    public void start(long period, TimeUnit periodUnit) {
        LOG.debug("Clock starting with period {} and unit {}", period, periodUnit);

        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(this::tick, 0, period, periodUnit);
    }

    public void tick() {
        BusMessage message = coffeeMaker.asBusMessage();
        LOG.trace("Clock ticking. Sending message to bus: {}", message);
        bus.update(message);
    }
}