package roofing.coffee.maker;

import java.util.concurrent.TimeUnit;
import roofing.coffee.maker.busses.Clock;
import roofing.coffee.maker.busses.Clock.ClockBuilder;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ClockProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.PotProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.ReservoirProps;
import roofing.coffee.maker.plugins.properties.CoffeeMakerProperties.WarmerPlateProps;

/**
 * TestTimeCoffeeMakerCreator creates CoffeeMaker instances for tests only.
 * 
 * Ideally, tests would acquire a CoffeeMaker via
 * {@code CoffeeMakerCreator::create(ClockBuilder, CoffeeMakerProperties)}. That approach has two
 * drawbacks. First, the test code must provide a ClockBuilder and a CoffeeMakerProperties instance
 * - even if the test in question doesn't care to understand such details; second, that method is
 * package protected because it is also inteded for internal usage or usage by tests. We cannot make
 * the method public, else clients of this Jar may be tempted to use a method that is intended for
 * tests only.
 * 
 * Ultimately, this class acts merely as a workaround to the package-protected scope while
 * alleviating the above two problems. Java 9's module system may yield a cleaner approach.
 * 
 * @author nferraro-roofing
 *
 */
public final class TestTimeCoffeeMakerCreator {

    private TestTimeCoffeeMakerCreator() {/* Disable instantiation */}

    public static CoffeeMaker create() {
        ClockProps clock = new ClockProps(60L, TimeUnit.SECONDS);
        PotProps pot = new PotProps(10);
        ReservoirProps reservoir = new ReservoirProps(1);
        WarmerPlateProps warmerPlate = new WarmerPlateProps(10);

        CoffeeMakerProperties props = new CoffeeMakerProperties(clock, pot, reservoir, warmerPlate);

        ClockBuilder clockBuilder = Clock.builder();
        return CoffeeMakerCreator.create(clockBuilder, props);
    }
}
