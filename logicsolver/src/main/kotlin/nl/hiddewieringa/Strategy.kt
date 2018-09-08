package nl.hiddewieringa


// Strategy

typealias Conclusion = OneOf<Value, NotAllowed>

typealias Strategy<I, C> = (I) -> Set<C>

//interface Strategy<I, C> {
//    fun conclude(data: I): List<C>
//}

data class Value(val coordinate: Coordinate, val value: Int)

data class NotAllowed(val coordinate: Coordinate, val value: Int)

typealias Group<M, T> = (M) -> List<T>
//interface Group<M, T> {
//    fun elements(m: M): List<T>
//}

// TODO: make immutable
class GroupStrategy(val data: List<SudokuSolveData>) {

    val strategies: Set<Strategy<List<SudokuSolveData>, Conclusion>>

    init {
        strategies = setOf(
                ::missingValue,
                ::missingNotAllowed,
                ::singleValueAllowed,
                ::filledValueNotAllowedRest
        )
    }

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

    fun missingNotAllowed(data : List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::hasValue)
        .flatMap { hasValue ->
            data.filter {
                hasValue.coordinate != it.coordinate && !it.notAllowed.contains(hasValue.value!!)
            }.map {
                OneOf.right<Value, NotAllowed>(NotAllowed(it.coordinate, hasValue.value!!))
            }
        }.toSet()
    }

    fun filledValueNotAllowedRest(data : List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::hasValue)
        .flatMap {hasValue ->
            val missingNotAllowed = (1..9) - listOf(hasValue.value!!) - hasValue.notAllowed
            missingNotAllowed.map {
                OneOf.right<Value, NotAllowed>(NotAllowed(hasValue.coordinate, it))
            }
        }.toSet()
    }

    fun singleValueAllowed(data : List<SudokuSolveData>): Set<Conclusion> {
        return data.filter(SudokuSolveData::isEmpty).flatMap<SudokuSolveData, Conclusion> { solveData ->
            val m = (1..9).map { i ->
                i to solveData.notAllowed.contains(i)
            }.toMap()

            return if (m.values.filter { !it }.size == 1) {
                setOf(OneOf.left(Value(solveData.coordinate, m.filterValues { !it }.keys.first())))
            }  else {
                setOf()
            }
        }.toSet()
    }

    fun gatherConclusions() :Set<Conclusion> {
        return strategies.flatMap {
            it(data)
        }.toSet()
    }
}
