package roofing.coffee.maker.component;

public class WarmerPlate {

    private final CoffeeMaker cofeeMaker;

    public WarmerPlate(CoffeeMaker coffeeMaker) {
        this.cofeeMaker = coffeeMaker;
    }

    // TODO: accepts some state and reacts to it
    public void accept() {

    }

    /**
     * Warmer plate states provided in the problem statement.
     *
     * @author nferraro
     *
     */
    public enum State {

        WARMER_EMPTY, POT_EMPTY, POT_NOT_EMPTY;

    }
}
