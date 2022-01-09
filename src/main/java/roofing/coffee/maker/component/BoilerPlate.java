package roofing.coffee.maker.component;

public class BoilerPlate {

    private final CoffeeMaker cofeeMaker;

    public BoilerPlate(CoffeeMaker coffeeMaker) {
        this.cofeeMaker = coffeeMaker;
    }

    // TODO: accepts some state and reacts to it
    public void accept(BrewButton.State newState) {

    }

    /**
     * Boiler states provided in the problem statement.
     *
     * @author nferraro
     *
     */
    public static enum State {

        EMPTY, NOT_EMPTY;

    }
}