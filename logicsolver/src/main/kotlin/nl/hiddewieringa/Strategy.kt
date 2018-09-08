package nl.hiddewieringa


// Strategy

typealias Conclusion = OneOf<Value, NotAllowed>

typealias Strategy<I, C> = (I) -> List<C>

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

    val strategies: List<Strategy<List<SudokuSolveData>, Conclusion>>

    init {
        strategies = listOf(
                ::missingValue,
                ::missingNotAllowed,
                ::singleValueAllowed
                // TODO: filled value not allowed rest
        )
    }

    fun missingValue(data : List<SudokuSolveData>): List<Conclusion> {
        val m = (1..9).map { i ->
            i to data.find { it.value == i }
        }.toMap()

        return if ((1..9).filter { m.get(it) == null }.size == 1) {
            val coordinate = data.find { it.value == null }!!.coordinate
            val value = (1..9).find { m.get(it) == null }!!
            listOf(OneOf.left(Value(coordinate, value)))
        } else {
            listOf()
        }
    }

    fun missingNotAllowed(data : List<SudokuSolveData>): List<Conclusion> {
        return data.filter {
            it.value != null
        }
        .flatMap { hasValue ->
            data.filter {
                hasValue.coordinate != it.coordinate && !it.notAllowed.contains(hasValue.value!!)
            }.map {
                OneOf.right<Value, NotAllowed>(NotAllowed(it.coordinate, hasValue.value!!))
            }
        }
    }

    fun filedValueNotAllowedRest(data : List<SudokuSolveData>): List<Conclusion> {
        return data.filter {
            it.value != null
        }
        .flatMap { hasValue ->
            data.filter {
                hasValue.coordinate != it.coordinate && !it.notAllowed.contains(hasValue.value!!)
            }.map {
                OneOf.right<Value, NotAllowed>(NotAllowed(it.coordinate, hasValue.value!!))
            }
        }
    }

    fun singleValueAllowed(data : List<SudokuSolveData>): List<Conclusion> {
        return data.flatMap { solveData ->
            val m = (1..9).map { i ->
                i to solveData.notAllowed.contains(i)
            }.toMap()

            return if (m.values.filter { !it }.size == 1) {
                listOf(OneOf.left(Value(solveData.coordinate, m.filterValues { !it }.keys.first())))
            }  else {
                listOf()
            }
        }
    }

    fun gatherConclusions() :List<Conclusion> {
        val conclusions = strategies.flatMap<Strategy<List<SudokuSolveData>, Conclusion>, Conclusion> {
            val con = it(data)
            println("Found conclusions of ${con} for strategy ${it}")
            return con
        }
        return conclusions
    }
}
