package dev.noyzys.fp.kotlin.functor

fun main() {
    val tree = treeOf(1,
            treeOf(2,
                    treeOf(3), treeOf(4)),
            treeOf(5,
                    treeOf(6), treeOf(7)))

    println(tree) 

    val transformedTree = tree.mapper { it + 1 }

    println(transformedTree) 
}

fun <T> treeOf(value: T, leftTree: Tree<T> = EmptyTree, rightTree: Tree<T> = EmptyTree): Tree<T> =
        Node(value, leftTree, rightTree)

sealed class Tree<out A> : Functor<A> {

    abstract override fun toString(): String

    abstract override fun <B> mapper(func: (A) -> B): Tree<B>
}

data class Node<out A>(val value: A, val leftTree: Tree<A> = EmptyTree, val rightTree: Tree<A> = EmptyTree) : Tree<A>() {

    override fun toString(): String = "(N $value $leftTree $rightTree)"

    override fun <B> mapper(func: (A) -> B): Tree<B> =
            Node(func(value), leftTree.mapper(func), rightTree.mapper(func))
}

object EmptyTree : Tree<Nothing>() {

    override fun toString(): String = "FP"

    override fun <B> mapper(func: (Nothing) -> B): Tree<B> = EmptyTree
}