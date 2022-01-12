package roofing.coffee.maker.clocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import roofing.coffee.maker.components.ClockedComponent;

class AutomaticClock implements Clock {

    // We expect four ClockedComponents: CoffeeMakerUI, CoffeePot, WarmerPlate, and WaterReservoir
    private final List<ClockedComponent> synchedComponents = new ArrayList<>(4);

    private final long tickRate;
    private final TimeUnit tickRateUnit;
    private final ScheduledExecutorService svc;

    private boolean synched = false;

    AutomaticClock(long tickRate, TimeUnit tickRateUnit) {
        svc = Executors.newScheduledThreadPool(1);
        this.tickRate = tickRate;
        this.tickRateUnit = tickRateUnit;
    }

    @Override
    public void synchonize(ClockedComponent... newComponents) {
        synchedComponents.addAll(Arrays.asList(newComponents));

        if (!synched) {
            svc.scheduleAtFixedRate(this::tick, 0, tickRate, tickRateUnit);
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
