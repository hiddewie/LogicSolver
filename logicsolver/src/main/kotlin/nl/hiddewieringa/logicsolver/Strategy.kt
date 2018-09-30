package nl.hiddewieringa.logicsolver

/**
 * A conclusion is either a found value, or a value which is not allowed somewhere
 */
typealias Conclusion = OneOf<Value, NotAllowed>

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

/**
 * Finds the missing value if all but one value is filled in the group
 */
class MissingValueGroupStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<OneOf<Value, NotAllowed>> {
        val m = (1..9).map { i ->
            i to data.find { it.value == i }
        }.toMap()

        return if ((1..9).filter { m[it] == null }.size == 1) {
            val coordinate = data.find { it.value == null }!!.coordinate
            val value = (1..9).find { m[it] == null }!!
            setOf(OneOf.left(Value(coordinate, value)))
        } else {
            setOf()
        }
    }
}

/**
 * If a value is given in a cell, than all other cells in the group are not allowed to contain that value
 */
class FilledValueNotAllowedInGroupStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<OneOf<Value, NotAllowed>> {
        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    data.filter {
                        hasValue.coordinate != it.coordinate && !it.notAllowed.contains(hasValue.value!!)
                    }.map {
                        OneOf.right<Value, NotAllowed>(NotAllowed(it.coordinate, hasValue.value!!))
                    }
                }.toSet()
    }
}

/**
 * If all but one value is not allowed in a cell, then that value must be true
 */
class SingleValueAllowedStrategy : Strategy<List<SudokuSolveData>, Conclusion> {
    override fun invoke(data: List<SudokuSolveData>): Set<OneOf<Value, NotAllowed>> {
        return data.filter(SudokuSolveData::isEmpty)
                .flatMap<SudokuSolveData, Conclusion> { solveData ->
                    val m = (1..9).map { i ->
                        i to solveData.notAllowed.contains(i)
                    }.toMap()

                    if (m.values.filter { !it }.size == 1) {
                        setOf(OneOf.left(Value(solveData.coordinate, m.filterValues { !it }.keys.first())))
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
    override fun invoke(data: List<SudokuSolveData>): Set<OneOf<Value, NotAllowed>> {
        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    val missingNotAllowed = (1..9) - listOf(hasValue.value!!) - hasValue.notAllowed
                    missingNotAllowed.map {
                        OneOf.right<Value, NotAllowed>(NotAllowed(hasValue.coordinate, it))
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
