package roofing.coffee.maker.components;

public class CoffeePot implements ClockedComponent {

    // Cannot do this if the warmer plate says i'm still in the coffee pot!
    public void pourOutCoffee(int cups) {

    }

    public int cupsOfCoffee() {
        return 1;
    }

    public boolean isFull() {
        return false;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

}