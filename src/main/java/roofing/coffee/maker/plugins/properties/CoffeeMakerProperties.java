package roofing.coffee.maker.plugins.properties;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * CoffeeMakerSettings allows a front-end application that wraps a coffee make to configure the
 * coffee maker in a framework-agnostic manner. Simply construct an instance of this class with all
 * of the configuration values required.
 * 
 * Though this class is framework-agnostic, it works quite well with Spring and models Spring's
 * immutable <a href="https://docs.spring.io/spring-boot/docs/3.0.x/reference/html/features.html#features.external-config.typesafe-configuration-properties">@ConfigurationProperties</a> strategy.
 * 
 * @author nferraro-roofing
 */
@ToString(includeFieldNames = true)
@AllArgsConstructor
public final class CoffeeMakerProperties {

    @NonNull
    private final ClockProps clock;

    @NonNull
    private final PotProps pot;

    @NonNull
    private final ReservoirProps reservoir;

    @NonNull
    private final WarmerPlateProps warmerPlate;

    public long getClockTickDelay() {
        return clock.tickDelay;
    }

    public TimeUnit getClockTickDelayUnit() {
        return clock.delayUnit;
    }

    public int getPotMaxCapacityCups() {
        return pot.maxCapacityCups;
    }

    @ToString.Include
    public long getReservoirTicksPerCupBrewed() {
        return clock.ticksPerMinute / reservoir.cupsPerMinuteBrewRate;
    }

    @ToString.Include
    public long getWarmerPlateStayHotForTickLimit() {
        return clock.ticksPerMinute * warmerPlate.stayHotDurationMinutes;
    }

    @ToString(includeFieldNames = true)
    public static class ClockProps {

        private final long tickDelay;
        private final TimeUnit delayUnit;
        private final long ticksPerMinute;

        public ClockProps(long tickDelay, TimeUnit delayUnit) {
            assertTickDelay(tickDelay);
            assertTimeUnit(delayUnit);

            this.tickDelay = tickDelay;
            this.delayUnit = delayUnit;
            ticksPerMinute = delayUnit.convert(1, TimeUnit.MINUTES) / tickDelay;

            assertTicksPerMinute();
        }

        private void assertTickDelay(long tickDelay) {
            if (tickDelay <= 0) {
                throw new InvalidClockTickDelayPropertyException(tickDelay);
            }
        }

        private void assertTimeUnit(TimeUnit delayUnit) {
            // delayUnit must be no more coarser than TimeUnit.SECONDS. If delayUnit is coarser than
            // seconds, converting it to seconds would MINUTES (the next coarser TimeUnit) would 
            // return 0
            if (TimeUnit.MINUTES.convert(1, delayUnit) > 0) {
                throw new InvalidClockTimeUnitPropertyException(delayUnit.toString());
            }
        }

        private void assertTicksPerMinute() {
            // A clock should not tick slower than once per minute
            if (ticksPerMinute <= 0) {
                throw new InvalidClockTicksPerMinuteException(tickDelay, delayUnit);
            }
        }
    }

    @ToString(includeFieldNames = true)
    @AllArgsConstructor
    public static class PotProps {
        private final int maxCapacityCups;
    }

    @ToString(includeFieldNames = true)
    @AllArgsConstructor
    public static class ReservoirProps {
        private final int cupsPerMinuteBrewRate;
    }

    @ToString(includeFieldNames = true)
    @AllArgsConstructor
    public static class WarmerPlateProps {
        private final int stayHotDurationMinutes;
    }
}