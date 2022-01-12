package roofing.coffee.maker.components;

public interface CoffeeMakerUI extends ClockedComponent {

    boolean isCoffeeReady();

    void pressBrewButton();

    boolean isBrewButtonPressed();

    boolean isPlateHot();

}