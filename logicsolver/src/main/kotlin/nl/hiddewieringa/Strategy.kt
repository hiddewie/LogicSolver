package nl.hiddewieringa

/**
 * A conclusion is either a found value, or a value which is not allowed somewhere
 */
typealias Conclusion = OneOf<Value, NotAllowed>

/**
 * A strategy takes some input and genereates a set of conclusions
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
 * The group strategy gathers conclusions about cells in a group
 */
class GroupStrategy {

    /**
     * The substrategies that are used
     */
    val strategies: Set<Strategy<List<SudokuSolveData>, Conclusion>> = setOf(
            ::missingValue,
            ::filledValueNotAllowedInGroup,
            ::singleValueAllowed,
            ::filledValueRestNotAllowed
    )

    /**
     * Finds the missing value if all but one value is filled in the group
     */
    fun missingValue(data : List<SudokuSolveData>): Set<Conclusion> {
        val m = (1..9).map { i ->
            i to data.find { it.value == i }
        }.toMap()

        return if ((1..9).filter { m.get(it) == null }.size == 1) {
            val coordinate = data.find { it.value == null }!!.coordinate
            val value = (1..9).find { m.get(it) == null }!!
            setOf(OneOf.left(Value(coordinate, value)))
        } else {
            setOf()
        }
    }

    /**
     * If a value is given in a cell, than all other cells in the group are not allowed to contain that value
     */
    fun filledValueNotAllowedInGroup(data: List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    data.filter {
                        hasValue.coordinate != it.coordinate && !it.notAllowed.contains(hasValue.value!!)
                    }.map {
                        OneOf.right<Value, NotAllowed>(NotAllowed(it.coordinate, hasValue.value!!))
                    }
                }.toSet()
    }

    /**
     * If a value is given in a cell, then all other values are not allowed in that cell
     */
    fun filledValueRestNotAllowed(data: List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::hasValue)
                .flatMap { hasValue ->
                    val missingNotAllowed = (1..9) - listOf(hasValue.value!!) - hasValue.notAllowed
                    missingNotAllowed.map {
                        OneOf.right<Value, NotAllowed>(NotAllowed(hasValue.coordinate, it))
                    }
                }.toSet()
    }

    /**
     * If all but one value is not allowed in a cell, then that value must be true
     */
    fun singleValueAllowed(data: List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::isEmpty)
                .flatMap<SudokuSolveData, Conclusion> { solveData ->
                    val m = (1..9).map { i ->
                        i to solveData.notAllowed.contains(i)
                    }.toMap()

                    return if (m.values.filter { !it }.size == 1) {
                        setOf(OneOf.left(Value(solveData.coordinate, m.filterValues { !it }.keys.first())))
                    } else {
                        setOf()
                    }
                }.toSet()
    }

    /**
     * Gathers all conclusions from the substrategies
     */
    fun gatherConclusions(data: List<SudokuSolveData>): Set<Conclusion> {
        return strategies.flatMap {
            it(data)
        }.toSet()
    }
}
