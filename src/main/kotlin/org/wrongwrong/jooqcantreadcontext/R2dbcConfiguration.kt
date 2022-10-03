package org.wrongwrong.jooqcantreadcontext

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.r2dbc.OptionsCapableConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import reactor.core.publisher.Mono

const val KEY = "key"

fun readValue(): Mono<String> = Mono.deferContextual {
    Mono.just(it.get(KEY))
}

@Configuration
class R2dbcConfiguration : AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val h2UrlStr = "r2dbc:h2:mem:///testdb"

        val defaultConnectionFactory = ConnectionFactories.get(h2UrlStr)
        val config = ConnectionPoolConfiguration
            .builder(defaultConnectionFactory)
            .postAllocate { _ ->
                val valueMono: Mono<String> = readValue()

                valueMono
                    .doOnNext { println("success! $it") }
                    .doOnError { println("fail!") }
                    .onErrorComplete()
                    .then()
            }
            .build()

        return OptionsCapableConnectionFactory(
            ConnectionFactoryOptions.parse(h2UrlStr),
            ConnectionPool(config)
        )
    }
}
