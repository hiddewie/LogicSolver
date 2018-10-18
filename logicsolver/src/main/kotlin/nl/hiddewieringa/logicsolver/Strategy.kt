package nl.hiddewieringa.logicsolver

/**
 * A conclusion is either a found value, or a value which is not allowed somewhere
 */
typealias Conclusion = OneOf<Value, NotAllowed>

fun concludeNotAllowed(coordinate: Coordinate, value: Int): Conclusion {
    return OneOf.right(NotAllowed(coordinate, value))
}

fun <T> Collection<T>.concludeNotAllowed(coordinate: (T) -> Coordinate, value: (T) -> Int): List<Conclusion> {
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
typealias Group<M, T> = (M) -> Set<T>

typealias SudokuGroup = Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>

typealias GroupsWithData = Triple<Set<SudokuGroup>, Map<Coordinate, SudokuSolveData>, Set<Int>>

typealias DataWithValues = Pair<Set<SudokuSolveData>, Set<Int>>

fun Collection<SudokuSolveData>.coordinates(): List<Coordinate> {
    return map { it.coordinate }
}

fun <T> List<Coordinate>.toValues(data: Map<Coordinate, T>): List<T> {
    return map { data[it]!! }
}

fun Collection<SudokuSolveData>.ignoreCoordinates(coordinates: Set<Coordinate>, data: Map<Coordinate, SudokuSolveData>): List<SudokuSolveData> {
    return (coordinates() - coordinates).toValues(data)
}

/**
 * Finds the missing value if all but one value is filled in the group
 */
class MissingValueGroupStrategy : Strategy<DataWithValues, Conclusion> {
    override fun invoke(dataWithValues: DataWithValues): Set<Conclusion> {
        val (data, values) = dataWithValues

        val m = values.map { i ->
            i to data.find { it.value == i }
        }.toMap()

        return if (values.filter { m[it] == null }.size == 1) {
            val coordinate = data.find { it.value == null }!!.coordinate
            val value = values.find { m[it] == null }!!
            setOf(concludeValue(coordinate, value))
        } else {
            setOf()
        }
    }
}

/**
 * If a value is given in a cell, than all other cells in the group are not allowed to contain that value
 */
class FilledValueNotAllowedInGroupStrategy : Strategy<DataWithValues, Conclusion> {
    override fun invoke(dataWithValues: DataWithValues): Set<Conclusion> {
        val (data, _) = dataWithValues

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
class SingleValueAllowedStrategy : Strategy<DataWithValues, Conclusion> {
    override fun invoke(dataWithValues: DataWithValues): Set<Conclusion> {
        val (data, values) = dataWithValues

        return data.filter(SudokuSolveData::isEmpty)
                .flatMap { solveData ->
                    val m = values.map { i ->
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
class FilledValueRestNotAllowedStrategy : Strategy<DataWithValues, Conclusion> {
    override fun invoke(dataWithValues: DataWithValues): Set<Conclusion> {
        val (data, values) = dataWithValues

        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    val missingNotAllowed = values - setOf(hasValue.value!!) - hasValue.notAllowed
                    missingNotAllowed.concludeNotAllowed({ hasValue.coordinate }, { it })
                }.toSet()
    }
}

/**
 * If a value is given in a cell, then all other values are not allowed in that cell
 */
class TwoNumbersTakeTwoPlacesStrategy : Strategy<DataWithValues, Conclusion> {
    override fun invoke(dataWithValues: DataWithValues): Set<Conclusion> {
        val (data, values) = dataWithValues

        return values.flatMap { a ->
            values.filter { b ->
                a != b
            }.flatMap { b ->
                twoNumbers(data, a, b, values)
            }
        }.toSet()
    }

    private fun twoNumbers(data: Set<SudokuSolveData>, a: Int, b: Int, values: Set<Int>): List<Conclusion> {
        val allowedA = data.whereValueIsAllowed(a)
        val allowedB = data.whereValueIsAllowed(b)

        if (allowedA.size != 2 || allowedB.size != 2 || allowedA != allowedB) {
            return listOf()
        }

        return allowedA.findByCoordinate(data)
                .flatMap { solveData ->
                    (values - solveData.notAllowed - listOf(a, b))
                            .concludeNotAllowed({ solveData.coordinate }, { it })
                }
    }

    private fun Collection<SudokuSolveData>.whereValueIsAllowed(value: Int): Set<Coordinate> {
        return filter { !it.notAllowed.contains(value) }.coordinates().toSet()
    }

    private fun Set<Coordinate>.findByCoordinate(data: Collection<SudokuSolveData>): List<SudokuSolveData> {
        return map { coordinate -> data.find { it.coordinate == coordinate }!! }
    }
}

/**
 * If two numbers are only allowed in exactly two places, they are not allowed in any other places.
 */
class TwoNumbersOnlyInTwoPlacesStrategy : Strategy<DataWithValues, Conclusion> {
    override fun invoke(dataWithValues: DataWithValues): Set<Conclusion> {
        val (data, values) = dataWithValues

        val twoAllowed = data.filter {
            it.notAllowed.size == values.size - 2
        }

        return twoAllowed.flatMap { a ->
            twoAllowed.filter { b ->
                a.coordinate != b.coordinate
            }.filter { b ->
                a.notAllowed.toSet() == b.notAllowed.toSet()
            }.flatMap { b ->
                conclusionsForTwoPlaces(data, a, b, values)
            }
        }.toSet()
    }

    private fun conclusionsForTwoPlaces(data: Set<SudokuSolveData>, a: SudokuSolveData, b: SudokuSolveData,
                                        values: Set<Int>): List<Conclusion> {
        val allowed = values.toList() - a.notAllowed

        return allowed.flatMap { allowedValue ->
            data.filter { !it.notAllowed.contains(allowedValue) }
                    .filter { it.coordinate != a.coordinate && it.coordinate != b.coordinate }
                    .concludeNotAllowed({ it.coordinate }, { allowedValue })
        }
    }
}

class OverlappingGroupsStrategy : Strategy<GroupsWithData, Conclusion> {

    override fun invoke(groupsWithData: GroupsWithData): Set<Conclusion> {
        val (groups, data, values) = groupsWithData

        val evaluatedGroups = groups.map { it(data) }
        return evaluatedGroups.flatMap { group1 ->
            (evaluatedGroups - setOf(group1)).flatMap { group2 ->
                overlappingConclusionsForGroups(group1, group2, data, values)
            }
        }.toSet()
    }

    fun overlappingConclusionsForGroups(group1: Set<SudokuSolveData>, group2: Set<SudokuSolveData>,
                                        data: Map<Coordinate, SudokuSolveData>, values: Set<Int>): Set<Conclusion> {
        val overlappingCoordinates = group1.coordinates().intersect(group2.coordinates())
        if (overlappingCoordinates.size < 2) {
            return setOf()
        }
        if (overlappingCoordinates.all { data[it]!!.hasValue() }) {
            return setOf()
        }

        return values.flatMap { i ->
            overlappingConclusionsForGroupsAndValue(
                    group1.ignoreCoordinates(overlappingCoordinates, data),
                    group2.ignoreCoordinates(overlappingCoordinates, data),
                    i
            )
        }.toSet()
    }

    private fun overlappingConclusionsForGroupsAndValue(group1Other: List<SudokuSolveData>, group2Other: List<SudokuSolveData>,
                                                        value: Int): List<Conclusion> {
        return if (group1Other.all { it.isNotAllowed(value) }) {
            group2Other.filter {
                it.isEmpty() && it.isAllowed(value)
            }.concludeNotAllowed({ it.coordinate }, { value })
        } else {
            listOf()
        }
    }
}

/**
 * The group strategy gathers conclusions about cells in a group
 */
class GroupStrategy : Strategy<DataWithValues, Conclusion> {

    /**
     * The substrategies that are used
     */
    private val strategies: Set<Strategy<DataWithValues, Conclusion>> = setOf(
            MissingValueGroupStrategy(),
            FilledValueNotAllowedInGroupStrategy(),
            SingleValueAllowedStrategy(),
            FilledValueRestNotAllowedStrategy(),
            TwoNumbersTakeTwoPlacesStrategy(),
            TwoNumbersOnlyInTwoPlacesStrategy()
    )

    /**
     * Gathers all conclusions from the substrategies
     */
    override fun invoke(data: DataWithValues): Set<OneOf<Value, NotAllowed>> {
        return strategies.flatMap { strategy ->
            strategy(data)
        }.toSet()
    }
}
