package roofing.coffee.maker.components;

public interface WaterReservoir extends ClockedComponent {

    void fill(int cupsOfwater);

    boolean isEmpty();

}
