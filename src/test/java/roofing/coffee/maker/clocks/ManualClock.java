package roofing.coffee.maker.clocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import roofing.coffee.maker.components.ClockedComponent;

class ManualClock implements Clock {

    // We expect four ClockedComponents: CoffeeMakerUI, CoffeePot, WarmerPlate, and WaterReservoir
    private final List<ClockedComponent> synchedComponents = new ArrayList<>(4);

    @Override
    public void synchonize(ClockedComponent... newComponents) {
        synchedComponents.addAll(Arrays.asList(newComponents));
    }

    @Override
    public void tick() {
        for (ClockedComponent c : synchedComponents) {
            c.update();
        }
    }

}
