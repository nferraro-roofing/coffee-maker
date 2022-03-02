package roofing.coffee.maker.plugins.properties;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * CoffeeMakerProperties enables clients to tune the behavior of a CoffeeMaker.
 * 
 * <p>
 * {@code CoffeeMakerCreator::create(CoffeeMakerProperties)} creates CoffeeMaker instances based on
 * the application properties provided in the CoffeeMakerProperties argument.
 * </p>
 * 
 * <p>
 * CoffeeMakerProperties is framework-agnostic. Clients may load it with data from whatever
 * application property framework they desire. However, this class works quite well with Spring and
 * models Spring's immutable <a href=
 * "https://docs.spring.io/spring-boot/docs/3.0.x/reference/html/features.html#features.external-config.typesafe-configuration-properties">ConfigurationProperties</a>
 * strategy.
 * </p>
 * 
 * <p>
 * This class's inner classes describe the application properties that affect CoffeeMaker instances.
 * The inner classes include:
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps}</li>
 * <li>{@link roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps}</li>
 * <li>{@link roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps}</li>
 * <li>{@link roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps}</li>
 * </ul>
 * 
 * @see roofing.coffee.maker.CoffeeMakerCreator#create(CoffeeMakerProperties)
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

    /**
     * Returns the delay between ticks of a CoffeeMaker's clock.
     * 
     * <p>
     * This value alone means nothing. Combine it with the {@code delayUnit} to make any sense.
     * Combined, they describe the rate at which a CoffeeMaker's internal clock ticks.
     * </p>
     * 
     * @see roofing.coffee.maker.plugins.properties.CoffeeMakerProperties#getClockTickDelayUnit()
     * @return the delay between ticks of a CoffeeMaker's clock.
     */
    public long getClockTickDelay() {
        return clock.tickDelay;
    }

    /**
     * Returns the delay unit between ticks of a CoffeeMaker's clock.
     * 
     * <p>
     * This value alone means nothing. Combine it with the {@code tickDelay} to make any sense.
     * Combined, they describe the rate at which a CoffeeMaker's internal clock ticks.
     * </p>
     * 
     * @see roofing.coffee.maker.plugins.properties.CoffeeMakerProperties#getClockTickDelay()
     * @return the delay between ticks of a CoffeeMaker's clock.
     */
    public TimeUnit getClockTickDelayUnit() {
        return clock.delayUnit;
    }

    /**
     * Returns the max cups of coffee that a CoffeeMaker's CoffeePot should hold.
     * 
     * <p>
     * A CoffeeMaker's WaterReservoir max capacity always exceeds this value by 1.
     * </p>
     * 
     * @return the max cups of coffee that a CoffeeMaker's CoffeePot should hold.
     */
    public int getPotMaxCapacityCups() {
        return pot.maxCapacityCups;
    }

    /**
     * Returns the number of ticks of a CoffeeMaker's clock that elapse before the CoffeeMaker brews
     * a cup of coffee.
     * 
     * <p>
     * CoffeeMakerProperties calculates this value based on the ClockProps' ticks per minute and the
     * ReserviourProps {@code cupsPerMinuteBrewRate}: (ticks per minute) /
     * {@code cupsPerMinuteBrewRate}.
     * </p>
     * 
     * @return the number of ticks of a CoffeeMaker's clock that elapse before the CoffeeMaker brews
     *         a cup of coffee.
     */
    @ToString.Include
    public long getReservoirTicksPerCupBrewed() {
        return clock.ticksPerMinute / reservoir.cupsPerMinuteBrewRate;
    }

    /**
     * Returns the number of ticks of a CoffeeMaker's clock that elapse before the CoffeeMaker's
     * warmer plate turns off.
     * 
     * <p>
     * A CoffeeMaker's WarmerPlate remains hot for a short time after brewing completes. This value
     * describes that duration in terms of ticks of a clock.
     * </p>
     * 
     * <p>
     * CoffeeMakerProperties calculates this value based on the ClockProps' ticks per minute and the
     * WarmerProps' {@code stayHotDurationMinutes}: (ticks per minute) /
     * {@code stayHotDurationMinutes}.
     * </p>
     * 
     * @return the number of ticks of a CoffeeMaker's clock that elapse before the CoffeeMaker's
     *         warmer plate turns off.
     */
    @ToString.Include
    public long getWarmerPlateStayHotForTickLimit() {
        return clock.ticksPerMinute * warmerPlate.stayHotDurationMinutes;
    }

    /**
     * ClockProps affect the rate at which a CoffeeMaker's internal clock ticks.
     * 
     * <p>
     * A CoffeeMaker's internal clock schedules the rate at which each components updates itself
     * based on the state of every other component. A faster-ticking clock enables higher accuracy
     * at the cost of CPU.
     * </p>
     * 
     * @author nferraro-roofing
     *
     */
    @ToString(includeFieldNames = true)
    public static class ClockProps {

        private final long tickDelay;
        private final TimeUnit delayUnit;
        private final long ticksPerMinute;

        /**
         * Create a ClockProps instance that instructs a CoffeeMaker's clock to tick after every
         * {@code tickDelay} has passed in the provided {@code delayUnit}.
         * 
         * <p>
         * For example, a {@code tickDelay} of 10 and {@code delayUnit TimeUnit.Seconds} would
         * instruct a CoffeeMaker's clock to tick once every ten seconds. This rate is referred to
         * as the ticks per minute.
         * </p>
         * 
         * <p>
         * Some constraints exist on these parameters. First, {@code tickDelay} must exceed 0;
         * second, {@code delayUnit} must be no coarser than {@code TimeUnit.Seconds}; finally, the
         * combined effect of {@code tickDelay} and {@code delayUnit} would cause a clock to tick
         * fewer than once per minute.
         * </p>
         * 
         * @param tickDelay the delay between a CoffeeMaker's clock ticks
         * @param delayUnit the delay unit to be applied to {@code tickDelay}
         * @throws InvalidClockTickDelayPropertyException when {@code tickDelay} does not exceed 0
         * @throws InvalidClockTimeUnitPropertyException when {@code delayUnit} is coarser than
         *         {@code TimeUnit.Seconds}
         * @throws InvalidClockTicksPerMinuteException when {@code tickDelay} and {@code delayUnit}
         *         would cause a clock to tick fewer than once per minute.
         */
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
            // delayUnit must be no more coarse than TimeUnit.SECONDS. If delayUnit is coarser than
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

    /**
     * PotProps affect the capacity of a CoffeeMaker's CoffeePot and the capacity of a CoffeeMaker's
     * WaterReservoir.
     * 
     * @author nferraro-roofing
     *
     */
    @ToString(includeFieldNames = true)
    public static class PotProps {

        private final int maxCapacityCups;

        /**
         * Construct instances of PotProps with one integer value - {@code maxCapacityCups} -
         * indicating the max cups of coffee that a CoffeeMaker's coffee pot can hold.
         * 
         * <p>
         * A CoffeeMaker's WaterReservoir always exceeds this value by 1.
         * </p>
         * 
         * @param maxCapacityCups the max cups of coffee that a CoffeeMaker's coffee pot can hold.
         * @throws IllegalArgumentException when {@code maxCapacityCups} does not exceed 0.
         */
        public PotProps(int maxCapacityCups) {
            if (maxCapacityCups <= 0) {
                throw new IllegalArgumentException(
                        "A CoffeeMaker's maxCapacityCups is required and must exceed 0. The provided maxCapacityCups was "
                                + maxCapacityCups);
            }

            this.maxCapacityCups = maxCapacityCups;
        }
    }

    /**
     * ReservoirProps affect the rate at which a CoffeeMaker can brew coffee.
     * 
     * @author nferraro-roofing
     *
     */
    @ToString(includeFieldNames = true)
    public static class ReservoirProps {

        private final int cupsPerMinuteBrewRate;

        /**
         * Construct instances of PotProps with one integer value - {@code cupsPerMinuteBrewRate} -
         * indicating the cups of coffee per minute that a CoffeeMaker brews.
         * 
         * @param cupsPerMinuteBrewRate the cups of coffee per minute that a CoffeeMaker brews.
         * @throws IllegalArgumentException when {@code cupsPerMinuteBrewRate} does not exceed 0.
         */
        public ReservoirProps(int cupsPerMinuteBrewRate) {
            if (cupsPerMinuteBrewRate <= 0) {
                throw new IllegalArgumentException(
                        "A ReservoirProps cupsPerMinuteBrewRate is required and must exceed 0. The provided cupsPerMinuteBrewRate was "
                                + cupsPerMinuteBrewRate);
            }

            this.cupsPerMinuteBrewRate = cupsPerMinuteBrewRate;
        }
    }

    /**
     * WarmerPlateProps affects the duration, in minutes, that a CoffeeMaker's WarmerPlate remains
     * hot after brewing completes.
     * 
     * @author nferraro-roofing
     *
     */
    @ToString(includeFieldNames = true)
    public static class WarmerPlateProps {

        private final int stayHotDurationMinutes;

        /**
         * Construct instances of WarmerPlateProps with one integer value -
         * {@code stayHotDurationMinutes} - indicating the duration, in minutes, that a
         * CoffeeMaker's WarmerPlate remains hot after brewing completes.
         * 
         * @param stayHotDurationMinutes the cups of coffee per minute that a CoffeeMaker brews.
         * @throws IllegalArgumentException when {@code stayHotDurationMinutes} does not exceed 0.
         */
        public WarmerPlateProps(int stayHotDurationMinutes) {
            if (stayHotDurationMinutes <= 0) {
                throw new IllegalArgumentException(
                        "A WarmerPlateProps stayHotDurationMinutes is required and must exceed 0. The provided stayHotDurationMinutes was "
                                + stayHotDurationMinutes);
            }

            this.stayHotDurationMinutes = stayHotDurationMinutes;
        }
    }
}