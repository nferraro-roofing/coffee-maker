package roofing.coffee.maker.clocks;

import java.util.concurrent.TimeUnit;

public class ClockBuilder {

    private long tickRate = 0;
    private TimeUnit tickRateUnit = null;

    public ClockBuilder withTickRate(long tickRate) {
        this.tickRate = tickRate;
        return this;
    }

    public ClockBuilder withTickRateUnit(TimeUnit tickRateUnit) {
        this.tickRateUnit = tickRateUnit;
        return this;
    }

    public Clock buildAutomaticallyTickingClock() {
        return new ManualClock();
    }

    public Clock buildManuallyTickingClock() {
        assertAutomaticClockParams();
        return new AutomaticClock(tickRate, tickRateUnit);
    }

    private void assertAutomaticClockParams() {
        if (tickRate == 0 || tickRateUnit == null) {
            throw new IllegalStateException(
                    "You have not set the tick rate, set the tick rate to 0, or have not set the "
                            + "tick rate unit, but also requested an automatically ticking Clock. "
                            + "Automatically ticking clocks require these parameters. Please set "
                            + "them both (set tick rate to non-zero), or request a manually ticking"
                            + " Clock.");
        }
    }
}
