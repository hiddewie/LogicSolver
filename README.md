# LogicSolver

Solver for logic puzzles, easily extensible for other kind of puzzles

Build status (using Semaphore CI): [![Build Status](https://semaphoreci.com/api/v1/hiddewie/logicsolver/branches/master/badge.svg)](https://semaphoreci.com/hiddewie/logicsolver)

## Idea

This solver for logic puzzles is intended to follow the same reasoning as humans do, while solving logic puzzles.
Initially, this code is written to solve Sudokus (and variants of that puzzle), however it may be extended easily for
other logic puzzles.

#### Input

A puzzle has a form of input. In case of a sudoku this is the 9x9 grid with some numbers filled in. For other types of
puzzles the input is different.

#### Working input

Using the input, a working input is generated. In this data form the data is expanded into a form which can be used for
solving the puzzle.

#### Solver

A solver solves a puzzle given the puzzle working input and a set of strategies. If no more conclusions can be made the
puzzle has either been solved or cannot be solved using the available strategies.

#### Strategy

A strategy solves a very small part of the puzzle, by making conclusions based on the working input of the puzzle. In case
of a sudoku, for example a row with every number between 1 and 9 filled in may be concluded that the remaining number has
to be filled in in the empty square.

#### Output

The output of the puzzle may be in the same form as the input, or in a different form. The output may contain a solved
puzzle, or a partially solved puzzle with error messages why the puzzle could not be solved.


## Build

Run the command `./gradlew build`. The tests are run automatically.

## Test only

Run the command `./gradlew test`.
