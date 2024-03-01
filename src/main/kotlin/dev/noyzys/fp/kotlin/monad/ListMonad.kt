package dev.noyzys.fp.kotlin.monad

fun main(args: Array<String>) {
    val list1 = funListOf(1, 2, 3)
    val list2 = funListOf(5, 10, 15, 20)

    println(list1.mempty())   // []
    println(list1 mappend list2)    // [1, 2, 3, 5, 10, 15, 20]
    println(list1.mapper { x -> x * 2 })  // [2, 4, 6]
    println(FunList.pure(10))   // [10]

    val list3 = funListOf<(Int) -> Int>({ x -> x * 2 }, { x -> x + 1 }, { x -> x - 10 })
    println(list3 apply list1)  // [2, 4, 6, 2, 3, 4, -9, -8, -7]
    println(list3 apply list2)  // [10, 20, 30, 40, 6, 11, 16, 21, -5, 0, 5, 10]
    println(list1 _apply list3)  // [2, 4, 6, 2, 3, 4, -9, -8, -7]
    println(list2 _apply list3)  // [10, 20, 30, 40, 6, 11, 16, 21, -5, 0, 5, 10]

    println(Nil flatMap { x -> funListOf(x) })  // []
    println(list1 flatMap { x -> funListOf(x, -x) })    // [1, -1, 2, -2, 3, -3]
    println(funListOf(list1, list2).flatten())  // [1, 2, 3, 5, 10, 15, 20]

    println(funListOf(1, 2)
            .flatMap { x -> funListOf(x to 'a', x to 'c') }     // [(1, a), (1, c), (2, a), (2, c)]
            .mapper { x -> x.first to x.second.toUpperCase() }     // [(1, A), (1, C), (2, A), (2, C)]
            ._apply(funListOf<(Pair<Int, Char>) -> Char>({ x -> x.second }, { x -> x.second + x.first }))   // [A, B, C, D, A, C, C, E]
            .distinct()   // [E, D, C, B, A]
            .reverse()  // [A, B, C, D, E]
    )
}

sealed class FunList<out T> {
    companion object
}

object Nil : FunList<kotlin.Nothing>() {
    override fun toString(): String = "[]"
}

data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>() {
    override fun toString(): String = "[${foldLeft("") { acc, x -> "$acc, $x" }.drop(2)}]"
}

fun <T> funListOf(vararg elements: T): FunList<T> = elements.toFunList()

private fun <T> Array<out T>.toFunList(): FunList<T> = when {
    this.isEmpty() -> Nil
    else -> Cons(this[0], this.copyOfRange(1, this.size).toFunList())
}

fun <T> FunList<T>.mempty() = Nil

infix fun <T> FunList<T>.mappend(other: FunList<T>): FunList<T> = when (this) {
    is Nil -> other
    is Cons -> Cons(head, tail.mappend(other))
}

fun <T> FunList<FunList<T>>.flatten(): FunList<T> = foldRight(mempty()) { t, r: FunList<T> -> t mappend r }

fun <T> FunList.Companion.pure(value: T): FunList<T> = Cons(value, Nil)

infix fun <T, R> FunList<(T) -> R>.apply(func: FunList<T>): FunList<R> = when (this) {
    is Nil -> Nil
    is Cons -> f.mapper(head) mappend tail.apply(f)
}

infix fun <T, R> FunList<T>._apply(func: FunList<(T) -> R>): FunList<R> = when (this) {
    is Nil -> Nil
    is Cons -> f.mapper { it(head) } mappend tail._apply(f)
}

infix fun <T, R> FunList<T>.flatMap(func: (T) -> FunList<R>): FunList<R> = mapper(f).flatten()

infix fun <T, R> FunList<T>.mapper(func: (T) -> R): FunList<R> = when (this) {
    is Nil -> Nil
    is Cons -> Cons(f(head), tail.mapper(f))
}

fun <T, R> FunList<T>.foldRight(acc: R, func: (T, R) -> R): R = when (this) {
    is Nil -> acc
    is Cons -> f(head, tail.foldRight(acc, f))
}

tailrec fun <T, R> FunList<T>.foldLeft(acc: R, func (R, T) -> R): R = when (this) {
    is Nil -> acc
    is Cons -> tail.foldLeft(f(acc, head), f)
}

tailrec fun <T> FunList<T>.contains(element: T): Boolean = when (this) {
    is Nil -> false
    is Cons -> if (element == head) true else tail.contains(element)
}

fun <T> FunList<T>.distinct(): FunList<T> =
    foldLeft(Nil as FunList<T>) { acc, x -> if (acc.contains(x)) acc else Cons(x, acc) }

tailrec fun <T> FunList<T>.reverse(acc: FunList<T> = Nil): FunList<T> = when (this) {
    is Nil -> acc
    is Cons -> tail.reverse(Cons(head, acc))
}

fun <T> FunList<T>.filter(acc: FunList<T> = Nil, func: (T) -> Boolean): FunList<T> = when (this) {
    Nil -> acc.reverse()
    is Cons -> if (f(head)) {
        tail.filter(Cons(head, acc), f)
    } else {
        tail.filter(acc, f)
    }
}

fun<T> printFunList(list: FunList<T>) = println(list.toStringByFoldLeft())

private fun <T> FunList<T>.toStringByFoldLeft(): String = "[${foldLeft("") { acc, x -> "$acc, $x" }.drop(2)}]"