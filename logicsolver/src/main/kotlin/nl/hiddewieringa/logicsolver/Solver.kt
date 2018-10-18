package nl.hiddewieringa.logicsolver

interface LogicPuzzleSolver<IN, OUT> {
    fun solve(input: IN): OneOf<OUT, List<LogicSolveError>>
}

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

    fun isNotAllowed(value: Int): Boolean {
        return !isAllowed(value)
    }

    fun isAllowed(value: Int): Boolean {
        return !notAllowed.contains(value)
    }

    override fun toString(): String {
        return "SSD($coordinate, $value, $notAllowed)"
    }
}

/**
 * Solves sudoku input of different forms
 */
class SudokuSolver : LogicPuzzleSolver<SudokuInput, SudokuOutput> {

    private val groupStrategy = GroupStrategy()
    private val overlappingGroupsStrategy = OverlappingGroupsStrategy()

    /**
     * Migrates input groups to Group objects
     */
    private fun toGroups(groups: List<Set<Coordinate>>): Set<SudokuGroup> {
        return groups.map { coordinates: Set<Coordinate> ->
            { v: Map<Coordinate, SudokuSolveData> ->
                coordinates.map { v[it] }.filterNotNull().toSet()
            }
        }.toSet()
    }

    /**
     * Builds solving data from input
     */
    private fun buildSolvingData(coordinates: List<Coordinate>, input: Map<Coordinate, Int>): MutableMap<Coordinate, SudokuSolveData> {
        return coordinates.map { coordinate ->
            coordinate to SudokuSolveData(coordinate, input[coordinate], listOf())
        }.toMap(mutableMapOf())
    }

    /**
     * Gathers conclusions from each group
     */
    private fun gatherConclusions(groups: Set<SudokuGroup>, data: MutableMap<Coordinate, SudokuSolveData>, puzzleValues: Set<Int>): Set<Conclusion> {

        return groups.flatMap { strategy ->
            groupStrategy(Pair(strategy(data), puzzleValues))
        }.toSet() + overlappingGroupsStrategy(Triple(groups, data, puzzleValues))
    }

    /**
     * Processes the conclusions from each group
     */
    private fun processConclusion(data: MutableMap<Coordinate, SudokuSolveData>, conclusion: Conclusion) {
        conclusion.match({ value ->
            if (data[value.coordinate]!!.value != null) {
                throw Exception("Value at ${data[value.coordinate]!!.coordinate} is ${data[value.coordinate]!!.value} but replacing with ${value.value}")
            }
            data[value.coordinate] = SudokuSolveData(value.coordinate, value.value, data[value.coordinate]!!.notAllowed)
        }, { notAllowed ->
            data[notAllowed.coordinate] = SudokuSolveData(notAllowed.coordinate, data[notAllowed.coordinate]!!.value, data[notAllowed.coordinate]!!.notAllowed + listOf(notAllowed.value))
        })
    }

    /**
     * Solves the input by finding conclusions until no more conclusions can be found.
     * Then, either the puzzle has been solved or an error is returned.
     */
    override fun solve(input: SudokuInput): OneOf<SudokuOutput, List<LogicSolveError>> {
        val groups = toGroups(input.groups)
        val data = buildSolvingData(input.coordinates, input.values)

        if (isSolved(data)) {
            return toOutput(input.coordinates, data)
        }

        do {
            val conclusions = gatherConclusions(groups, data, input.puzzleValues)
            conclusions.forEach {
                processConclusion(data, it)
            }

            if (!isValid(groups, data, input.puzzleValues)) {
                throw Exception("The puzzle is not valid: current state: ${toOutput(input.coordinates, data)}")
            }
        } while (!conclusions.isEmpty())


        return if (isSolved(data)) {
            toOutput(input.coordinates, data)
        } else {
            OneOf.right(listOf(LogicSolveError("No more conclusions, cannot solve")))
        }
    }

    /**
     * Checks if the puzzle has been solved
     */
    private fun isSolved(data: MutableMap<Coordinate, SudokuSolveData>): Boolean {
        return !data.values.any {
            it.value == null
        }
    }

    /**
     * Checks if the puzzle is valid
     */
    private fun isValid(groups: Set<SudokuGroup>, data: MutableMap<Coordinate, SudokuSolveData>, puzzleValues: Set<Int>): Boolean {
        return groups.all { group ->
            val groupData = group(data)
            puzzleValues.all { i ->
                groupData.asSequence().filter { it.hasValue() }.count { it.value == i } <= 1
            }
        }
    }

    /**
     * Generates the output given solving data
     */
    private fun toOutput(coordinates: List<Coordinate>, data: MutableMap<Coordinate, SudokuSolveData>): OneOf<SudokuOutput, List<LogicSolveError>> {
        val valueMap = data
                .filterValues { it.value != null }
                .mapValues {
                    it.value.value!!
                }
        return OneOf.left(SudokuOutput(coordinates, valueMap))
    }
}
