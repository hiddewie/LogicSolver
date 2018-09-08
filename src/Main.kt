import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.function.Function
import java.util.stream.IntStream.range

fun main(args: Array<String>) {
    println("Hello, world!")
}

// Infra


// Optional<T> -> either T or none


//class Either<L, R> : OneOf<L, R> {
//
//}

class OneOf<L, R>(val left: L?, val right: R?) {

    companion object Constructor {
        fun <L, R> left(left: L): OneOf<L, R> {
            return OneOf(left, null)
        }

        fun <L, R> right(right: R): OneOf<L, R> {
            return OneOf(null, right)
        }
    }

    fun <T> mapLeft(transform: Function<L, T>): OneOf<T, R> {
        return if (left != null) OneOf(transform.apply(left), right) else OneOf(null, right)
    }

    fun <T> mapRight(transform: Function<R, T>): OneOf<L, T> {
        return if (right != null) OneOf(left, transform.apply(right)) else OneOf(left, null)
    }

    fun <T> match(leftMatch: Function<L, T>, rightMatch: Function<R, T>): T {
        return if (left != null) leftMatch.apply(left) else rightMatch.apply(right())
    }

    fun isLeft(): Boolean {
        return left != null
    }

    fun left(): L {
        return left!!
    }

    fun isRight(): Boolean {
        return right != null
    }

    fun right(): R {
        return right!!
    }
}


//
//Result<R, E> -> Either<Result, Error>
//Result.map(R -> T) -> Result<T, E>
//.flatMap -> Result<T, E>
//
//OneOf<A, B, C...>: {
//    of(A),
//    of(B),
//    of(C),
//
//    OneOf<T, B, C> mapA<T>(A -> T)
//    // mapB
//    // mapC
//    OneOf<D, E, F> map<D, E, F>(A -> D, B -> E, C -> F)
//    T match<T>(A -> T, B -> T, C -> T)
//}


// Input


data class LogicSolveError(override val message: String) : Exception(message)

interface LogicPuzzleInput<I, O> {
    fun solve(): OneOf<O, List<LogicSolveError>>
}

data class Coordinate(val a: Int, val b: Int)


fun row(i: Int): List<Coordinate> {
    return (1..9).map {
        Coordinate(i, it)
    }
}

fun column(i: Int): List<Coordinate> {
    return (1..9).map {
        Coordinate(it, i)
    }
}

fun block(i: Int): List<Coordinate> {
    return (1..9).map {
        Coordinate(i / 3 + it / 3, i % 3 + it % 3)
    }
}

class Sudoku(values: Map<Coordinate, Int>) : SudokuInput(values, (1..9).flatMap {
    listOf(row(it), column(it), block(it))
})

open class SudokuInput(val values: Map<Coordinate, Int>, val groups: List<List<Coordinate>>) : LogicPuzzleInput<SudokuInput, SudokuOutput> {
    override fun solve(): OneOf<SudokuOutput, List<LogicSolveError>> {
        return OneOf.left(SudokuOutput(mapOf()))
    }
}

data class SudokuOutput(val values: Map<Coordinate, Int>)

//data class Sudoku

//new Sudoku(
//[(1,1) -> 2]
//).solve()
//
//new SudokuX(
//[(1,1) -> 1]
//).solve()
//
//new SudokuSnake(
//[(1,1) -> 1],
//[[(1,1), (2,2), (1,1)], [(3,4), (4,5)]]
//)

// SingleBlockPuzzle(size) {
//
// }
// Sudoku -> SingleBlockPuzzle(9)

//Sudoku(values) {
//    values...
//    groups = [
//        range(1, 9).flatmap(r -> [
//    row(r),
//    column(r),
//    block(r)
//    ]),
//    ]
//}

//SudokuSnake (values, groups) {
//    super()
//    verifyConnected(groups)
//    verifyGroupSizes(groups)
//    verifyAllIndicesTaken(groups)
//}

//Sudoku implements LogicPuzzleInput
//SudokuX implements LogicPuzzleInput
//... implements LogicPuzzleInput

//interface LogicPuzzleInput {
//
//    Result<Sudoku, Error> solve()
//
//}


// Solve

//SudokuSolver solver(SudokuInput input) {
//    new SudokuSolver (input)
//}

class SudokuSolveData (val coordinate: Coordinate, val value :Int?, val notAllowed: List<Int>)

//fun value(value: Int):SudokuSolveData {
//    return SudokuSolveData(value, (1..9).filterNot { it == value })
//}

class SudokuSolver(input: SudokuInput) {

    val groups: List<Group<Map<Coordinate, SudokuSolveData>, SudokuSolveData>> // List<(a:Map<Coordinate, SudokuSolveData> -> List<SudokuSolveData>)>
    val data: MutableMap<Coordinate, SudokuSolveData> = mutableMapOf()

    init  {
        // SudokuInput is groups and values

//        input.groups.each {
//
//        }

//        groups = listOf()


        groups = input.groups.map { coordinates: List<Coordinate> ->
            { v: Map<Coordinate, SudokuSolveData>->
                coordinates.map { v.get(it) }.filterNotNull()
            }
        }

        (1..9).forEach {i ->
            range(1, 9).forEach { j->
                val coordinate = Coordinate(i,j)
                data[coordinate] = if (input.values.containsKey(coordinate)) {
                    SudokuSolveData(coordinate, input.values[coordinate]!!, listOf())
                } else {
                    SudokuSolveData(coordinate, null, listOf())
                }
            }
        }
//        values = values(k: v) -> k: value(v)
//        notAllowed = values(k: )
//        rules = range(1, 9) -> {}
    }

//    fun processConclusion()  {
////        groups.each {
////            processConclusion(it)
////        }
//    }

   fun gatherConclusions(): List<Conclusion>
    {
       return groups.flatMap {
            return GroupStrategy(it(data)).gatherConclusions()
//            it().gatherConclusion(it)
        }
    }

    fun processConclusion(conclusion: Conclusion) {
        if (conclusion.conclusion.isLeft()) {
            val value = conclusion.conclusion.left()
            data[value.coordinate] = SudokuSolveData(value.coordinate, value.value, data[value.coordinate]!!.notAllowed)
        } else {
            val notAllowed = conclusion.conclusion.right()
            data[notAllowed.coordinate] = SudokuSolveData(notAllowed.coordinate, data[notAllowed.coordinate]!!.value, data[notAllowed.coordinate]!!.notAllowed + listOf(notAllowed.value))
        }
    }

    fun  solve(): OneOf<SudokuOutput, List<LogicSolveError>> {
do {
        val conclusions = gatherConclusions()
        if (conclusions.isEmpty()) {
        } else {
            conclusions.forEach(::processConclusion)
//            processConclusions()
//            conclusions.forEach {
//                processConclusion(it)
//            }
        }
    } while(!conclusions.isEmpty())

        return if (isSolved()) {
            OneOf.left(SudokuOutput(mapOf()))
        } else {
            return OneOf.right(listOf(LogicSolveError("No more conclusions, cannot solve")))

        }
    }

    fun isSolved():Boolean {
        return !data.values.any {
            it.value == null
        }
    }
}


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
