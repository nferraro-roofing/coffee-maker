package roofing.coffee.maker.component;

/**
 * Hardware API defined in the problem statement.
 *
 * @author nferraro
 *
 */
public interface CoffeeMaker {

    BoilerPlate.State getBoilerPlateState();

    BrewButton.State getBrewButtonState();

    WarmerPlate.State getWarmerPlateState();

    ReliefValve.State getReliefValveState();

    void setBoilerState(BoilerPlate.State newState);

    void setBrewButtonState(BrewButton.State newState);

    void setReliefValveState(ReliefValve.State newState);

    void setWarmerState(WarmerPlate.State newState);
}
