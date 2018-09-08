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

class SudokuSolveData (val coordinate: Coordinate, val value :Int?, val notAllowed: List<Int>)

//fun value(value: Int):SudokuSolveData {
//    return SudokuSolveData(value, (1..9).filterNot { it == value })
//}

class SudokuSolver(input: SudokuInput) {

    val groups: List<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>> // List<(a:Map<Coordinate, SudokuSolveData> -> List<SudokuSolveData>)>
    val data: MutableMap<Coordinate, SudokuSolveData> = mutableMapOf()

    init  {
        // SudokuInput is groups and values

//        input.groups.each {
//
//        }

//        groups = listOf()


        groups = input.groups.map { coordinates: List<Coordinate> ->
            { v: Map<Coordinate, SudokuSolveData>->
                coordinates.map { v.get(it) }.filterNotNull()
            }
        }

        (1..9).forEach {i ->
            (1..9).forEach { j->
                val coordinate = Coordinate(i, j)
                data[coordinate] = if (input.values.containsKey(coordinate)) {
                    SudokuSolveData(coordinate, input.values[coordinate]!!, listOf())
                } else {
                    SudokuSolveData(coordinate, null, listOf())
                }
            }
        }
//        values = values(k: v) -> k: value(v)
//        notAllowed = values(k: )
//        rules = range(1, 9) -> {}
    }

//    fun processConclusion()  {
////        groups.each {
////            processConclusion(it)
////        }
//    }

    fun gatherConclusions(): List<Conclusion>
    {
        return groups.flatMap {
            return GroupStrategy(it(data)).gatherConclusions()
//            it().gatherConclusion(it)
        }
    }

    fun processConclusion(conclusion: Conclusion) {
        if (conclusion.conclusion.isLeft()) {
            val value = conclusion.conclusion.left()
            data[value.coordinate] = SudokuSolveData(value.coordinate, value.value, data[value.coordinate]!!.notAllowed)
        } else {
            val notAllowed = conclusion.conclusion.right()
            data[notAllowed.coordinate] = SudokuSolveData(notAllowed.coordinate, data[notAllowed.coordinate]!!.value, data[notAllowed.coordinate]!!.notAllowed + listOf(notAllowed.value))
        }
    }

    fun  solve(): OneOf<SudokuOutput, List<LogicSolveError>> {
        do {
            val conclusions = gatherConclusions()
            if (conclusions.isEmpty()) {
            } else {
                conclusions.forEach(::processConclusion)
//            processConclusions()
//            conclusions.forEach {
//                processConclusion(it)
//            }
            }
        } while(!conclusions.isEmpty())

        return if (isSolved()) {
            val valueMap = data.mapValues {
                it.value.value!!
            }
            OneOf.left(SudokuOutput(valueMap))
        } else {
            return OneOf.right(listOf(LogicSolveError("No more conclusions, cannot solve")))

        }
    }

    fun isSolved():Boolean {
        return !data.values.any {
            it.value == null
        }
    }
}
