package com.jelipo.cleanreactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CleanReactiveApplication

fun main(args: Array<String>) {

    runApplication<CleanReactiveApplication>(*args)
}
