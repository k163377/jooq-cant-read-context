package org.wrongwrong.jooqcantreadcontext

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JooqCantReadContextApplication

fun main(args: Array<String>) {
    runApplication<JooqCantReadContextApplication>(*args)
}
