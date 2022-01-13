package roofing.coffee.maker.clocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import roofing.coffee.maker.components.ClockedComponent;

public class AutomaticClock implements Clock {

    // We expect four ClockedComponents: CoffeeMakerUI, CoffeePot, WarmerPlate, and WaterReservoir
    private final List<ClockedComponent> synchedComponents = new ArrayList<>(4);

    private final ScheduledExecutorService svc;

    private boolean synched = false;

    public AutomaticClock() {
        svc = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void synchonize(ClockedComponent... newComponents) {
        synchedComponents.addAll(Arrays.asList(newComponents));

        if (!synched) {
            svc.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
            synched = true;
        }
    }

    @Override
    public void tick() {
        for (ClockedComponent c : synchedComponents) {
            c.update();
        }
    }

}
