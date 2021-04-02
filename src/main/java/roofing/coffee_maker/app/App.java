package roofing.coffee_maker.app;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import roofing.coffee_maker.impl.InMemoryCoffeeMaker;
import roofing.coffee_maker.observable.CoffeeMakerObservable;
import roofing.coffee_maker.observable.CoffeeMakerObservable.ObservableRegistration;
import roofing.coffee_maker.observable.impl.BoilerPlateObserver;
import roofing.coffee_maker.observable.impl.WeakReferenceCoffeMakerObservable;

/**
 * Hello world!
 *
 */
public class App {

    private static final Set<ObservableRegistration> REGISTRATIONS = new HashSet<>();

    public static void main(String[] args) {
        CoffeeMakerObservable observable =
                new WeakReferenceCoffeMakerObservable(new InMemoryCoffeeMaker());
        
        REGISTRATIONS.add(observable.register(new BoilerPlateObserver()));

        new ScheduledThreadPoolExecutor(1).schedule(
                () -> {
                        System.out.println("Notifying registrations: " + REGISTRATIONS);
                        observable.notifyObservers();
                }
                , 1
                , TimeUnit.SECONDS);
    }
}
