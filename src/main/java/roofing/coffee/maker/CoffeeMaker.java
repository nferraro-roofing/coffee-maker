package roofing.coffee.maker;

import roofing.coffee.maker.busses.BusMessage;
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

    public int getMaxWaterCapacityCups() {
        return reservoir.maxCapacityCups();
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

    public boolean isBrewing() {
        return reservoir.isBrewing();
    }

    public CoffeePot removePot() {
        if (warmer.hasPot()) {
            warmer.removePot();
            return pot;
        }

        throw new IllegalStateException(
                "The coffee pot has been removed previously without replacement. Please replace "
                        + "the pot via replacePot() before removing again.");
    }

    public void replacePot() {
        if (!warmer.hasPot()) {
            warmer.replacePot();

        } else {
            throw new IllegalStateException(
                    "The coffee pot is currently on the warmer plate. Cannot replace a pot that "
                            + "is already present! Please remove the pot first via removePot().");
        }
    }

    public BusMessage asBusMessage() {
        return BusMessage.builder()
                .withBrewButton(button)
                .withCoffeePot(pot)
                .withWarmerPlate(warmer)
                .withWaterReservoir(reservoir)
                .build();
    }
}