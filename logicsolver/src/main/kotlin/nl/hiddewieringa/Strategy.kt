package nl.hiddewieringa


// Strategy

//group: SudokuSolver -> [SudokuValue]?

data class Conclusion(val conclusion: OneOf<Value, NotAllowed>)

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

//            { data: List<SudokuSolveData> ->  allValueExceptOne ? findMissingValue : <> },
//            { data: List<SudokuSolveData> -> data.unfilled.each { value -> singleNumberAllowed ? valueOfMissingNumber : <> } }
//            { data: List<SudokuSolveData> -> data.values.each { v -> data.unfilled.allowed(v) -> NotAllowed(v) } }
        )
    }
    fun missingValue(data : List<SudokuSolveData>): List<Conclusion> {
        val m = (1..9).map { i ->
            i to data.find { it.value == i }
        }.toMap()

        return if ((1..9).map { m.get(it) }.contains(null)) {
            val coordinate = data.find { it.value == null }!!.coordinate
            val value = (1..9).find { m.get(it) == null }!!
            listOf(Conclusion(OneOf.left(Value(coordinate, value))))
        } else {
            listOf()
        }

//        return if (data.filter { it.value != null }.toSet().size() == 8) {
//            listOf(OneOf.left(Value(data.filter {  })))
//        } else {
//            listOf()
//        }
    }


    fun missingNotAllowed(data : List<SudokuSolveData>): List<Conclusion> {
        return data.filter {
            it.value != null
        }
                .flatMap {hasValue ->
                    data.filter { it: SudokuSolveData ->
                        !it.notAllowed.contains(hasValue.value!!)
                    }.map {
                        Conclusion(OneOf.right(NotAllowed(it.coordinate, hasValue.value!!)))
                    }
                }
    }

    fun singleValueAllowed(data : List<SudokuSolveData>): List<Conclusion> {
        return data.flatMap { solveData ->
            val m = (1..9).map { i ->
                i to solveData.notAllowed.contains(i)
            }.toMap()

            return if (m.values.filter { !it }.size == 1) {
                listOf(Conclusion(OneOf.left(Value(solveData.coordinate, m.filterValues { !it }.keys.first()))))
            }  else {
                listOf()
            }
        }
//
//        return data.filter {
//            it.notAllowed.size() == 8
//        }
//                .map {  }
//                .flatMap {hasValue ->
//                    data.filter { it: SudokuSolveData ->
//                        !it.notAllowed.contains(hasValue.value!!)
//                    }.map {
//                        Conclusion(OneOf.right(NotAllowed(it.coordinate, hasValue.value!!)))
//                    }
//                }
    }


//    data : OneOf<Value, NotAllowed>[] = [Value(1), [NotAllowed(1), NotAllowed(2), NotAllowed(3)]]

//    fun ifApplicable(coordinate: Coordinate): Boolean {
//        group.elements().has(coordinate)
//    }

    fun gatherConclusions() :List<Conclusion> {
        return strategies.flatMap {
            it(data)
        }
    }

//    processConclusion(Conclusion it)
//    {
//        ifApplicable it {
//            process(it)
//        }
//    }
}
