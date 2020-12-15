package io.smallibs.effects

import io.smallibs.core.And
import io.smallibs.core.And.Companion.and
import io.smallibs.core.Effects.Companion.handle
import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.coroutines.resume
import kotlin.test.Test

class StateAndLogTest {

    @Test
    fun shouldPerformEffect() {
        val log: AtomicRef<String> = atomic("")
        val state = atomic(10)

        GlobalScope.async {
            handle<Unit, And<State<Int>, Log>> { (state, log) ->
                val value1 = !state.get
                !state.set(value1 + 32)
                val value2 = !state.get
                !log.log("Done with $value2")
            } with {
                State<Int>(
                    set = { value ->
                        { k ->
                            state.value = value
                            k.resume(Unit)
                        }
                    },
                    get = { k ->
                        k.resume(state.value)
                    }
                ) and Log { value ->
                    { k ->
                        log.getAndSet(log.value + value)
                        k.resume(Unit)
                    }
                }
            }
        }

        Await() atMost 5000 until { log.value == "Done with 42" }
    }
}
