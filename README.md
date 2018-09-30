# LogicSolver

Solver for logic puzzles, easily extensible for other kind of puzzles

Build status (using Semaphore CI): [![Build Status](https://semaphoreci.com/api/v1/hiddewie/logicsolver/branches/master/badge.svg)](https://semaphoreci.com/hiddewie/logicsolver)

Maintanability (using CodeClimate): [![Maintainability](https://api.codeclimate.com/v1/badges/78c7a17b06141586c06a/maintainability)](https://codeclimate.com/github/hiddewie/LogicSolver/maintainability)

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

# Examples

Below are a few examples of puzzles which can be solved using the logic solver.

### Sudoku

```
val input = Sudoku.readFromString("""
. . . . 4 5 . . 9
9 5 . . . 7 3 2 .
. 8 2 . 6 . 1 . .
6 7 . 4 . 8 9 . 2
. 1 5 6 . 9 4 . .
. . . . . . . . 8
. 3 . . 7 . . . 1
. . 7 8 . . . 4 .
. 4 6 3 . 2 7 . 5
""")
val output = SudokuSolver().solve(input)
```

The variable `output` will be
```
3 6 1 2 4 5 8 7 9
9 5 4 1 8 7 3 2 6
7 8 2 9 6 3 1 5 4
6 7 3 4 5 8 9 1 2
8 1 5 6 2 9 4 3 7
4 2 9 7 3 1 5 6 8
2 3 8 5 7 4 6 9 1
5 9 7 8 1 6 2 4 3
1 4 6 3 9 2 7 8 5
```

### Sudoku X

The Sudoku X has two diagonals on which all numbers from 1 till 9 are allowed exactly once.

```
val input = SudokuX.readFromString("""
 . 3 . . 8 . . . .
 9 . 6 5 3 7 . . .
 2 . . . 9 . . . 5
 . . 3 . . . 1 . 8
 . . 9 8 . 6 3 . .
 8 . 5 . . . 6 . .
 1 . . . 6 . . . 4
 . . . 1 5 8 7 . 2
 . . . . 2 . . 1 .
""")
val output = SudokuSolver().solve(input)
```

The variable `output` will be
```
5 3 1 2 8 4 9 7 6
9 4 6 5 3 7 2 8 1
2 8 7 6 9 1 4 3 5
6 7 3 9 4 5 1 2 8
4 2 9 8 1 6 3 5 7
8 1 5 3 7 2 6 4 9
1 5 2 7 6 3 8 9 4
3 9 4 1 5 8 7 6 2
7 6 8 4 2 9 5 1 3
```

### Sudoku Hyper

The Sudoku Hyper has four blocks in which all numbers from 1 till 9 are allowed exactly once.

```
val input = SudokuHyper.readFromString("""
7 . 3 . . 8 5 . .
. . . . . 5 1 . 9
5 . . . . . . 7 .
. . 4 . . . . 3 8
. . 6 . 5 . 7 . .
8 3 . . . . 2 . .
. 1 . . . . . . 4
6 . 9 2 . . . . .
. . 5 4 . . 9 . 2
""")
val output = SudokuSolver().solve(input)
```

The variable `output` will be
```
7 9 3 1 4 8 5 2 6
4 6 2 3 7 5 1 8 9
5 8 1 9 6 2 4 7 3
1 5 4 7 2 9 6 3 8
9 2 6 8 5 3 7 4 1
8 3 7 6 1 4 2 9 5
2 1 8 5 9 7 3 6 4
6 4 9 2 3 1 8 5 7
3 7 5 4 8 6 9 1 2
```
