package roofing.coffee.maker;

import roofing.coffee.maker.clocks.Clock;

/**
 * A creator of CoffeeMaker instances.
 * 
 * CoffeeMakerCreator purposefully does not provide a default constructor. Doing so forces clients
 * of this package to consider the ramifications of different Clocks. Clock speed and behavior which
 * is non-trivial consideration and can affect the performance and correctness of a CoffeeMaker
 * client.
 * 
 * @author nferraro-roofing
 *
 */
public class CoffeeMakerCreator {

    private final Clock clock;

    /**
     * Create a CoffeeMakerCreator that will synchronize the CoffeeMaker's components with the
     * provided Clock instance.
     * 
     * @param clock
     */
    public CoffeeMakerCreator(Clock clock) {
        this.clock = clock;
    }

    public CoffeeMaker create() {

    }
}