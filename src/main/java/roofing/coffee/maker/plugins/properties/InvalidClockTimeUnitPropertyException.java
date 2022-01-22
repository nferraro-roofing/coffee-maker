package roofing.coffee.maker.plugins.properties;

@SuppressWarnings("serial")
public class InvalidClockTimeUnitPropertyException extends RuntimeException {

    private static final String MESSAGE =
            "A coffee maker's clock time unit must be no coarser than TimeUnit.MINUTES. "
                    + "The provided TimeUnit was %1s. Please correct this property value and "
                    + "re-start the application.";


    public InvalidClockTimeUnitPropertyException(String providedTimeUnit) {
        super(String.format(MESSAGE, providedTimeUnit));
    }
}