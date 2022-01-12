package roofing.coffee.maker;

public interface CoffeeMaker {

    void fill(int cupsOfwater);

    void pressBrewButton();

    int cupsOfWater();

    int cupsOfCoffee();

    boolean isWarmerPlateOn();

    boolean isBrewComplete();

    void removePot();

    void replacePot();

}