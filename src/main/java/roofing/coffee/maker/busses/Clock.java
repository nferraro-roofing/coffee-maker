package roofing.coffee.maker.busses;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import roofing.coffee.maker.CoffeeMaker;

public class Clock {

    private final Bus bus;
    private final CoffeeMaker coffeeMaker;

    Clock(Bus bus, CoffeeMaker coffeeMaker) {
        this.bus = bus;
        this.coffeeMaker = coffeeMaker;
    }

    public void start() {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
    }

    public void tick() {
        bus.update(coffeeMaker.asBusMessage());
    }
}
