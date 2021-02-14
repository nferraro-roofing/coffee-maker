package com.roofing.coffee_maker.app;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.roofing.coffee_maker.impl.InMemoryCoffeeMaker;
import com.roofing.coffee_maker.observable.CoffeeMakerObservable;
import com.roofing.coffee_maker.observable.impl.BoilerPlateObserver;
import com.roofing.coffee_maker.observable.impl.WeakReferenceCoffeMakerObservable;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        CoffeeMakerObservable observable =
                new WeakReferenceCoffeMakerObservable(new InMemoryCoffeeMaker());
        observable.addObserver(new BoilerPlateObserver());

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1);
        scheduledService.schedule(observable::notifyObservers, 1, TimeUnit.SECONDS);
    }
}
