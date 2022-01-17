package roofing.coffee.maker.busses;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import roofing.coffee.maker.CoffeeMaker;

public class Clock {

    private final Bus bus;
    private final CoffeeMaker coffeeMaker;

    public static ClockBuilder builder() {
        return new ClockBuilder();
    }

    private Clock(Bus bus, CoffeeMaker coffeeMaker) {
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

    public static class ClockBuilder {

        private Bus bus;
        private CoffeeMaker coffeeMaker;

        public ClockBuilder withBus(Bus bus) {
            this.bus = bus;
            return this;
        }

        public ClockBuilder withCoffeeMaker(CoffeeMaker coffeeMaker) {
            this.coffeeMaker = coffeeMaker;
            return this;
        }

        public Clock build() {
            if (bus == null) {
                throw new IllegalStateException(
                        "You've attempted to build a Clock without setting a Bus, but a Bus is "
                                + "required to build a Clock. Please use withBus() to set the "
                                + "bus for this Clock.");
            }

            if (coffeeMaker == null) {
                throw new IllegalStateException(
                        "You've attempted to build a Clock without setting a CoffeeMaker, but a "
                                + "CoffeeMaker is required to build a Clock. Please use "
                                + "withCoffeeMaker() to set the bus for this Clock.");
            }

            return new Clock(bus, coffeeMaker);
        }
    }
}