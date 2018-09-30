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

    override fun toString(): String {
        return "SSD($coordinate, $value, $notAllowed)"
    }
}

/**
 * Solves sudoku input of different forms
 */
class SudokuSolver : LogicPuzzleSolver<SudokuInput, SudokuOutput> {

    private val groupStrategy = GroupStrategy()

    /**
     * Migrates input groups to Group objects
     */
    private fun toGroups(groups: List<List<Coordinate>>): Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>> {
        return groups.map { coordinates: List<Coordinate> ->
            { v: Map<Coordinate, SudokuSolveData> ->
                coordinates.map { v[it] }.filterNotNull()
            }
        }.toSet()
    }

    /**
     * Builds solving data from input
     */
    private fun buildSolvingData(input: Map<Coordinate, Int>): MutableMap<Coordinate, SudokuSolveData> {
        val data: MutableMap<Coordinate, SudokuSolveData> = mutableMapOf()

        (1..9).forEach { i ->
            (1..9).forEach { j ->
                val coordinate = Coordinate(i, j)
                data[coordinate] = if (input.containsKey(coordinate)) {
                    SudokuSolveData(coordinate, input[coordinate]!!, listOf())
                } else {
                    SudokuSolveData(coordinate, null, listOf())
                }
            }
        }

        return data
    }

    /**
     * Gathers conclusions from each group
     */
    private fun gatherConclusions(groups: Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>>,
                                  data: MutableMap<Coordinate, SudokuSolveData>): Set<Conclusion> {

        return groups.flatMap { strategy ->
            groupStrategy(strategy(data))
        }.toSet() + gatherOverlappingConclusions(groups, data)
    }

    // TODO: extract into separate strategy
    private fun gatherOverlappingConclusions(groups: Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>>,
                                             data: MutableMap<Coordinate, SudokuSolveData>): Set<Conclusion> {
        val evaluatedGroups = groups.map { it(data) }
        return evaluatedGroups.flatMap { group1 ->
            (evaluatedGroups - setOf(group1)).flatMap { group2 ->
                overlappingConclusionsForGroups(group1, group2, data)
            }
        }.toSet()
    }

    fun overlappingConclusionsForGroups(group1: List<SudokuSolveData>, group2: List<SudokuSolveData>,
                                        data: MutableMap<Coordinate, SudokuSolveData>): Set<Conclusion> {
        val overlappingCoordinates = group1.map { it.coordinate }.intersect(group2.map { it.coordinate })
        if (overlappingCoordinates.size < 2) {
            return setOf()
        }
        if (overlappingCoordinates.all { data[it]!!.hasValue() }) {
            return setOf()
        }

        return (1..9).flatMap { i ->
            val group1Other = ((group1.map { it.coordinate }) - overlappingCoordinates).map { data[it]!! }
            val group2Other = ((group2.map { it.coordinate }) - overlappingCoordinates).map { data[it]!! }

            if (group1Other.all { it.notAllowed.contains(i) }) {
                group2Other.filter {
                    it.isEmpty() && !it.notAllowed.contains(i)
                }.map {
                    OneOf.right<Value, NotAllowed>(NotAllowed(it.coordinate, i))
                }
            } else {
                setOf<Conclusion>()
            }
        }.toSet()
    }

    /**
     * Processes the conclusions from each group
     */
    private fun processConclusion(data: MutableMap<Coordinate, SudokuSolveData>, conclusion: Conclusion) {
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
    override fun solve(input: SudokuInput): OneOf<SudokuOutput, List<LogicSolveError>> {
        val groups: Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>> = toGroups(input.groups)
        val data = buildSolvingData(input.values)

        if (isSolved(data)) {
            return toOutput(data)
        }

        do {
            val conclusions = gatherConclusions(groups, data)
            conclusions.forEach {
                processConclusion(data, it)
            }

            if (!isValid(groups, data)) {
                throw Exception("The puzzle is not valid: current state: ${toOutput(data)}")
            }
        } while (!conclusions.isEmpty())

        return if (isSolved(data)) {
            toOutput(data)
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
    private fun isValid(groups: Set<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>>,
                        data: MutableMap<Coordinate, SudokuSolveData>): Boolean {
        return groups.all { group ->
            val groupData = group(data)
            (1..9).all {i ->
                groupData.filter { it.hasValue() }.count { it.value == i } <= 1
            }
        }
    }

    /**
     * Generates the output given solving data
     */
    private fun toOutput(data: MutableMap<Coordinate, SudokuSolveData>): OneOf<SudokuOutput, List<LogicSolveError>> {
        val valueMap = data
                .filterValues { it.value != null }
                .mapValues {
                    it.value.value!!
                }
        return OneOf.left(SudokuOutput(valueMap))
    }
}
