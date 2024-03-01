package dev.noyzys.fp.kotlin.monoid

interface Foldable<out A> {

    fun <B> foldLeft(acc: B, f: (B, A) -> B): B

    fun <B> foldMap(func: (A) -> B, m: Monoid<B>): B 
            = foldLeft(m.mempty()) { b , a -> m.mappend(b, func(a)) }

}