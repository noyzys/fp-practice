package dev.noyzys.fp.kotlin.monad

import dev.noyzys.fp.kotlin.functor.Functor

interface Monad<out A> : Functor<A> {

    fun <V> pure(value: V): Monad<V>

    override fun <B> mapper(func: (A) -> B): Monad<B> = flatMap { a -> pure(func(a)) }

    infix fun <B> flatMap(func: (A) -> Monad<B>): Monad<B>

    infix fun <B> leadTo(m: Monad<B>): Monad<B> = flatMap { m }
}