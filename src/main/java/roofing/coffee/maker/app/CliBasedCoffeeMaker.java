package roofing.coffee.maker.app;

import java.util.List;

import roofing.coffee.maker.component.BoilerPlate;
import roofing.coffee.maker.component.BrewButton;
import roofing.coffee.maker.component.CoffeeMaker;
import roofing.coffee.maker.component.EventBus;
import roofing.coffee.maker.component.InMemoryCoffeeMaker;
import roofing.coffee.maker.component.ReliefValve;
import roofing.coffee.maker.component.WarmerPlate;

public class CliBasedCoffeeMaker {

    // Singleton, instances of every bus and every component.
    // Initialize everything eagerly as we have no on-demand
    // instances and will constantly use every bus and component.

    private static final EventBus<BoilerPlate.State> BOILER_PLATE_BUS = new EventBus<>();
    private static final EventBus<BrewButton.State> BREW_BUTTON_BUS = new EventBus<>();
    private static final EventBus<WarmerPlate.State> WARMER_PLATE_BUS = new EventBus<>();
    private static final EventBus<ReliefValve.State> RELIEF_VALVE_BUS = new EventBus<>();

    private static final CoffeeMaker COFFEE_MAKER = new InMemoryCoffeeMaker(BOILER_PLATE_BUS,
            BREW_BUTTON_BUS, WARMER_PLATE_BUS, RELIEF_VALVE_BUS);

    private static final BoilerPlate BOILER_PLATE = new BoilerPlate(COFFEE_MAKER);
    private static final BrewButton BREW_BUTTON = new BrewButton(COFFEE_MAKER);
    private static final WarmerPlate WARMER_PLATE = new WarmerPlate(COFFEE_MAKER);
    private static final ReliefValve RELIEF_VALVE = new ReliefValve(COFFEE_MAKER);

    static {
        // We cannot construct our any EventBus instance without a CoffeeMaker
        // instance and an instance of each component. So, we must lazily add
        // subscribers to each bus here instead.

        // TODO
        BOILER_PLATE_BUS.subscribe();
    }

    public static void main(String[] args) {}

}