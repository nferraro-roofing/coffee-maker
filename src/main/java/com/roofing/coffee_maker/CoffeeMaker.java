package com.roofing.coffee_maker;

/**
 * Hardware API defined in the problem statement.
 *
 * @author nferraro
 *
 */
public interface CoffeeMaker {

    BoilerState getBoilerPlateState();

    BrewButtonState getBrewButtonState();

    WarmerPlateState getWarmerPlateState();

    void setBoilerState(BoilerState newState);

    void setBrewButtonState(BrewButtonState newState);

    void setReliefValveState(ReliefValveState newState);

    void setWarmerState(WarmerPlateState newState);
}
