# sc2002-project
A project for Nanyang Technological University's (NTU) SC2002 course on Object-Oriented Design and Programming

Repository URL: https://github.com/GabeeDeVera/sc2002-project
Stage 2 adds `VIPCustomer` and `CriticCustomer`. VIP customers lose 5
satisfaction per waiting turn and receive 10% off their bill. Critic customers
lose 12 satisfaction per waiting turn and prefer the menu item with the most
preparation units. Customer behavior is dispatched through the customer
subclasses.

## Build Instructions
Run `python compile.py` from the root directory to compile the project.

Then, run `python run.py` to run the project. Note that python creates a subprocess that runs the `Main.java` file.

If you run into issues with stale `.class` build artifacts, run `python make_clean.py` to remove all `.class` files in the `src\main\java` directory.

## Video Timestamps

TODO
