package nl.hiddewieringa


//data class Sudoku

//new Sudoku(
//[(1,1) -> 2]
//).solve()
//
//new SudokuX(
//[(1,1) -> 1]
//).solve()
//
//new SudokuSnake(
//[(1,1) -> 1],
//[[(1,1), (2,2), (1,1)], [(3,4), (4,5)]]
//)

// SingleBlockPuzzle(size) {
//
// }
// Sudoku -> SingleBlockPuzzle(9)

//Sudoku(values) {
//    values...
//    groups = [
//        range(1, 9).flatmap(r -> [
//    row(r),
//    column(r),
//    block(r)
//    ]),
//    ]
//}

//SudokuSnake (values, groups) {
//    super()
//    verifyConnected(groups)
//    verifyGroupSizes(groups)
//    verifyAllIndicesTaken(groups)
//}

//Sudoku implements LogicPuzzleInput
//SudokuX implements LogicPuzzleInput
//... implements LogicPuzzleInput

//interface LogicPuzzleInput {
//
//    Result<Sudoku, Error> solve()
//
//}


// Solve

//SudokuSolver solver(SudokuInput input) {
//    new SudokuSolver (input)
//}

class SudokuSolveData(val coordinate: Coordinate, val value: Int?, val notAllowed: List<Int>) {
    fun hasValue(): Boolean {
        return value != null
    }

    fun isEmpty(): Boolean {
        return !hasValue()
    }
}

class SudokuSolver(input: SudokuInput) {

    val groups: Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>>
    val data: MutableMap<Coordinate, SudokuSolveData> = mutableMapOf()

    init {
        groups = input.groups.map { coordinates: List<Coordinate> ->
            { v: Map<Coordinate, SudokuSolveData> ->
                coordinates.map { v.get(it) }.filterNotNull()
            }
        }.toSet()

        (1..9).forEach { i ->
            (1..9).forEach { j ->
                val coordinate = Coordinate(i, j)
                data[coordinate] = if (input.values.containsKey(coordinate)) {
                    SudokuSolveData(coordinate, input.values[coordinate]!!, listOf())
                } else {
                    SudokuSolveData(coordinate, null, listOf())
                }
            }
        }
    }

    fun gatherConclusions(): Set<Conclusion> {
        return groups.flatMap {
            GroupStrategy(it(data)).gatherConclusions()
        }.toSet()
    }

    fun processConclusion(conclusion: Conclusion) {
        if (conclusion.isLeft()) {
            val value = conclusion.left()
            if (data[value.coordinate]!!.value != null) {
                throw Exception("Value at ${data[value.coordinate]!!.coordinate} is ${data[value.coordinate]!!.value} but replacing with ${value.value}")
            }
//            println("Setting value at ${data[value.coordinate]!!.coordinate} to value ${value.value}")
            data[value.coordinate] = SudokuSolveData(value.coordinate, value.value, data[value.coordinate]!!.notAllowed)
        } else {
            val notAllowed = conclusion.right()
//            println("Not allowed ${notAllowed.value} at ${data[notAllowed.coordinate]!!.coordinate}")
            data[notAllowed.coordinate] = SudokuSolveData(notAllowed.coordinate, data[notAllowed.coordinate]!!.value, data[notAllowed.coordinate]!!.notAllowed + listOf(notAllowed.value))
        }
    }

    fun solve(): OneOf<SudokuOutput, List<LogicSolveError>> {
        if (isSolved()) {
            return toOutput()
        }

//        print()

        do {
            val conclusions = gatherConclusions()
            conclusions.forEach(::processConclusion)
//            print()
        } while (!conclusions.isEmpty())

        return if (isSolved()) {
            toOutput()
        } else {
            OneOf.right(listOf(LogicSolveError("No more conclusions, cannot solve")))
        }
    }

    private fun print() {
        (1..9).forEach { i ->
            (1..9).forEach { j ->
                print(data.get(Coordinate(i, j))?.value ?: ' ')
                print(' ')
            }
            print('\n')
        }
        print('\n')
    }

    fun isSolved(): Boolean {
        return !data.values.any {
            it.value == null
        }
    }

    fun toOutput(): OneOf<SudokuOutput, List<LogicSolveError>> {
        val valueMap = data.mapValues {
            it.value.value!!
        }
        return OneOf.left(SudokuOutput(valueMap))
    }
}
