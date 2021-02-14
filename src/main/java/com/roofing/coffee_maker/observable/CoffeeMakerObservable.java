package com.roofing.coffee_maker.observable;

public interface CoffeeMakerObservable {

    public void addObserver(CoffeeMakerObserver observer);

    public <T> void removeObserver(CoffeeMakerObserver observer);

    public void notifyObservers();

}
