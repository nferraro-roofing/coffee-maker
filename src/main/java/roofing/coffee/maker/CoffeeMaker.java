package roofing.coffee.maker;

public class CoffeeMaker {

    private WaterLevel currWaterLevel;
    private CoffeePotState currPotState;
    private BrewButtonState currButtonState;

    private final Bus bus;

    public CoffeeMaker(Bus bus) {
        this.bus = bus;
    }

    public void fillWaterReservoir() {

    }

    public void emptyWaterReservoir() {

    }

    public void removePot() {

    }

    public void replacePot() {

    }

    public void pressBrewButton() {

    }

    public void isCoffeeReady() {

    }

    public void isWarmerPlateOn() {

    }

    WaterLevel getCurrWaterLevel() {
        return currWaterLevel;
    }

    CoffeePotState getCurrPotState() {
        return currPotState;
    }

    BrewButtonState getCurrButtonState() {
        return currButtonState;
    }
}