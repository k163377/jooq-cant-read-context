package org.wrongwrong.jooqcantreadcontext

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.generated.tables.references.FOO_TABLE
import org.jooq.impl.DSL
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@SpringBootTest
class Test @Autowired constructor(private val cfi: ConnectionFactory) {
    @Test
    fun base() {
        val contextMono = Mono.deferContextual { readValue() }
            .contextWrite { it.put(KEY, "foo") }

        println(contextMono.block()) // -> foo
    }

    @Test
    fun success() {
        Mono.deferContextual {
            cfi.create().toMono()
                .map { DSL.using(it) }
                .flatMap { it.selectFrom(FOO_TABLE).toMono() }
        }
            .contextWrite { it.put(KEY, "bar") }
            .block()
    }

    @Test
    fun fail() {
        val create: DSLContext = DSL.using(cfi)

        Mono.deferContextual {
            create.selectFrom(FOO_TABLE).toMono()
        }
            .contextWrite { it.put(KEY, "baz") }
            .block()
    }
}
