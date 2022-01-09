package roofing.coffee.maker.component;

public class InMemoryCoffeeMaker implements CoffeeMaker {

    private final EventBus<BoilerPlate.State> boilerPlateBus;
    private final EventBus<BrewButton.State> brewButtonBus;
    private final EventBus<WarmerPlate.State> warmerPlateBus;
    private final EventBus<ReliefValve.State> reliefValvleBus;

    private BoilerPlate.State currentBoilerState;
    private BrewButton.State currentButtonState;
    private WarmerPlate.State currentWarmerState;
    private ReliefValve.State currentValveState;

    public InMemoryCoffeeMaker(EventBus<BoilerPlate.State> boilerPlateBus,
            EventBus<BrewButton.State> brewButtonState,
            EventBus<WarmerPlate.State> warmerPlateBus,
            EventBus<ReliefValve.State> reliefValvleBus) {
        this.boilerPlateBus = boilerPlateBus;
        this.brewButtonBus = brewButtonState;
        this.warmerPlateBus = warmerPlateBus;
        this.reliefValvleBus = reliefValvleBus;
    }

    @Override
    public BoilerPlate.State getBoilerPlateState() {
        return currentBoilerState;
    }

    @Override
    public BrewButton.State getBrewButtonState() {
        return currentButtonState;
    }

    @Override
    public WarmerPlate.State getWarmerPlateState() {
        return currentWarmerState;
    }

    @Override
    public ReliefValve.State getReliefValveState() {
        return currentValveState;
    }

    @Override
    public void setBoilerState(BoilerPlate.State newState) {
        currentBoilerState = newState;
        boilerPlateBus.publish(newState);
    }

    @Override
    public void setBrewButtonState(BrewButton.State newState) {
        currentButtonState = newState;
        brewButtonBus.publish(newState);
    }

    @Override
    public void setReliefValveState(ReliefValve.State newState) {
        currentValveState = newState;
        reliefValvleBus.publish(newState);
    }

    @Override
    public void setWarmerState(WarmerPlate.State newState) {
        currentWarmerState = newState;
        warmerPlateBus.publish(newState);
    }
}