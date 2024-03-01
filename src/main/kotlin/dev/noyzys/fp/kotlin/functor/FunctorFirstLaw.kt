package dev.noyzys.fp.kotlin.functor

fun main() {
    println(Nothing.mapper { identity(it) } == identity(Nothing))  // true
    println(Just(5).mapper { identity(it) } == identity(Just(5)))  // true

    // Tree 1 laws
    val tree = Node(1, Node(2, EmptyTree, EmptyTree), Node(3, EmptyTree, EmptyTree))

    println(EmptyTree.mapper { identity(it) } == identity(EmptyTree)) // true
    println(tree.mapper { identity(it) } == identity(tree)) // true

    // Either 1 laws
    println(Left("error").mapper { identity(it) } == identity(Left("error"))) // true
    println(Right(5).mapper { identity(it) } == identity(Right(5))) // true
}

fun <T> identity(x: T): T = x