package com.roofing.coffee_maker.observable.impl;

import com.roofing.coffee_maker.CoffeeMaker;
import com.roofing.coffee_maker.observable.CoffeeMakerObservable;
import com.roofing.coffee_maker.observable.CoffeeMakerObserver;

public class WeakReferenceCoffeMakerObservable implements CoffeeMakerObservable {

    private final CoffeeMaker observed;

    public WeakReferenceCoffeMakerObservable(CoffeeMaker observed) {
        this.observed = observed;
    }

    @Override
    public void addObserver(CoffeeMakerObserver observer) {/* TODO */}

    @Override
    public <T> void removeObserver(CoffeeMakerObserver observer) {/* TODO */}

    @Override
    public void notifyObservers() {/* TODO */}

}
