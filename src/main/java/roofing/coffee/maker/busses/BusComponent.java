package roofing.coffee.maker.busses;

/**
 * A BusComponent interacts with other BusComponents (in the form of a BusMessage) in order to
 * maintain its internal state.
 * 
 * <p>
 * Each of the CoffeeMaker's core components implements this interface:
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
 * A BusComponent's primary concern revolves around reading BusMessages from other BusComponents. A
 * BusMessage contains other BusComponents, and a Bus synchronizes each component to every other
 * component. This scheme enables BusComponents to maintain a consistent overall state. More to the
 * point, since each of a CoffeeMaker's internal components implements this interface, this scheme
 * enables an entire <i>CoffeeMaker</i> to maintain a consistent state.
 * </p>
 * 
 * <p>
 * Additionally, a BusComponent may {@code refresh} itself from some other BusComponent of the same
 * type. In doing so, a BusComponent may communicate its internal state to other BusComponents via
 * copies of itself. This capability, along with {@code reset()} enables a low-memory approach to
 * inter-communication between BusComponents. For more information, see
 * {@link roofing.coffee.maker.busses.BusMessage.BusMessageBuilder}.
 * </p>
 * 
 * @see roofing.coffee.maker.busses.BusMessage.BusMessageBuilder
 * @see roofing.coffee.maker.busses.Clock#tick()
 * @see roofing.coffee.maker.busses.Bus#update(BusMessage)
 * @author nferraro-roofing
 * @param <T> The type from which this BusComponent understands how to refresh itself.
 */
public interface BusComponent<T> {

    /**
     * Causes this BusComponent to update it's internal state based on the provided BusMessage -
     * i.e. based on other BusComponents.
     * 
     * @param message the state of other BusComponents in a CoffeeMaker.
     */
    void readBusMessage(BusMessage message);

    /**
     * Sets this BusComponents internal state to match that of the {@code T from}.
     * 
     * <p>
     * Type T must match the type of this BusComponent. E.g. a CoffeePot cannot refresh its internal
     * state from a WaterReservoir; it can only refresh its internal state from another CoffeePot.
     * </p>
     * 
     * <p>
     * BusComponent does not restrict type T to extend BusComponent, but T effectively will always
     * extend BusComponent. If a counter-example makes sense, then so be it!
     * </p>
     * 
     * @param from the other BusComponent to mimic
     */
    void refreshFrom(T from);

    /**
     * Reset this BusComponent's internal state to whatever it would have been upon its inception.
     * 
     * <p>
     * In general, this method only makes sense in order to create a clean slate after polluting
     * this BusComponent's internal state via calls to {@code readBusMessage} and
     * {@code refreshFrom}. See the source of
     * {@link roofing.coffee.maker.busses.BusMessage.BusMessageBuilder} for a usage example.
     * </p>
     * 
     */
    void reset();
}