package roofing.coffee.maker.clocks;

import roofing.coffee.maker.components.ClockedComponent;

public interface Clock {

    void synchonize(ClockedComponent... components);

    void tick();

}