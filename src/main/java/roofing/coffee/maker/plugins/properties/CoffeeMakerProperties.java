package roofing.coffee.maker.plugins.properties;

import java.util.concurrent.TimeUnit;

/**
 * CoffeeMakerSettings allows a front-end application that wraps a coffee make to configure the
 * coffee maker in a framework agnostic manner. Simply construct an instance of this class with all
 * of the configuration values required.
 * 
 * Though this class is framework agnostic, it works quite well with Spring and models Spring's
 * immutable <a
 * href="URL#https://docs.spring.io/spring-boot/docs/3.0.x/reference/html/features.html#features.external-config.typesafe-configuration-properties.constructor-binding>@ConfigurationProperties</a>
 * pattern.
 * 
 * @author nferraro-roofing
 *
 */
public final class CoffeeMakerProperties {

    private final Clock clock;
    private final Pot pot;
    private final Reservoir reservoir;
    private final WarmerPlate warmerPlate;

    public CoffeeMakerProperties(Clock clock,
            Pot pot,
            Reservoir reservoir,
            WarmerPlate warmerPlate) {
        this.clock = clock;
        this.pot = pot;
        this.reservoir = reservoir;
        this.warmerPlate = warmerPlate;
    }

    public long getClockTickDelay() {
        return clock.tickDelay;
    }

    public TimeUnit getClockTickDelayUnit() {
        return clock.delayUnit;
    }

    public int getPotMaxCapacityCups() {
        return pot.maxCapacityCups;
    }

    public int getReservoirMaxCapacityCups() {
        return pot.maxCapacityCups + reservoir.maxCapacityCupsOffset;
    }

    public long getReservoirTicksPerCupBrewed() {
        return clock.delayUnit.convert(1, TimeUnit.MINUTES) 
                / clock.tickDelay
                / reservoir.cupsPerMinuteBrewRate;
    }

    public long getWarmerPlateStayHotForTickLimit() {
        return clock.delayUnit.convert(1, TimeUnit.MINUTES) 
                / clock.tickDelay
                / warmerPlate.stayHotDurationMinutes;
    }

    public static class Clock {

        private final long tickDelay;
        private final TimeUnit delayUnit;

        public Clock(long tickDelay, TimeUnit delayUnit) {
            if (tickDelay <= 0) {
                throw new InvalidClockTickDelayPropertyException(tickDelay);
            }

            // delayUnit must be no more coarser than TimeUnit.SECONDS. If delayUnit is coarser than
            // seconds, converting it to seconds would return 0
            if (TimeUnit.SECONDS.convert(1, delayUnit) > 0) {
                throw new InvalidClockTimeUnitPropertyException(delayUnit.toString());
            }

            this.tickDelay = tickDelay;
            this.delayUnit = delayUnit;
        }
    }

    public static class Pot {

        private final int maxCapacityCups;

        public Pot(int maxCapacityCups) {
            this.maxCapacityCups = maxCapacityCups;
        }
    }

    public static class Reservoir {

        private final int maxCapacityCupsOffset;
        private final int cupsPerMinuteBrewRate;

        public Reservoir(int maxCapacityCupsOffset, int cupsPerMinuteBrewRate) {
            this.maxCapacityCupsOffset = maxCapacityCupsOffset;
            this.cupsPerMinuteBrewRate = cupsPerMinuteBrewRate;
        }
    }

    public static class WarmerPlate {

        private final int stayHotDurationMinutes;

        public WarmerPlate(int stayHotDurationMinutes) {
            this.stayHotDurationMinutes = stayHotDurationMinutes;
        }
    }
}