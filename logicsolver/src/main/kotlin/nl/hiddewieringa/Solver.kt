package nl.hiddewieringa

/**
 * Solving data structure for sudokus.
 *
 * Contains its location, its value, and any values that are not allowed.
 */
class SudokuSolveData(val coordinate: Coordinate, val value: Int?, val notAllowed: List<Int>) {
    fun hasValue(): Boolean {
        return value != null
    }

    fun isEmpty(): Boolean {
        return !hasValue()
    }
}

/**
 * Solves sudoku input of different forms
 */
class SudokuSolver(input: SudokuInput) {

    val groups: Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>>
    val data: MutableMap<Coordinate, SudokuSolveData> = mutableMapOf()

    init {
        // Migrate groups to Group objects
        groups = input.groups.map { coordinates: List<Coordinate> ->
            { v: Map<Coordinate, SudokuSolveData> ->
                coordinates.map { v.get(it) }.filterNotNull()
            }
        }.toSet()

        // Build solving data from input
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

    /**
     * Gathers conclusions from each group
     */
    fun gatherConclusions(): Set<Conclusion> {
        return groups.flatMap {
            GroupStrategy(it(data)).gatherConclusions()
        }.toSet()
    }

    /**
     * Processes the conclusions from each group
     */
    fun processConclusion(conclusion: Conclusion) {
        if (conclusion.isLeft()) {
            val value = conclusion.left()
            if (data[value.coordinate]!!.value != null) {
                throw Exception("Value at ${data[value.coordinate]!!.coordinate} is ${data[value.coordinate]!!.value} but replacing with ${value.value}")
            }
            data[value.coordinate] = SudokuSolveData(value.coordinate, value.value, data[value.coordinate]!!.notAllowed)
        } else {
            val notAllowed = conclusion.right()
            data[notAllowed.coordinate] = SudokuSolveData(notAllowed.coordinate, data[notAllowed.coordinate]!!.value, data[notAllowed.coordinate]!!.notAllowed + listOf(notAllowed.value))
        }
    }

    /**
     * Solves the input by finding conclusions until no more conclusions can be found.
     * Then, either the puzzle has been solved or an error is returned.
     */
    fun solve(): OneOf<SudokuOutput, List<LogicSolveError>> {
        if (isSolved()) {
            return toOutput()
        }

        do {
            val conclusions = gatherConclusions()
            conclusions.forEach(::processConclusion)
        } while (!conclusions.isEmpty())

        return if (isSolved()) {
            toOutput()
        } else {
            OneOf.right(listOf(LogicSolveError("No more conclusions, cannot solve")))
        }
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
