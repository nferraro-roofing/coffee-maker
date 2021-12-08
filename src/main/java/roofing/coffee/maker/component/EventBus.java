package roofing.coffee.maker.component;

import java.util.List;
import java.util.function.Consumer;

/**
 * An EventBus in a coffee maker communicate state information to
 * subscribers of the bus. 
 * 
 * Each EventBus can communicate one type of information - e.g. 
 * {@code EventBus<RelifValveState>}.
 * 
 * The idea is that zero or many components of a coffee maker can
 * subscribe to an EventBus in order to react to changes in the
 * coffee maker's state.
 * 
 * @author nferraro
 */
public class EventBus<T> {

  private final List<Consumer<T>> callbacks; 

  public EventBus(List<Consumer<T>> callbacks) {
    this.callbacks = callbacks;
  }

  void publish(T event) {
    for (Consumer<T> c: callbacks) {
      c.accept(event);
    }
  }

}