package roofing.coffee.maker;

import org.junit.jupiter.api.BeforeEach;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;

/**
 * TODO: complete docs
 * 
 * difference between this and real usage: normally, we don't allow clients of coffee-maker to use a
 * Bus directly; instead, we use the Clock to automatically update the Bus in a different thread.
 * However, we must control the bus here so that we can traverse known execution paths.
 * 
 * @author nferraro-roofing
 *
 */
class RefillTests {

    private Clock clock;
    private CoffeeMaker subject;

    @BeforeEach
    void initSubjectAndClock() {
        ClockBuilder clockBuilder = Clock.builder();
        subject = CoffeeMakerCreator.create(clockBuilder);
        clock = clockBuilder.build();
    }

    // TODO: pour out coffee / refill reservoir
}