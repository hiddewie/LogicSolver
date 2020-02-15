# LogicSolver

Solver for logic puzzles, easily extensible for other kind of puzzles

[![Build Status](https://hiddewie.semaphoreci.com/badges/LogicSolver.svg)](https://hiddewie.semaphoreci.com/projects/LogicSolver) 
[![Maintainability](https://api.codeclimate.com/v1/badges/78c7a17b06141586c06a/maintainability)](https://codeclimate.com/github/hiddewie/LogicSolver/maintainability) 
[![Test Coverage](https://api.codeclimate.com/v1/badges/78c7a17b06141586c06a/test_coverage)](https://codeclimate.com/github/hiddewie/LogicSolver/test_coverage)

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

### Double sudoku

The double sudoku has two sudokus of which the bottom three blocks coincide with the top three blocks of the other.

```
val input = SudokuDouble.readFromString("""
. 8 1 . 4 . 7 3 .
. 4 9 . . . 8 1 .
. . . . 5 . . . .
6 . 4 . . . 9 . 7
. . . . . . . . .
7 . . 5 9 4 . . 3
. . . 6 . 5 . . .
. . . . 1 . . . .
. . . 4 . 8 . . .
6 . . 3 7 2 . . 5
. . . . . . . . .
8 . 3 . . . 6 . 1
. . . . 6 . . . .
. 4 6 . . . 8 3 .
. 8 1 . 5 . 2 9 .
""")
val output = SudokuSolver().solve(input)
```

The variable `output` will be
```
5 8 1 2 4 9 7 3 6
2 4 9 3 7 6 8 1 5
3 7 6 8 5 1 2 9 4
6 5 4 1 8 3 9 2 7
8 9 3 7 6 2 5 4 1
7 1 2 5 9 4 6 8 3
1 3 8 6 2 5 4 7 9
4 2 5 9 1 7 3 6 8
9 6 7 4 3 8 1 5 2
6 1 4 3 7 2 9 8 5
2 5 9 1 8 6 7 4 3
8 7 3 5 4 9 6 2 1
7 9 2 8 6 3 5 1 4
5 4 6 2 9 1 8 3 7
3 8 1 7 5 4 2 9 6
```

### Sudoku Samurai

The sudoku samurai has five sudokus that are connected by four blocks of nine values.


```
val input = SudokuSamurai.readFromString("""
. 4 9 . . . . 7 3       3 9 . . . . 8 2 .
2 . 8 . . . 1 . 5       2 . 8 . . . 6 . 9
5 6 . . . . 4 8 .       . 6 7 . . . . 1 3
. . . . 9 5 . . .       . . . 3 6 . . . .
. . . 8 . 1 . . .       . . . 5 . 2 . . .
. . . 7 6 . . . .       . . . . 7 9 . . .
. 2 5 . . . . 6 7 . . . 8 4 . . . . 7 3 .
8 . 6 . . . 3 . 4 . 7 . 9 . 6 . . . 2 . 4
7 3 . . . . 5 2 . . . . . 3 1 . . . . 5 6
            . . . 1 . 4 . . .
            . 8 . . . . . 1 .
            . . . 5 . 7 . . .
5 1 . . . . 6 9 . . . . . 2 7 . . . . 9 8
3 . 8 . . . 4 . 1 . 6 . 3 . 5 . . . 4 . 2
. 4 7 . . . . 5 2 . . . 4 6 . . . . 7 1 .
. . . 1 5 . . . .       . . . . 4 1 . . .
. . . 6 . 8 . . .       . . . 3 . 5 . . .
. . . . 9 4 . . .       . . . 6 9 . . . .
1 6 . . . . 2 8 .       . 9 1 . . . . 4 3
2 . 9 . . . 1 . 4       2 . 4 . . . 6 . 1
. 5 4 . . . . 3 7       5 7 . . . . 8 2 .
""")
val output = SudokuSolver().solve(input)
```

The variable `output` will be
```
1 4 9 2 5 8 6 7 3       3 9 5 4 1 6 8 2 7
2 7 8 4 3 6 1 9 5       2 1 8 7 5 3 6 4 9
5 6 3 9 1 7 4 8 2       4 6 7 9 2 8 5 1 3
6 8 7 3 9 5 2 4 1       5 8 9 3 6 1 4 7 2
9 5 4 8 2 1 7 3 6       6 7 3 5 4 2 1 9 8
3 1 2 7 6 4 8 5 9       1 2 4 8 7 9 3 6 5
4 2 5 1 8 3 9 6 7 3 1 5 8 4 2 6 9 5 7 3 1
8 9 6 5 7 2 3 1 4 8 7 2 9 5 6 1 3 7 2 8 4
7 3 1 6 4 9 5 2 8 9 4 6 7 3 1 2 8 4 9 5 6
            2 3 5 1 9 4 6 7 8            .
            7 8 9 6 2 3 5 1 4            .
            1 4 6 5 8 7 2 9 3            .
5 1 2 4 8 7 6 9 3 4 5 8 1 2 7 4 5 6 3 9 8
3 9 8 5 2 6 4 7 1 2 6 9 3 8 5 9 1 7 4 6 2
6 4 7 9 1 3 8 5 2 7 3 1 4 6 9 8 2 3 7 1 5
9 3 6 1 5 2 7 4 8       6 5 8 2 4 1 9 3 7
4 2 5 6 7 8 3 1 9       9 4 2 3 7 5 1 8 6
7 8 1 3 9 4 5 2 6       7 1 3 6 9 8 2 5 4
1 6 3 7 4 9 2 8 5       8 9 1 7 6 2 5 4 3
2 7 9 8 3 5 1 6 4       2 3 4 5 8 9 6 7 1
8 5 4 2 6 1 9 3 7       5 7 6 1 3 4 8 2 9
```

### Sudoku Tiny

The sudoku tiny has a 6x6 grid with 2x3 blocks. The numbers between 1 and 6 must be filled in, but otherwise the same
rules apply as normal sudokus

```
val input = SudokuTiny.readFromString("""
. . . 6 4 2
. . . . . .
. . . . . 6
. . . . 3 .
4 . 5 . . .
2 . 6 5 . .
""")
val output = SudokuSolver().solve(input)
```

The variable `output` will be
```
3 5 1 6 4 2
6 2 4 3 5 1
5 4 3 1 2 6
1 6 2 4 3 5
4 1 5 2 6 3
2 3 6 5 1 4
```
