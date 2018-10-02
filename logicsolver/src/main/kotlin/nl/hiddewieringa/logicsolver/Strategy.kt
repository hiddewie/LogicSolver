package nl.hiddewieringa.logicsolver

/**
 * A conclusion is either a found value, or a value which is not allowed somewhere
 */
typealias Conclusion = OneOf<Value, NotAllowed>

fun concludeNotAllowed(coordinate: Coordinate, value: Int): Conclusion {
    return OneOf.right(NotAllowed(coordinate, value))
}

fun <T> List<T>.concludeNotAllowed(coordinate: (T) -> Coordinate, value: (T) -> Int): List<Conclusion> {
    return map { concludeNotAllowed(coordinate(it), value(it)) }
}

fun concludeValue(coordinate: Coordinate, value: Int): Conclusion {
    return OneOf.left(Value(coordinate, value))
}

/**
 * A strategy takes some input and generates a set of conclusions
 */
typealias Strategy<I, C> = (I) -> Set<C>

/**
 * The value in a sudoku cell
 */
data class Value(val coordinate: Coordinate, val value: Int)

/**
 * A value which is not allowed in a sudoku cell
 */
data class NotAllowed(val coordinate: Coordinate, val value: Int)

/**
 * A group transforms the working puzzle data into a working list of data
 */
typealias Group<M, T> = (M) -> List<T>

typealias SudokuGroup = Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>

/**
 * Finds the missing value if all but one value is filled in the group
 */
class MissingValueGroupStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<Conclusion> {
        val m = (1..9).map { i ->
            i to data.find { it.value == i }
        }.toMap()

        return if ((1..9).filter { m[it] == null }.size == 1) {
            val coordinate = data.find { it.value == null }!!.coordinate
            val value = (1..9).find { m[it] == null }!!
            setOf(concludeValue(coordinate, value))
        } else {
            setOf()
        }
    }
}

/**
 * If a value is given in a cell, than all other cells in the group are not allowed to contain that value
 */
class FilledValueNotAllowedInGroupStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    data.filter {
                        hasValue.coordinate != it.coordinate && !it.notAllowed.contains(hasValue.value!!)
                    }.concludeNotAllowed({ it.coordinate }, { hasValue.value!! })
                }.toSet()
    }
}

/**
 * If all but one value is not allowed in a cell, then that value must be true
 */
class SingleValueAllowedStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::isEmpty)
                .flatMap { solveData ->
                    val m = (1..9).map { i ->
                        i to solveData.notAllowed.contains(i)
                    }.toMap()

                    if (m.values.filter { !it }.size == 1) {
                        val value = m.filterValues { !it }.keys.first()
                        setOf(concludeValue(solveData.coordinate, value))
                    } else {
                        setOf()
                    }
                }.toSet()
    }
}

/**
 * If a value is given in a cell, then all other values are not allowed in that cell
 */
class FilledValueRestNotAllowedStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    val missingNotAllowed = (1..9) - listOf(hasValue.value!!) - hasValue.notAllowed
                    missingNotAllowed.concludeNotAllowed({ hasValue.coordinate }, { it })
                }.toSet()
    }
}

typealias GroupsWithData = Pair<Set<SudokuGroup>, Map<Coordinate, SudokuSolveData>>

class OverlappingGroupsStrategy : Strategy<GroupsWithData, Conclusion> {

    override fun invoke(pair: GroupsWithData): Set<Conclusion> {
        val groups = pair.first
        val data = pair.second

        val evaluatedGroups = groups.map { it(data) }
        return evaluatedGroups.flatMap { group1 ->
            (evaluatedGroups - setOf(group1)).flatMap { group2 ->
                overlappingConclusionsForGroups(group1, group2, data)
            }
        }.toSet()
    }

    private fun List<SudokuSolveData>.coordinates(): List<Coordinate> {
        return map { it.coordinate }
    }

    private fun <T> List<Coordinate>.toValues(data: Map<Coordinate, T>): List<T> {
        return map { data[it]!! }
    }

    private fun List<SudokuSolveData>.ignoreCoordinates(coordinates: Set<Coordinate>, data: Map<Coordinate, SudokuSolveData>): List<SudokuSolveData> {
        return (coordinates() - coordinates).toValues(data)
    }

    fun overlappingConclusionsForGroups(group1: List<SudokuSolveData>, group2: List<SudokuSolveData>,
                                        data: Map<Coordinate, SudokuSolveData>): Set<Conclusion> {
        val overlappingCoordinates = group1.coordinates().intersect(group2.coordinates())
        if (overlappingCoordinates.size < 2) {
            return setOf()
        }
        if (overlappingCoordinates.all { data[it]!!.hasValue() }) {
            return setOf()
        }

        return (1..9).flatMap { i ->
            val group1Other = group1.ignoreCoordinates(overlappingCoordinates, data)
            val group2Other = group2.ignoreCoordinates(overlappingCoordinates, data)

            if (group1Other.all { it.notAllowed.contains(i) }) {
                group2Other.filter {
                    it.isEmpty() && !it.notAllowed.contains(i)
                }.concludeNotAllowed({ it.coordinate }, { i })
            } else {
                setOf<Conclusion>()
            }
        }.toSet()
    }
}

/**
 * The group strategy gathers conclusions about cells in a group
 */
class GroupStrategy : Strategy<List<SudokuSolveData>, Conclusion> {

    /**
     * The substrategies that are used
     */
    private val strategies: Set<Strategy<List<SudokuSolveData>, Conclusion>> = setOf(
            MissingValueGroupStrategy(),
            FilledValueNotAllowedInGroupStrategy(),
            SingleValueAllowedStrategy(),
            FilledValueRestNotAllowedStrategy()
    )

    /**
     * Gathers all conclusions from the substrategies
     */
    override fun invoke(data: List<SudokuSolveData>): Set<OneOf<Value, NotAllowed>> {
        return strategies.flatMap { strategy ->
            strategy(data)
        }.toSet()
    }
}
