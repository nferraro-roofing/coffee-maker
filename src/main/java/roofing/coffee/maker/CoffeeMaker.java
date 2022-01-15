package roofing.coffee.maker;

import roofing.coffee.maker.busses.Bus;
import roofing.coffee.maker.busses.BusMessage;
import roofing.coffee.maker.components.BrewButton;
import roofing.coffee.maker.components.CoffeePot;
import roofing.coffee.maker.components.WarmerPlate;
import roofing.coffee.maker.components.WaterReservoir;

public class CoffeeMaker {

    private final Bus bus;
    private final WaterReservoir reservoir;
    private final BrewButton button;
    private final CoffeePot pot;
    private final WarmerPlate warmer;

    CoffeeMaker(Bus bus,
            WaterReservoir reservoir,
            BrewButton button,
            CoffeePot pot,
            WarmerPlate warmer) {
        this.bus = bus;
        this.reservoir = reservoir;
        this.button = button;
        this.pot = pot;
        this.warmer = warmer;
    }

    public void fill(int cupsOfwater) {
        reservoir.fill(cupsOfwater);
        enqueueCurrentState(); // Important enqueue AFTER we change state
    }

    public void pressBrewButton() {
        button.pressBrewButton();
        enqueueCurrentState(); // Important enqueue AFTER we change state
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
        enqueueCurrentState(); // Important enqueue AFTER we change state
        return pot;
    }

    public void replacePot() {
        warmer.replacePot();
        enqueueCurrentState(); // Important enqueue AFTER we change state
    }

    private void enqueueCurrentState() {
        bus.enqueue(BusMessage.builder()
                .withBrewButton(button)
                .withCoffeePot(pot)
                .withWarmerPlate(warmer)
                .withWaterReservoir(reservoir)
                .build());
    }
}