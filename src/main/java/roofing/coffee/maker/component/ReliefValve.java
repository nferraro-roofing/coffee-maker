package roofing.coffee.maker.component;

public class ReliefValve {

  private final final CoffeeMaker cofeeMaker;

  public ReliefValve(CoffeeMaker coffeeMaker) {
    this.cofeeMaker = coffeeMaker;
  }

  // TODO: accepts some state and reacts to it
  public void accept() {

  }

  /**
   * Relief valve states provided in the problem statement.
   *
   * @author nferraro
   *
   */
  public enum State {

    VALVE_OPEN, VALVE_CLOSED;

  }
}