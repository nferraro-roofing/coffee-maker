package roofing.coffee.maker.busses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Bus {

    // We expect four ClockedComponents: CoffeeMakerUI, CoffeePot, WarmerPlate, and WaterReservoir
    private final List<BusComponent<?>> synchedComponents = new ArrayList<>(4);

    private Queue<BusMessage> messages = new LinkedList<>();

    public void synchBusComponents(BusComponent<?>... components) {
        synchedComponents.addAll(Arrays.asList(components));
    }

    public void enqueue(BusMessage msg) {
        messages.add(msg);
    }

    public void update() {
        for (BusMessage message : messages) {
            for (BusComponent<?> c : synchedComponents) {
                c.readBusMessage(message);
            }

            messages.remove(message);
        }
    }
}
