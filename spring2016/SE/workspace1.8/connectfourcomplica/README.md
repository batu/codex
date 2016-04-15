# Connect Four OOP!

A pattern implementation of m,n,k-games, with a Connect Four
foundation.

## Introduction

The objective of this homework series is to build a framework for
developing and deploying a library of
"[m,n,k-games](https://en.wikipedia.org/wiki/M,n,k-game)". These games
not only happen to be fun, but also lend themselves well to object
oriented practices. Each homework will focus on a slightly different
piece of the framework, encouraging the use of design patterns to
build code that is easily extensible.

## Testing

The `test` package comes with an overall system test, and a series of
unit tests. You can use the system test for sanity checking, but your
focus should be the unit tests. There are several standard tests you
must pass, but you should also create your own tests for areas that
might not be covered.

Within the `test` directory, there is a sub-directory for unit tests
(`tests/unit`). This directory contains another directory containing
the standard set of tests (`jsw7`) -- you should create another
directory alongside this containing your tests. Specifically, if your
NetID is `bold24`, you might do the following:
```
$> cd src/test/unit/
$> ls
jsw7
$> mkdir bold24
```
and put your unit tests in `bold24`.

Note, there are no standard tests for the view (`ConnectFourView`) nor
are you required to create any (although, feel free to if you're so
inclined).

## Build/test/run

### Ant

You can use whatever you like for development. If you choose to remain
in the command line, the ant build script recognizes the following
commands:

Command | Description
     ---|            ---
clean   | Removes extraneous directories; most notably, existing class files. Use this command if Java's giving you strange errors.
compile | Compiles all Java files under `src`.
test    | Engages JUnit to run the unit tests.
run     | Runs the full game.

### Eclipse

Eclipse might need a little help to get JavaFX working. There are two
errors you might see:

If Eclipse is giving you lots of red-x's and complaining about "access
restriction"s, you'll need to change the resolution of the JavaFX
libraries. Follow the instructions
[here](http://stackoverflow.com/a/29329228), but providing
```
javafx/**
```
as the rule pattern.

If Eclipse cannot resolve the JavaFX libraries, you will need to add
them manually. First, find the ```jfxrt.jar``` on your system. If you're using a Bash-based system, you can type:
```
find / -name 'jfxrt.jar'
```
to obtain its location (if you know the top level directory where your
JDK is installed, replacing ```/``` with that location will help
```find``` run faster). In Eclipse, right click the project in the
package view and go to "Properties." Under "Java Build Path", select
the "Library" tab. There you can "Add External JARs..." and select
```jfxrt.jar``` based on its location.

## Workflow

It is expected that you will use GitHub religiously. You should fork
this project and [make your fork
private](https://help.github.com/articles/making-a-public-repository-private/). You
can then `branch`/`commit`/`push` freely. You should submit your final
code to NYU Classes. Once all submissions are in, you can make your
repository public.

# Version 0: Connect Four

The primary objective of this homework is to develop the game of
[Connect Four](https://en.wikipedia.org/wiki/Connect_Four). This is a
two-player game in which players take turns putting chips on a
grid. Once a player has four chips in-a-row they win. See the game's
original [television commercial](https://youtu.be/9KsfiqAdSW0) for a
visual summary.

Aside from game development, this homework should get you comfortable
with unit testing -- you'll notice a JUnit file buried in the test
package -- and with the [observer design
pattern](https://en.wikipedia.org/wiki/Observer_pattern).

## Classes

There are four interfaces provided for you: `Game`, `View`,
`Controller`, and `ObservableGame`; each located in `src/api`. It is
your job to implement these classes in `impl`.

Package | Class | Extends | Implements | Summary
     ---|    ---|      ---|         ---|        ---
api  | View | | Observer | Handles visual information: User input from `stdin`, visualization of the board to `stdout`.
     | Game | ObservableGame | | Manages the internal game state.
     | Controller | | | Manages view events.
     | ObservableGame | Observable | | Makes an entire game observable using the Java observer interface, and a game winner observable using the JavaFX bindings.
util | Chip | SimpleObjectProperty<Color> | | Represents chips on a board (replaces `Token`s).
impl | ConnectFourGame | Game | | Your implementation of Game.
     | GameController | Controller | | Your implementation of Controller.
     | ConsoleView | View | | Your implementation of View (replaces `ConnectFourView`).
     | GraphicalView | Application | | JavaFX entry point.

# Version 1: UI with JavaFX

The objective of this assignment is to add a graphical user interface
to your previous Connect Four codebase using JavaFX. In doing so, your
code should follow some semblance of model view controller.

# Assignment

## Updates to `Game`

There have been several changes to `Game` that you must take into
account:

* The surface is a matrix of `Chip`'s rather than `Enum`'s. As such
  several methods have been altered to return a `Chip` rather than a
  `Token`.

* `currentPlayer` and `getWinner` have been changed to
  `getCurrentPlayer` and `getWinningPlayer`, respectively.

* `nextPlayer` has been removed.

* Extends `ObservableGame`, which is a combination of the generalized
  Java Observable, and a particular Chip observable.

These changes will likely require you refactor a bit of your code.

## Rename of `ConnectFourView`

Now called `ConsoleView`.

## Addition of a controller

Your view is free to communicate with the controller as much as it
likes; however, at the very least it must offload disk placement to
the controller. Errors in disk placement should not stop the view from
doing its job.

## Adding a GUI

You must use JavaFX. How you do so is up to you:

### Building the view

JavaFX views can be built either statically, or programatically. In
the former, you create FXML files that specify layout elements, and
the Java classes that handle enhancing those elements and handling
events. There are tools that can assist in writing FXML; Scene Builder
from Oracle is most notable. Creating the views programatically
involves uses the JavaFX libraries to set the scene and display the
stage. Either approach is fine.

### Model/controller communication

There are a number of ways in which your view can communicate with the
model and controller. One method is to continue using the default Java
Observer interface, and to redraw the stage on update. Another method
is to use bindings, a specialized type of variable connection defined
by the JavaFX framework. Bindings are similar to observervables, but
specialized for attributes. The additional `Chip` class is setup to
make using bindings easier. As with view creation, the method of
communication you use is up to you.

### Requirements of the view

The view must accomplish the following:

1. Display the game and allow the user to interact with it using a
   mouse. Specifically, it should display the entire grid. When a user
   clicks a column, that users should chip should appear in the
   appropriate row.

2. Display the current player.

3. Display the winning player.

4. Give some indication that the game is over. A user should be able
   to distinguish between a tie and a win.

5. Once the game is determined to be finished, disallow more chips
   being played.
