package roofing.coffee.maker.components;

public class WaterReservoir implements ClockedComponent {

    public void fill(int cupsOfwater) {
        
    }

    public int cupsOfWater() {
        return 1;
    }

    // Also brews only while warmerplate.hasPot();
    public boolean isBrewing() {
        return false;
        // On tick, if !.isEmpty but pot isFull, do not brew more coffee
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

}
