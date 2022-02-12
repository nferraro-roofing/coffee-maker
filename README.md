Simple coffee maker implementation

# TODO - coffee maker proper
* App settings
** warmer plate stay hot duration
** Update tests
** fix red
*** add tests for CoffeeMakerProperties where appropriate
*** careful of warmer plate duration, water reservoir brew rate, and coffee pot brew rate. E.g. make sure ticksSinceLastCupBrewed resets properly and such
* Logging
* Javadoc / Readme
** Not too happy with COFFEE_POT_MAX_CAPACITY_OFFSET being package protected
** Not too happy with the app-setting members being non-final (e.g. WaterReservoir::maxCapacityCups)
** The true issue with  this whole thing is that the problem is very contrived. It doesn't encapsulate any useful logic - it's all for its own sake. So I changed the problem statement a bit
* Maybe note the 1-cycle lag time issue with all components and remove that stuff from the coffee pot section
** The low-memory, anit-GC approach with the BusMessage, builder, and BusComponent
** Document tests too
** describe the framework agnostic natue of the settings
** describe whatthe settings do and how to use them & restrictions and such
*** Note how coffee pot brew rate is an approximations and depends on the clock tick rate etc
* Format javadoc and view how it actually turns out
** especially interested in CoffeeMakerSettings
* maven enforcer rules?
* Lombok?

# Improvements I want to make:
* Use of modules via java9 - https://www.oracle.com/corporate/features/understanding-java-9-modules.html
** This would allow me to export only CoffeeMaker rather than everything, while also allowing me to organize my packages in a readable, discoverable manner
** I would export CoffeeMaker, CoffeeMakerCreator, and BrewButton only
** I would also make CoffeeMaker::currentState visible only within the roofing.coffee.maker.* package if that's possible
** I could solve all of this by plopping everything into once package. This isn't a bad idea, but I like very small packages
* BrewButton internal state could probably use booleans instead of an enum
* Thread-safety in BusMessageBuilder
* CoffeePot max capacity should be an app setting
* CoffeeMaker has a one-way state diagram; there's no way to reset it - i.e clean it and re-use it tomorrow
** It would be cool to make the coffee maker sensitive to the clock and reset itself after 30 minutes or something - like a real coffe maker
** This would change the nature of isBrewing quite a bit!
* More granular brew rate than whole cups
* never added the concept of a coffee ground holder

# Key difficulties I had
* Making the thing unit-testable given the asynch ticking nature of the clock
* Decoupling the concepts of snapshotting state of the components after user input vs just always using the current state
** Even if I do not actually snapshot the current state in my final solution, still write about this challenge and why I came to this conclusion

### The Mark IV Special Coffee Maker [Problem statement - taken from Uncle Bob's [article] (http://objectmentor.com/resources/articles/CoffeeMaker.pdf)]


The Mark IV Special makes up to 12 cups of coffee at a time. The user places a filter in the filter holder, fills the filter with coffee grounds, and slides the filter holder into its receptacle. The user then pours up to 12 cups of water into the water strainer and presses the Brew button. The water is heated until boiling. The pressure of the evolving steam forces the water to be sprayed over the coffee grounds, and coffee drips through the filter into the pot. The pot is kept warm for extended periods by a warmer plate, which turns on only if coffee is in the pot. If the pot is removed from the warmer plate while water is being sprayed over the grounds, the flow of water is stopped so that brewed coffee does not spill on the warmer plate. The following hardware needs to be monitored or controlled:

* The heating element for the boiler. It can be turned on or off.
* The heating element for the warmer plate. It can be turned on or off.
* The sensor for the warmer plate. It has three states: warmerEmpty, potEmpty, potNotEmpty.
* A sensor for the boiler, which determines whether water is present. It has two states: boilerEmpty or boilerNotEmpty.
* The Brew button. This momentary button starts the brewing cycle. It has an indicator that lights up when the brewing cycle is over and the coffee is ready.
* A pressure-relief valve that opens to reduce the pressure in the boiler. The drop in pressure stops the flow of water to the filter. The value can be opened or closed.


https://flylib.com/books/en/4.444.1.119/1/
The hardware for the Mark IV has been designed and is currently under development. The hardware engineers have even provided a low-level API for us to use, so we don't have to write any bit- twiddling I/O driver code 
* CoffeeMakerAPI
* WarmerPlateStatus
* BoilerStatus
* BrewButtonStatus
* BoilerState
* WarmerState
* IndicatorState
* ReliefValveState
  
### Logic in a nutshell
Additional logic I can implement:
    Validations on external components (12 cups of water)
    Boiler sensor could be re-tooled as a reservoir, and drain at a given rate when the coffee is brewing
Internal components ------------------------------------------------

    Boiler (heats the WATER) - on or off
        When brew button is pressed & water level is NotEmpty, turn on
        else off
        
    Warmer plate (heats the COFFEE in the pot) - on or off
        When potNotEmpty, then ON
        else OFF

    Pressure relief valve - open or closed
        When warmerEmpty & boiler on then closed
        Else open

External components (user / time sets these) -----------------------

    Water level* - empty or notempty
        Determined based only on user input
        * renamed from boiler sensor in the problem statement
    
    Coffee pot state* - warmerEmpty, potEmpty, potNotEmpty
        Determined based only on user input
        * renamed from Warmer plate sensor
    
    Brew button - not pressed, pressed, coffee ready
        When user presses this, status is pressed
        When 
            