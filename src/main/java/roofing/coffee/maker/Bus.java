package roofing.coffee.maker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Bus {

    private final List<Consumer<CoffeeMaker>> callbacks = new ArrayList<>();

    public void subscribe(Consumer<CoffeeMaker> callback) {
        this.callbacks.add(callback);
    }

    public void publish(CoffeeMaker coffeeMaker) {
        for (Consumer<CoffeeMaker> c : callbacks) {
            c.accept(coffeeMaker);
        }
    }
}