package roofing.coffee.maker.busses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.ToString;

@ToString
public class Bus {

    // We expect four ClockedComponents: CoffeeMakerUI, CoffeePot, WarmerPlate, and WaterReservoir
    private final List<BusComponent<?>> synchedComponents = new ArrayList<>(4);

    public Bus(BusComponent<?>... components) {
        synchedComponents.addAll(Arrays.asList(components));
    }

    public void update(BusMessage message) {
        for (BusComponent<?> c : synchedComponents) {
            c.readBusMessage(message);
        }
    }
}
