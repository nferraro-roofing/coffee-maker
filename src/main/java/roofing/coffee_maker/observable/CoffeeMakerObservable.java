package roofing.coffee_maker.observable;

public interface CoffeeMakerObservable {

    public ObservableRegistration register(CoffeeMakerObserver observer);

    public void notifyObservers();

    public interface ObservableRegistration {

        void remove();

    }
}
