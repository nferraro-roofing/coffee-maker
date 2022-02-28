package roofing.coffee.maker.busses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.ToString;

/**
 * A Bus synchronizes the components ({@code BusComponent}) of a CoffeeMaker.
 * 
 * <p>
 * A Bus works alongside a Clock in order to ask a set of synchronized components to update their
 * internal state upon each tick of the clock.
 * </p>
 * 
 * @see roofing.coffee.maker.busses.Clock
 * @see roofing.coffee.maker.busses.BusComponent
 * @author nferraro-roofing
 *
 */
@ToString
public class Bus {

    // We expect four synchedComponents: BrewButton, CoffeePot, WarmerPlate, and WaterReservoir
    private final List<BusComponent<?>> synchedComponents = new ArrayList<>(4);

    /**
     * Construct a Bus that synchronizes the provided {@code components}.
     * 
     * @param components the BusComponent instances to synchronize.
     */
    public Bus(BusComponent<?>... components) {
        synchedComponents.addAll(Arrays.asList(components));
    }

    /**
     * Inform all synchronized components to update their internal state based on the provided
     * BusMessage.
     * 
     * @param message the message to send to each synchronized component
     */
    public void update(BusMessage message) {
        for (BusComponent<?> c : synchedComponents) {
            c.readBusMessage(message);
        }
    }
}
