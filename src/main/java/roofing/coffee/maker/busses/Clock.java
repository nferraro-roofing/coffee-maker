package roofing.coffee.maker.busses;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Clock {

    private final Bus bus;

    private boolean isTicking = false;

    public static Clock start(Bus bus) {
        Clock clock = new Clock(bus);
        clock.tick();
        return clock;
    }

    Clock(Bus bus) {
        this.bus = bus;
    }

    private void tick() {
        bus.update();

        if (!isTicking) {
            Executors.newScheduledThreadPool(1)
                    .scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
            isTicking = true;
        }
    }

}
