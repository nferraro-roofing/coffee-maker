package roofing.coffee.maker.busses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

/**
 * A BusMessage represents the current overall state of a CoffeeMaker via its components.
 * 
 * <p>
 * A BusMessage consists of four components:
 * </p>
 * 
 * <ul>
 * <li>{@link roofing.coffee.maker.components.BrewButton}</li>
 * <li>{@link roofing.coffee.maker.components.CoffeePot}</li>
 * <li>{@link roofing.coffee.maker.components.WarmerPlate}</li>
 * <li>{@link roofing.coffee.maker.components.WaterReservoir}</li>
 * </ul>
 * 
 * <p>
 * Construct instances of a BusMessage via its builder - provided by {@code builder()}. No public
 * constructor is available.
 * </p>
 * 
 * <p>
 * For a usage example, see the source of {@link roofing.coffee.maker.CoffeeMaker#asBusMessage()}.
 * </p>
 * 
 * @author nferraro-roofing
 *
 */
@ToString(includeFieldNames = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BusMessage {

    private static final BusMessageBuilder BUILDER = new BusMessageBuilder();
    
    /**
     * Returns a BusMessageBuilder, which can build BusMessages.
     * 
     * @return a BusMessageBuilder
     */
    public static BusMessageBuilder builder() {
        BUILDER.reset();
        return BUILDER; 
    }

    @Getter
    private final WaterReservoir reservoir;

    @Getter
    private final BrewButton button;

    @Getter
    private final CoffeePot pot;

    @Getter
    private final WarmerPlate warmer;

    /**
     * BusMessageBuilder builds instances of a BusMessage.
     * 
     * <p>
     * Clients of BusMessageBuilder may build a BusMessage by calling each {@code withX()} method on
     * a BusMessageBuilder, and then calling {@code build()}. Each of the CoffeeMaker's components
     * is required:
     * </p>
     * 
     * <ul>
     * <li>{@link roofing.coffee.maker.components.BrewButton}</li>
     * <li>{@link roofing.coffee.maker.components.CoffeePot}</li>
     * <li>{@link roofing.coffee.maker.components.WarmerPlate}</li>
     * <li>{@link roofing.coffee.maker.components.WaterReservoir}</li>
     * </ul>
     * 
     * <p>
     * Significantly, BusMessageBuilder enables a CoffeeMaker to communicate its current state
     * <i>without putting its actual components onto the bus.</i> Instead, BusMessages only ever
     * contain value-copies of each component rather than the CoffeeMaker's actual instance
     * variables. This approach enables the CoffeeMaker to communicate a <i>snapshot</i> of its
     * internal state, thus preventing any funny-business and race conditions between components.
     * </p>
     * 
     * <p>
     * Finally, the BusMessageBuilder strives to save memory by internally re-using one instance of
     * each of the CoffeeMaker's components. Each instance acts as the aforementioned value-copy
     * instance. But, significantly, BusMessageBuilder does not create a new value-copy each time a
     * client calls build().
     * </p>
     * 
     * <p>
     * Author's note: the low memory approach described above may create a race condition. Perhaps
     * we create a new BusMessage before each component is able to read the previous message from
     * the bus! In doing so, we would see an inconsistent state.
     * </p>
     * 
     * @author nferraro-roofing
     *
     */
    public static class BusMessageBuilder {

        // This approach is not thread safe unless we make a new BusMessageBuilder each
        // time we need one, but that defeats the whole purpose. Need some locking mechanism.
        private final WaterReservoir builderReservoir = WaterReservoir.busMessageInstance();
        private final BrewButton builderButton = new BrewButton();
        private final CoffeePot builderPot = CoffeePot.busMessageInstance();
        private final WarmerPlate builderWarmer = WarmerPlate.busMessageInstance();
        
        private boolean isReservoirSet = false;
        private boolean isButtonSet = false;
        private boolean isPotSet = false;
        private boolean isWarmerSet = false;

        /**
         * Causes this BusMessageBuilder to update its internal state with a copy of the provided
         * WaterReservoir.
         * 
         * @param reservoir the WaterReservoir to copy
         * @return this BusMessageBuilder
         */
        public BusMessageBuilder withWaterReservoir(WaterReservoir reservoir) {
            this.builderReservoir.refreshFrom(reservoir);
            isReservoirSet = true;
            return this;
        }

        /**
         * Causes this BusMessageBuilder to update its internal state with a copy of the provided
         * BrewButton.
         * 
         * @param button the BrewButton to copy
         * @return this BusMessageBuilder
         */
        public BusMessageBuilder withBrewButton(BrewButton button) {
            this.builderButton.refreshFrom(button);
            isButtonSet = true;
            return this;
        }

        /**
         * Causes this BusMessageBuilder to update its internal state with a copy of the provided
         * CoffeePot.
         * 
         * @param pot the CoffeePot to copy
         * @return this BusMessageBuilder
         */
        public BusMessageBuilder withCoffeePot(CoffeePot pot) {
            this.builderPot.refreshFrom(pot);
            isPotSet = true;
            return this;
        }

        /**
         * Causes this BusMessageBuilder to update its internal state with a copy of the provided
         * WarmerPlate.
         * 
         * @param warmer the WarmerPlate to copy
         * @return this BusMessageBuilder
         */
        public BusMessageBuilder withWarmerPlate(WarmerPlate warmer) {
            this.builderWarmer.refreshFrom(warmer);
            isWarmerSet = true;
            return this;
        }

        /**
         * Create a BusMessage from the components provided through each call to {@code withX()}.
         * 
         * <p>
         * Each of the CoffeeMaker's components is required. Throws an IllegalStateException if any
         * of these is not set.
         * </p>
         * 
         * <ul>
         * <li>{@link roofing.coffee.maker.components.BrewButton}</li>
         * <li>{@link roofing.coffee.maker.components.CoffeePot}</li>
         * <li>{@link roofing.coffee.maker.components.WarmerPlate}</li>
         * <li>{@link roofing.coffee.maker.components.WaterReservoir}</li>
         * </ul>
         * 
         * @return a BusMessage containing copies of each CoffeeMaker component provided through
         *         calls to {@code withX()}
         * @throws IllegalStateException if any of the CoffeeMaker's components is not set.
         */
        public BusMessage build() {
            assertState();
            return new BusMessage(builderReservoir, builderButton, builderPot, builderWarmer);
        }

        private void assertState() {
            if (!isReservoirSet) {
                throw new IllegalStateException(
                        "A WaterReservoir is required to build a BusMessage, but none was "
                                + "provided to this builder. Please call withWaterReservoir().");
            }

            if (!isButtonSet) {
                throw new IllegalStateException(
                        "A BrewButton is required to build a BusMessage, but none was "
                                + "provided to this builder. Please call withBrewButton().");
            }

            if (!isPotSet) {
                throw new IllegalStateException(
                        "A CoffeePot is required to build a BusMessage, but none was "
                                + "provided to this builder. Please call withCoffeePot().");
            }

            if (!isWarmerSet) {
                throw new IllegalStateException(
                        "A WarmerPlate is required to build a BusMessage, but none was "
                                + "provided to this builder. Please call withWarmerPlate().");
            }
        }

        private void reset() {
            builderReservoir.reset();
            builderButton.reset();
            builderPot.reset();
            builderWarmer.reset();

            isReservoirSet = false;
            isButtonSet = false;
            isPotSet = false;
            isWarmerSet = false;
        }
    }
}