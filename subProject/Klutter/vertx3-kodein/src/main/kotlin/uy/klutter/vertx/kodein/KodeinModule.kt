package uy.klutter.vertx.kodein

import com.fasterxml.jackson.databind.ObjectMapper
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

import uy.klutter.vertx.VertxInit
import uy.klutter.vertx.setupVertxLoggingToSlf4j
import org.slf4j.Logger as Slf4jLogger
import org.slf4j.LoggerFactory as Slf4jLoggerFactory

object KodeinVertx {
    /**
     * This Kodein Module does a few things:
     *
     * Make sure Vertx is setup to work with Kovenant for promises
     * Add a singleton for Jackson object mapper sharing it with the Vertx singleton
     * Setup logger to use the Vertx logging so logging is consistent with vertx
     */
    val module = Kodein.Module() {
        VertxInit.ensure()
        bind<ObjectMapper>() with singleton { io.vertx.core.json.Json.mapper }
        bind<io.vertx.core.logging.Logger>() with provider {
            receiver?. let {
                io.vertx.core.logging.LoggerFactory.getLogger(receiver!!.javaClass)
            } ?: throw IllegalStateException("Logger requires there to be a receiver, can only be used via delegate")
        }

        bind<io.vertx.core.logging.Logger>() with factory {
                name: String -> io.vertx.core.logging.LoggerFactory.getLogger(name)
        }
    }

    /**
     * Same as module above but routes Vert.x and Hazelcast logging facades to use Slf4j, and app directly to Slf4j
     */
    val moduleWithLoggingToSlf4j = Kodein.Module() {
        setupVertxLoggingToSlf4j()
        import(module)
        bind<org.slf4j.Logger>() with provider {
            receiver?. let {
                 Slf4jLoggerFactory.getLogger(receiver!!.javaClass)
            } ?: throw IllegalStateException("Logger requires there to be a receiver, can only be used via delegate")
        }
        bind<org.slf4j.Logger>() with factory { name: String -> Slf4jLoggerFactory.getLogger(name) }
    }
}

