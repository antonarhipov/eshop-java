package org.example.eshop

import org.example.eshop.service.ShopProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ShopProperties::class)
class EshopApplication

fun main(args: Array<String>) {
    runApplication<EshopApplication>(*args)
}
