package roofing.coffee.maker.busses;

public interface BusComponent<TypeRefreshibleFrom> {

    void readBusMessage(BusMessage message);

    void refreshFrom(TypeRefreshibleFrom from);

    void reset();
}