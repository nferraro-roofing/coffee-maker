package roofing.coffee.maker.busses;

import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

/**
 * Please do NOT override equals and hashcode unless we also update bus. We very much intend for
 * equals() to check instance vs instance. For this reason, we have explicitly overriden equals and
 * hashcode but merely call the super class - Object
 * 
 * @author nferraro-roofing
 *
 */
public class BusMessage {

    private static final BusMessageBuilder BUILDER = new BusMessageBuilder();
    
    public static BusMessageBuilder builder() {
        BUILDER.reset();
        return BUILDER; 
    }

    private final WaterReservoir reservoir;
    private final BrewButton button;
    private final CoffeePot pot;
    private final WarmerPlate warmer;

    private BusMessage(WaterReservoir reservoir,
            BrewButton button,
            CoffeePot pot,
            WarmerPlate warmer) {
        this.reservoir = reservoir;
        this.button = button;
        this.pot = pot;
        this.warmer = warmer;
    }

    public WaterReservoir getReservoir() {
        return reservoir;
    }

    public BrewButton getButton() {
        return button;
    }

    public CoffeePot getPot() {
        return pot;
    }

    public WarmerPlate getWarmer() {
        return warmer;
    }

    /**
     * Use the builder to enable the coffee maker to communicate its current state without putting
     * its actual components onto the bus. I.e. the bus should contain a value-copy of each
     * component and not the actual instance.
     * 
     * The coffee maker or the BusMessage constructor could new up the copied components, but it
     * feels cleaner to use a builder. I don't want anything to new up domain components except for
     * components that are explicitly intended for that purpose.
     * 
     * @author nferraro-roofing
     *
     */
    public static class BusMessageBuilder {

        // TODO: this approach is not thread safe unless we make a new BusMessageBuilder each
        // time we need one, but that defeats the whole purpose. Need some locking mechanism
        private final WaterReservoir reservoir = WaterReservoir.busMessageInstance();
        private final BrewButton button = new BrewButton();
        private final CoffeePot pot = CoffeePot.busMessageInstance();
        private final WarmerPlate warmer = WarmerPlate.busMessageInstance();
        
        private boolean isReservoirSet = false;
        private boolean isButtonSet = false;
        private boolean isPotSet = false;
        private boolean isWarmerSet = false;

        public BusMessageBuilder withWaterReservoir(WaterReservoir reservoir) {
            this.reservoir.refreshFrom(reservoir);
            isReservoirSet = true;
            return this;
        }

        public BusMessageBuilder withBrewButton(BrewButton button) {
            this.button.refreshFrom(button);
            isButtonSet = true;
            return this;
        }

        public BusMessageBuilder withCoffeePot(CoffeePot pot) {
            this.pot.refreshFrom(pot);
            isPotSet = true;
            return this;
        }

        public BusMessageBuilder withWarmerPlate(WarmerPlate warmer) {
            this.warmer.refreshFrom(warmer);
            isWarmerSet = true;
            return this;
        }

        public BusMessage build() {
            assertState();
            return new BusMessage(reservoir, button, pot, warmer);
        }

        private void assertState() {
            if (!isReservoirSet) {
                throw new IllegalStateException(
                        "A WaterReservoir is required to build a BusMessage,but none was "
                                + "provided to this builder. Please call withWaterReservoir().");
            }

            if (!isButtonSet) {
                throw new IllegalStateException(
                        "A BrewButton is required to build a BusMessage,  but none was "
                                + "provided to this builder. Please call withBrewButton().");
            }

            if (!isPotSet) {
                throw new IllegalStateException(
                        "A CoffeePot is required to build a BusMessage,  but none was "
                                + "provided to this builder. Please call withCoffeePot().");
            }

            if (!isWarmerSet) {
                throw new IllegalStateException(
                        "A WarmerPlate is required to build a BusMessage,  but none was "
                                + "provided to this builder. Please call withWarmerPlate().");
            }
        }

        private void reset() {
            reservoir.reset();
            button.reset();
            pot.reset();
            warmer.reset();

            isReservoirSet = false;
            isButtonSet = false;
            isPotSet = false;
            isWarmerSet = false;
        }
    }
}