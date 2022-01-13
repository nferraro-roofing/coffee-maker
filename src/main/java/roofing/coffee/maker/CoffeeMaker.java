package roofing.coffee.maker;

import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

public class CoffeeMaker {

    private final WaterReservoir reservoir;
    private final BrewButton button;
    private final CoffeePot pot;
    private final WarmerPlate warmer;

    CoffeeMaker(WaterReservoir reservoir, BrewButton button, CoffeePot pot, WarmerPlate warmer) {
        this.reservoir = reservoir;
        this.button = button;
        this.pot = pot;
        this.warmer = warmer;
    }

    public void fill(int cupsOfwater) {
        reservoir.fill(cupsOfwater);
    }

    public void pressBrewButton() {
        button.pressBrewButton();
    }

    public int cupsOfWater() {
        return reservoir.cupsOfWater();
    }

    public int cupsOfCoffee() {
        return pot.cupsOfCoffee();
    }

    public boolean isWarmerPlateOn() {
        return warmer.isHot();
    }

    public boolean isBrewComplete() {
        return reservoir.isBrewing();
    }

    public CoffeePot removePot() {
        warmer.removePot();
        return pot;
    }

    public void replacePot() {
        warmer.replacePot();
    }
}