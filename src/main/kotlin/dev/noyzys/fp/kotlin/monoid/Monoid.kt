package dev.noyzys.fp.kotlin.monoid

import dev.noyzys.fp.kotlin.monad.Monad.FunList

interface Monoid<T> {

    fun mempty(): T

    fun mappend(m1: T, m2: T): T
}

fun <T> Monoid<T>.mconcat(list: FunList<T>): T = list.foldRight(mempty(), ::mappend)

class ProductMonoid : Monoid<Int> {

    override fun mempty(): Int = 1

    override fun mappend(m1: Int, m2: Int): Int = m1 * m2
}

class SumMonoid : Monoid<Int> {

    override fun mempty(): Int = 0

    override fun mappend(m1: Int, m2: Int): Int = m1 + m2
}