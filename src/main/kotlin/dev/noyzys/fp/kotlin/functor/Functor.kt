package dev.noyzys.fp.kotlin.functor

interface Functor<out A> {
    fun <B> mapper(func: (A) -> B): Functor<B>
}