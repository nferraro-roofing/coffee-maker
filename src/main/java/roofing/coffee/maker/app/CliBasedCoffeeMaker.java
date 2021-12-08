package roofing.coffee.maker.app;

import java.util.List;

import roofing.coffee.maker.component.BoilerPlate;
import roofing.coffee.maker.component.BrewButton;
import roofing.coffee.maker.component.CoffeeMaker;
import roofing.coffee.maker.component.EventBus;

public class CliBasedCoffeeMaker {

  public static void main(String[] args) {
    
    CoffeeMaker coffeeMaker = null; // TODO

    BoilerPlate boilerPlate = new BoilerPlate(coffeeMaker);

    EventBus<BrewButton.State> boilerStateBus = 
            new EventBus<BrewButton.State>(List.of(boilerPlate::accept));

  }

}