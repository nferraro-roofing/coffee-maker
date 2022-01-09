package roofing.coffee.maker.component;

public class BrewButton {

    private final CoffeeMaker cofeeMaker;

    public BrewButton(CoffeeMaker coffeeMaker) {
        this.cofeeMaker = coffeeMaker;
    }

    // TODO: accepts some state and reacts to it
    public void accept() {

    }

    /**
     * Brew button (indicator) states provided in the problem statement.
     *
     * @author nferraro
     *
     */
    public enum State {

        NOT_PUSHED, PUSHED;

    }

}
