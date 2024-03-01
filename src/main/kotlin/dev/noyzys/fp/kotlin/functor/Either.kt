package dev.noyzys.fp.kotlin.functor

import kotlin.Nothing

fun main() {
    println(pure(5))  // Right(value=2)
    println(pure(0))  // Left(value=divide by zero)
    
    println(pure(5).mapper { r -> r * 2 })  // Right(value=4)
    println(pure(0).mapper { r -> r * 2 })  // Left(value=divide by zero)
}

private fun pure(n: Int): Either<String, Int> = try {
    Right(10 / n)
} catch (e: ArithmeticException) {
    Left("divide by zero")
}

// LOGIC
sealed class Either<out L, out R> : Functor<R> {
    abstract override fun <R2> mapper(func: (R) -> R2): Either<L, R2>
}

data class Left<out L>(val value: L): Either<L, Nothing>() {
    override fun <R2> mapper(func: (Nothing) -> R2): Either<L, R2> = this
}

data class Right<out R>(val value: R): Either<Nothing, R>() {
    override fun <R2> mapper(func: (R) -> R2): Either<Nothing, R2> = Right(func(value))
}

inline fun <L, R, T> Either<L, R>.fold(left: (L) -> T, right: (R) -> T): T {
    return when (this) {
        is Left -> left(value)
        is Right -> right(value)
    }
}

inline fun <C, L : C, R : C, T> Either<L, R>.fold(func: (C) -> T): T {
    return fold(func, func)
}

@Suppress("UNCHECKED_CAST")
inline fun <L, R, U> Either<L, R>.flatMap(mapper: (R) -> Either<L, U>): Either<L, U> = when(this) {
    is Either.Left -> this as Either<L, U>
    is Either.Right -> mapper(this.right)
}

fun <T, L> T?.either(left: L): Either<L, T> =
    if(this == null) Either.Left(left) else Either.Right(this)

inline fun <L, R> Either<L, R>.leftPeek(func: (L) -> Unit): Either<L, R> {
    if (this is Either.Left) {
        f(this.left)
    }

    return this
}

inline fun <L, R> Either<L, R>.leftPeekIf(peekIf: (L) -> Boolean, func: (L) -> Unit): Either<L, R> {
    if (this is Either.Left) {
        if (peekIf(this.left)) {
            f(this.left)
        }
    }

    return this
}

inline fun <L, R> Either<L, R>.filterOrElse(filter: (R) -> Boolean, orElse: () -> L): Either<L, R> = when(this) {
    is Either.Left -> this
    is Either.Right -> if (filter(this.right)) this else left(orElse())
}
