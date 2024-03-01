package dev.noyzys.fp.kotlin.monoid

fun main() {
    val tree = Node(1,
            Node(2,
                    Node(3), Node(4)),
            Node(5,
                    Node(6), Node(7)))

    println(tree.foldLeft(0) { a, b -> a + b })    // 28
    println(tree.foldLeft(1) { a, b -> a * b })    // 5040

    println(tree.foldMap({ a -> a * 2 }, SumMonoid()))  // 56
    println(tree.foldMap({ a -> a + 1 }, ProductMonoid()))  // 40320



    // sum monoid
    val x = 1
    val y = 2
    val z = 3
    SumMonoid().run {
        println(mappend(mempty(), x) == x)  // true
        println(mappend(x, mempty()) == x)  // true
        println(mappend(mappend(x, y), z) == mappend(x, mappend(y, z)))  // true
    }

    println(SumMonoid().mconcat(funListOf(1, 2, 3, 4, 5)))      // 15

    // product monoid
    ProductMonoid().run {
        println(mappend(mempty(), x) == x)  // true
        println(mappend(x, mempty()) == x)  // true
        println(mappend(mappend(x, y), z) == mappend(x, mappend(y, z)))  // true
    }

    println(ProductMonoid().mconcat(funListOf(1, 2, 3, 4, 5)))      // 120
}

sealed class BinaryTree<out A> : Foldable<A> {

    override fun <B> foldLeft(acc: B, func: (B, A) -> B): B = when (this) {
        is EmptyTree -> acc
        is Node -> {
            val leftAcc = leftTree.foldLeft(acc, f)
            val rootAcc = f(leftAcc, value)
            rightTree.foldLeft(rootAcc, f)
        }
    }
}

data class Node<A>(val value: A, val leftTree: BinaryTree<A> = EmptyTree, val rightTree: BinaryTree<A> = EmptyTree) : BinaryTree<A>()
object EmptyTree : BinaryTree<kotlin.Nothing>()
