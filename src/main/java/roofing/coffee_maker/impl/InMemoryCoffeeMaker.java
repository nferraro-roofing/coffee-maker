package roofing.coffee_maker.impl;

import roofing.coffee_maker.BoilerState;
import roofing.coffee_maker.BrewButtonState;
import roofing.coffee_maker.CoffeeMaker;
import roofing.coffee_maker.ReliefValveState;
import roofing.coffee_maker.WarmerPlateState;

public class InMemoryCoffeeMaker implements CoffeeMaker {

    @Override
    public BoilerState getBoilerPlateState() {
        /* TODO */
        return null;
    }

    @Override
    public BrewButtonState getBrewButtonState() {
        /* TODO */
        return null;
    }

    @Override
    public WarmerPlateState getWarmerPlateState() {
        /* TODO */
        return null;
    }

    @Override
    public void setBoilerState(BoilerState newState) {/* TODO */}

    @Override
    public void setBrewButtonState(BrewButtonState newState) {/* TODO */}

    @Override
    public void setReliefValveState(ReliefValveState newState) {/* TODO */}

    @Override
    public void setWarmerState(WarmerPlateState newState) {/* TODO */}

}
