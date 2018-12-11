package uy.klutter.vertx

import io.vertx.core.Context
import nl.komponents.kovenant.Dispatcher
import nl.komponents.kovenant.DispatcherContext


// -- connects the context of Kovenant promises to dispatch into Vert.x thread and context management.

internal class VertxKovenantContext(val originalContext: nl.komponents.kovenant.Context) : nl.komponents.kovenant.Context {
    override val callbackContext: DispatcherContext
        get() {
            val currentVertxContext = vertxContext()
            return if (currentVertxContext != null) {
                VertxCallbackDispatcherContext(currentVertxContext)
            } else {
                originalContext.callbackContext
            }
        }
    override val workerContext: DispatcherContext
        get() {
            val currentVertxContext = vertxContext()
            return if (currentVertxContext != null) {
                VertxWorkerDispatcherContext(currentVertxContext)
            } else {
                originalContext.workerContext
            }
        }

    override val multipleCompletion: (curVal: Any?, newVal: Any?) -> Unit
        get() = { curVal: Any?, newVal: Any? -> throw IllegalStateException("Value[$curVal] is set, can't override with new value[$newVal]") }
}

internal class VertxCallbackDispatcherContext(private val ctx: Context) : DispatcherContext {
    override val dispatcher: Dispatcher
        get() = VertxCallbackDispatcher(ctx)
    override val errorHandler: (Exception) -> Unit
        get() = throw UnsupportedOperationException()

}

internal class VertxCallbackDispatcher(private val ctx: Context) : BasicDispatcher() {
    override fun offer(task: () -> Unit): Boolean {
        ctx.runOnContext {
            task()
        }
        return true
    }

}

internal class VertxWorkerDispatcherContext(private val ctx: Context) : DispatcherContext {
    override val dispatcher: Dispatcher
        get() = VertxWorkerDispatcher(ctx)
    override val errorHandler: (Exception) -> Unit
        get() = throw UnsupportedOperationException()

}

internal class VertxWorkerDispatcher(private val ctx: Context) : BasicDispatcher() {
    override fun offer(task: () -> Unit): Boolean {
        ctx.owner().executeBlocking<Unit>({
            task()
        }, false, null)
        return true
    }
}

internal abstract class BasicDispatcher : Dispatcher {
    override fun stop(force: Boolean, timeOutMs: Long, block: Boolean): List<() -> Unit> = throw UnsupportedOperationException()
    override fun tryCancel(task: () -> Unit): Boolean = false
    override val terminated: Boolean
        get() = throw UnsupportedOperationException()
    override val stopped: Boolean
        get() = throw UnsupportedOperationException()
}




