package roofing.coffee.maker.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.ToString;
import roofing.coffee.maker.busses.BusComponent;
import roofing.coffee.maker.busses.BusMessage;

@ToString(includeFieldNames = true)
public class WarmerPlate implements BusComponent<WarmerPlate> {

    private static final Logger LOG = LoggerFactory.getLogger(WarmerPlate.class);

    // stayHotTickLimit is an app setting (see CoffeeMakerProperties and CoffeeMakerCreator).
    // Therefore, it should be final. However, instances of WarmerPlate that are intended for use
    // a BusMessage won't know this value (see busMessageInstance()). As a result, the only way
    // for such instances to know about this value is from other instances post-creation time in
    // refreshFrom() - thus preventing this value from being final.
    private long stayHotTickLimit;

    private int cyclesAfterBrewStopped = 0;
    private boolean hasPot = true;
    private boolean isHot = false;

    /**
     * Create an instance of a WarmerPlate to be used as within a bus message.
     * 
     * @return a WarmerPlate intended for use within a BusMessage.
     */
    public static WarmerPlate busMessageInstance() {
        return new WarmerPlate();
    }

    public WarmerPlate(long stayHotTickLimit) {
        this.stayHotTickLimit = stayHotTickLimit;
    }

    private WarmerPlate() {
        this.stayHotTickLimit = 0;
    }

    @Override
    public void readBusMessage(BusMessage message) {
        boolean reservoirIsBrewing = message.getReservoir().isBrewing();

        // < instead of <= because WarmerPlate naturally has a 1-tick lag time after brewing stops
        isHot = reservoirIsBrewing || cyclesAfterBrewStopped < stayHotTickLimit;

        if (reservoirIsBrewing) {
            cyclesAfterBrewStopped = 0;

        } else if (cyclesAfterBrewStopped < stayHotTickLimit) {
            cyclesAfterBrewStopped++;
        }

        LOG.trace("WarmerPlate after reading a BusMessage: isHot? {}, cyclesAfterBrewStopped? {}",
                isHot,
                cyclesAfterBrewStopped);
    }

    @Override
    public void refreshFrom(WarmerPlate other) {
        this.hasPot = other.hasPot();
        this.isHot = other.isHot();
        this.stayHotTickLimit = other.stayHotTickLimit;
        // No need to refresh cyclesAfterBrewStopped because this information is not important in a
        // bus message. It's an internal-only value.
    }

    @Override
    public void reset() {
        this.hasPot = true;
        this.isHot = false;
    }

    public void removePot() {
        hasPot = false;
    }

    public void replacePot() {
        hasPot = true;
    }

    public boolean isHot() {
        return isHot;
    }

    public boolean hasPot() {
        return hasPot;
    }
}