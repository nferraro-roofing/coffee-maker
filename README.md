Simple coffee maker implementation

# TODO - CI/CD
Goal:
    - Configure github to build & deploy my artifacts to a github package
    - Do so when I merge into main, or maybe at other points too
    - Deploy the jar with sources
    - Deploy with site.xml
    - Create a github site that points to the artifact
        - Latest release only
    - Report on unit test runs
- TODO:
    - Create a github site
    - Point my site to the "latest" release only
    - Display build status in readme & point readme to my site
    - Report on unit test runs in the site (or elsewhere?)
    - Point URL in the pom to the github website
- Resources:
    - See: https://github.com/awhitford/lombok.maven
    - https://pages.github.com/
    - https://maven.apache.org/guides/mini/guide-site.html
    - Publish javadoc as part of site generation: https://maven.apache.org/plugins/maven-javadoc-plugin/usage.html
    
# TODO - coffee maker proper
* Improve test coverage & bump coverage requirement to 95%, Compare eclipse to jacoco
* Complete javadocs
* Complete site.xml
* Complete readme
    * The low-memory, anit-GC approach with the BusMessage, builder, and BusComponent
    * Describe the framework agnostic nature of the settings
    * Describe what the settings do and how to use them & restrictions and such
        ** Note how coffee pot brew rate is an approximation and depends on the clock tick rate etc
    * Note improvements I want to make
    * Logging as a plugin. Test-time logging w/ defaults from logback-classic
    * Lombok plugin for IDE necessary to edit the code
    * Test coverage requirements - why not 100%?
    * Improvements I want to make:
        * Use of modules via java9 - https://www.oracle.com/corporate/features/understanding-java-9-modules.html
            * This would allow me to export only CoffeeMaker rather than everything, while also allowing me to organize my packages in a readable, discover-able manner
            * I would export CoffeeMaker, CoffeeMakerCreator, and BrewButton only
            * I would also make CoffeeMaker::currentState visible only within the roofing.coffee.maker.* package if that's possible
            * I could solve all of this by plopping everything into once package. This isn't a bad idea, but I like very small packages
            * BrewButton internal state could probably use booleans instead of an enum
            * Thread-safety in BusMessageBuilder & it maybe is over-engineered
            * CoffeeMaker has a one-way state diagram; there's no way to reset it - i.e clean it and re-use it tomorrow
            * It would be cool to make the coffee maker sensitive to the clock and reset itself after 30 minutes or something - like a real coffe maker
            * More granular brew rate than whole cups
            * never added the concept of a coffee ground holder
        * CoffeePot, WarmerPlate, WaterReservoir's settings are not final due to the bus message thing
        * Add maven-checkstyle-plugin, bug configure it to match my preferences. The idea is that others should be able to contribute without violating style checks
* Tag as version 1 & release officially via github
    * Cleanup old packages, commits and such too
    * Cleanup the downloadURL in the pom to point where I need it to point - likely using the version and such
    * Go over the other github features in my repo and see what I can add some polish to

# The Mark IV Special Coffee Maker [Problem statement - taken from Uncle Bob's [article] (http://objectmentor.com/resources/articles/CoffeeMaker.pdf)]

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