package roofing.coffee.maker.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * An EventBus in a coffee maker communicate state information to subscribers of the bus.
 * 
 * Each EventBus can communicate one type of information - e.g. {@code EventBus<RelifValveState>}.
 * 
 * The idea is that zero or many components of a coffee maker can subscribe to an EventBus in order
 * to react to changes in the coffee maker's state.
 * 
 * @author nferraro
 */
public class EventBus<T> {

  private final List<Consumer<T>> callbacks = ArrayList<>();

    public void subscribe(Consumer<T> callback) {
        this.callbacks.add(callback);
    }

    public void publish(T event) {
        for (Consumer<T> c : callbacks) {
            c.accept(event);
        }
    }

}