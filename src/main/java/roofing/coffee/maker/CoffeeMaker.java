package roofing.coffee.maker;

import lombok.ToString;
import roofing.coffee.maker.busses.BusMessage;
import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

/**
 * CoffeeMaker represents an actual coffee maker's basic capabilities - e.g. brewing coffee, warming
 * brewed coffee, etc.
 * 
 * <p>
 * A CoffeeMaker conceptually consists of a few components (found below). However, clients of this
 * class should not interact with these components directly; clients should only interact with the
 * CoffeeMaker and any component that its methods may return.
 * </p>
 * 
 * <ul>
 * <li>{@code CoffeePot} - holds coffee. Clients may interact with this component directly.</li>
 * <li>{@code BrewButton} - enables the user to request some coffee. Clients may interact with this
 * component indirectly via the CoffeeMaker.</li>
 * <li>{@code WaterReservoir} - enables the user to fill water into the CoffeeMaker. Clients may
 * interact with this component indirectly via the CoffeeMaker.</li>
 * <li>{@code WarmerPlate} - warms the brewed coffee. Clients may interact with this component
 * indirectly via the CoffeeMaker.</li>
 * </ul>
 * 
 * <p>
 * Usage of this class mimics that of a real coffee maker.
 * </p>
 * 
 * <p>
 * To brew coffee,
 * </p>
 *
 * <ol>
 * <li>{@code fill(cupsOfWater)}. Be sure to check the max capacity first via
 * {@code getMaxWaterCapacityCups()}.</li>
 * <li>{@code pressBrewButton()}, which prompts the CoffeeMaker to start brewing.</li>
 * </ol>
 * 
 * <p>
 * At any point the user can understand the state of the CoffeeMaker via a few reporting methods.
 * These methods mimic some of the indicators that a real coffee maker would physically display.
 * </p>
 * 
 * <ul>
 * <li>{@code cupsOfWater()} indicates the cups of water in the CoffeeMaker at this time. As brew
 * progresses, this number decreases one cup at a time.</li>
 * <li>{@code cupsOfCoffee()} indicates the cups of coffee in the CoffeeMaker at this time. This
 * number starts at 0, and increases one cup at a time as brew progresses.</li>
 * <li>{@code isBrewing()} indicates if the CoffeeMaker is still brewing coffee.</li>
 * <li>{@code isWarmerPlateOn()} indicates if CoffeeMaker's WarmerPlate is on. The WarmerPlate warms
 * the CoffeePot while the CoffeeMaker is brewing and for a short period thereafter.</li>
 * </ul>
 * 
 * <p>
 * Once the CoffeeMaker is done brewing coffee, the user can interact with the CoffeeMaker via the
 * CoffeePot. The CoffeePot allows the user to pour out some precious coffee!
 * </p>
 * 
 * <ol>
 * <li>{@code removePot()} returns a CoffeePot, which allows the user to acquire coffee via
 * {@code pourOutCoffee()}.</li>
 * <li>{@code replacePot()} enables the user to replace the pot after removing it.</li>
 * </ol>
 * 
 * <p>
 * Note: the user can neither remove the pot once it's already been removed, nor can the user
 * replace the pot if it's already present. In other words, these methods mimic a real life coffee
 * pot, which physically has or does not have a pot sitting on top of its warmer plate.
 * </p>
 * 
 * @author nferraro-roofing
 */
@ToString
public class CoffeeMaker {

    private final WaterReservoir reservoir;
    private final BrewButton button;
    private final CoffeePot pot;
    private final WarmerPlate warmer;

    /**
     * Construct a CoffeeMaker with its components.
     * 
     * <p>
     * Clients may not invoke this constructor; its visibility enables only
     * {@code CoffeeMakerCreator} to construct a CoffeeMaker. Clients should interact with
     * {@code CoffeeMakerCreator}.
     * </p>
     * 
     * @param reservoir holds water.
     * @param button enables the user to start brewing coffee.
     * @param pot holds coffee.
     * @param warmer warms the coffee.
     */
    CoffeeMaker(WaterReservoir reservoir, BrewButton button, CoffeePot pot, WarmerPlate warmer) {
        this.reservoir = reservoir;
        this.button = button;
        this.pot = pot;
        this.warmer = warmer;
    }

    /**
     * Get the maximum number of cups of water that this reservoir can hold.
     * 
     * <p>
     * Application settings determine a CoffeeMaker's maximum water capacity.
     * </p>
     * 
     * @see roofing.coffee.maker.plugins.properties.CoffeeMakerProperties
     * @return the number of cups of water that this CoffeeMaker can hold.
     */
    public int getMaxWaterCapacityCups() {
        return reservoir.maxCapacityCups();
    }

    /**
     * Fill the CoffeeMaker with {@code cupsOfWater} water.
     * 
     * <p>
     * Take care not to over-fill the CoffeeMaker. At any time, this method accepts
     * {@code getMaxWaterCapacityCups()} - {@code cupsOfWater()} - i.e. the max capacity less the
     * water currently in the CoffeeMaker.
     * </p>
     * 
     * @param cupsOfwater - the cups of water to fill into the CoffeeMaker.
     * @throws IllegalArgumentException if {@code cupsOfwater} would over-fill the CoffeeMaker.
     */
    public void fill(int cupsOfwater) {
        reservoir.fill(cupsOfwater);
    }

