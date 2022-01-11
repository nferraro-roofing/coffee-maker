package roofing.coffee.maker;

public class CoffeeMakerCreator {

    public CoffeeMaker create() {

        Bus<Boiler.State> boilerPlateBus = new Bus<>();
        Bus<WaterLevel.State> brewButtonBus = new Bus<>();
        Bus<WarmerPlate.State> warmerPlateBus = new Bus<>();
        Bus<PressureReliefValve.State> refliefValveBus = new Bus<>();

        CoffeeMaker coffeeMaker = new InMemoryCoffeeMaker(
                boilerPlateBus,
                brewButtonBus,
                warmerPlateBus,
                refliefValveBus);

        Boiler boilerPlate = new Boiler(coffeeMaker);
        boilerPlateBus.subscribe(boilerPlate::accept);

        WaterLevel brewButton = new WaterLevel(coffeeMaker);
        brewButtonBus.subscribe(brewButton::accept);

        WarmerPlate warmerPlate = new WarmerPlate(coffeeMaker);
        warmerPlateBus.subscribe(warmerPlate::accept);

        PressureReliefValve refliefValve = new PressureReliefValve(coffeeMaker);
        refliefValveBus.subscribe(refliefValve::accept);

        return coffeeMaker;
    }
}
