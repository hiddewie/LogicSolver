package nl.hiddewieringa

import java.util.function.Function


// Infra

class OneOf<L, R>(val left: L?, val right: R?) {

    companion object Constructor {
        fun <L, R> left(left: L): OneOf<L, R> {
            return OneOf(left, null)
        }

        fun <L, R> right(right: R): OneOf<L, R> {
            return OneOf(null, right)
        }
    }

    fun <T> mapLeft(transform: (L) -> T): OneOf<T, R> {
        return if (left != null) OneOf(transform(left), right) else OneOf(null, right)
    }

    fun <T> mapRight(transform: (R) -> T): OneOf<L, T> {
        return if (right != null) OneOf(left, transform(right)) else OneOf(left, null)
    }

    fun <T> match(leftMatch: (L) -> T, rightMatch: (R) -> T): T {
        return if (left != null) leftMatch(left) else rightMatch(right())
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

    override fun toString():String {
        return if (isLeft()) {
            "<${left()}|>"
        } else {
            "<|${right()}>"
        }
    }

    override fun hashCode(): Int {
        var result = left?.hashCode() ?: 0
        result = 31 * result + (right?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OneOf<*, *>

        if (left != other.left) return false
        if (right != other.right) return false

        return true
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