    /**
     * Request that the CoffeeMaker start brewing coffee.
     * 
     * <p>
     * The user may <i>request</i> that brewing starts, but the CoffeeMaker will not actually brew
     * coffee unless water is present in the reservoir. Therefore, please {@code fill()} the
     * CoffeeMaker first.
     * </p>
     * 
     * <p>
     * Similarly, the user may manually stop the brewing process by pressing this button after
     * brewing has completed.
     * </p>
     * 
     * @see roofing.coffee.maker.CoffeeMaker#isBrewing()
     */
    public void pressBrewButton() {
        button.pressBrewButton();
    }

    /**
     * Returns the cups of water currently present in the CoffeeMaker.
     * 
     * <p>
     * This value increases as the user calls {@code fill()}. It decreases, one cup at a time, as
     * the CoffeeMaker brews coffee.
     * </p>
     * 
     * @see roofing.coffee.maker.CoffeeMaker#isBrewing()
     * @return the cups of water currently present in the CoffeeMaker
     */
    public int cupsOfWater() {
        return reservoir.cupsOfWater();
    }

    /**
     * Returns the cups of coffee currently present in the CoffeeMaker.
     * 
     * <p>
     * This value starts at 0, and increases, one cup at a time, as the CoffeeMaker brews coffee.
     * </p>
     * 
     * @see roofing.coffee.maker.CoffeeMaker#isBrewing()
     * @return the cups of coffee currently present in the CoffeeMaker
     */
    public int cupsOfCoffee() {
        return pot.cupsOfCoffee();
    }

    /**
     * Returns the cups of coffee currently present in the CoffeeMaker.
     * 
     * <p>
     * This value starts at 0, and increases, one cup at a time, as the CoffeeMaker brews coffee.
     * </p>
     * 
     * @see roofing.coffee.maker.CoffeeMaker#isBrewing()
     * @return the cups of coffee currently present in the CoffeeMaker
     */
    public boolean isWarmerPlateOn() {
        return warmer.isHot();
    }

    /**
     * Returns true when the CoffeeMaker is brewing, and false otherwise.
     * 
     * <p>
     * When initialized, a CoffeeMaker is not brewing coffee. It begins brewing coffee as soon as
     * the user {@code fill()}s at least one cup of water and requests brew via
     * {@code pressBrewButton()}.
     * </p>
     * 
     * <p>
     * Thereafter, the CoffeeMaker brews one cup of coffee at a time until the WaterReservoir is
     * empty, the CoffeePot is full, the user removes the CoffeePot, or the user manually cancels
     * the brew via {@code pressBrewbutton()}.
     * </p>
     * 
     * <p>
     * Application settings determine the rate of brewing.
     * </p>
     * 
     * @see roofing.coffee.maker.plugins.properties.CoffeeMakerProperties
     * @return the cups of coffee currently present in the CoffeeMaker
     */
    public boolean isBrewing() {
        return reservoir.isBrewing();
    }

    /**
     * Remove the pot from the CoffeeMaker.
     * 
     * <p>
     * Removing the pot enables the user to {@code pourOutCoffee(cupsOfCoffee)}.
     * </p>
     * 
     * <p>
     * Users must not remove the CoffeePot once it's already missing - e.g. the user may not call
     * this method twice in a row without calling {@code replacePot()} in between.
     * </p>
     * 
     * <p>
     * The CoffeeMaker's manufacturer recommends that the user await brew completion before removing
     * the CoffeePot. However, eager users may safely remove the CoffeePot prior to brew completion.
     * Brewing will pause until the user replaces the pot via {@code replacePot()}.
     * </p>
     * 
     * @see roofing.coffee.maker.components.CoffeePot#pourOutCoffee(int)
     * @return this CoffeeMaker's CoffeePot
     * @throws IllegalStateException if the user attempts to remove the CoffeePot that is already
     *         removed.
     */
    public CoffeePot removePot() {
        if (warmer.hasPot()) {
            warmer.removePot();
            return pot;
        }

        throw new IllegalStateException(
                "The coffee pot has been removed previously without replacement. Please replace "
                        + "the pot via replacePot() before removing again.");
    }

    /**
     * Replace the pot into the CoffeeMaker after removing it via {@code removePot()}.
     * 
     * <p>
     * Users must not replace a CoffeePot that already resides in the CoffeeMaker - i.e users must
     * first call {@code removePot()} before calling this method.
     * </p>
     * 
     * @throws IllegalStateException if the user attempts to replace the CoffeePot that is already
     *         present.
     */
    public void replacePot() {
        if (!warmer.hasPot()) {
            warmer.replacePot();

        } else {
            throw new IllegalStateException(
                    "The coffee pot is currently on the warmer plate. Cannot replace a pot that "
                            + "is already present! Please remove the pot first via removePot().");
        }
    }

    /**
     * Serialize this CoffeeMaker into a form suitable for inter-communication between CoffeeMaker
     * components.
     * 
     * <p>
     * <b>Note: external clients of this package should NOT call this method. It is intended for
     * internal usage only. </b>
     * </p>
     * 
     * <p>
     * A BusMessage represents the CoffeeMaker's current internal state. It contains a <i>copy</i>
     * of the components contained herein.
     * </p>
     * 
     * @see roofing.coffee.maker.busses.BusMessage
     * @return BusMessage - a representation of the CoffeeMaker's current internal state.
     */
    public BusMessage asBusMessage() {
        return BusMessage.builder()
                .withBrewButton(button)
                .withCoffeePot(pot)
                .withWarmerPlate(warmer)
                .withWaterReservoir(reservoir)
                .build();
    }
}