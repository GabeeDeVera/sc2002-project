# sc2002-project
A project for Nanyang Technological University's (NTU) SC2002 course on Object-Oriented Design and Programming

Repository URL: https://github.com/GabeeDeVera/sc2002-project
Stage Comparisons

Stage 2 adds `VIPCustomer` and `CriticCustomer`. Customer-specific waiting loss,
menu choice and VIP pricing are dispatched through the `Customer` subclasses.

Stage 3 adds `Host`, `ComboMeal` and the Happy Hour pricing policy. `Host` seats
at most one queued customer per turn. Combo prices use 90% of the two component
prices, rounded to cents. Happy Hour is activated once by the manager, lasts for
the current and next turn, applies to new orders only, and restores 15
satisfaction to customers still waiting for a table or food.

## Build Instructions
Run `python compile.py` from the root directory to compile the project.

Then, run `python run.py` to run the project. Note that python creates a subprocess that runs the `Main.java` file.

If you run into issues with stale `.class` build artifacts, run `python make_clean.py` to remove all `.class` files in the `src\main\java` directory.

## Video Timestamps

Build and run from the project root:

    javac -d out $(find src/main/java -name '*.java')
    java -cp out src.main.java.Main

The base setting uses 18 turns, two tables and six arrivals. Arrivals include
one VIP and one Critic so Stage 2 behaviour is reproducible.
