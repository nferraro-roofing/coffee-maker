package roofing.coffee.maker.busses;

/**
 * T is "Type Refresh-able From"
 * 
 * @author nferraro-roofing
 *
 * @param <T>
 */
public interface BusComponent<T> {

    void readBusMessage(BusMessage message);

    void refreshFrom(T from);

    void reset();
}