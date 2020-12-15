package io.smallibs.core

class And<L: Handler, R: Handler>(val left: L, val right: R): Handler {
    companion object {
        infix fun <L: Handler, R: Handler> L.and(right: R) = And(this, right)
    }

    operator fun component1(): L = left

    operator fun component2(): R = right
}

