package com.example.randomgenerator.model

class RandomGenerator {

    companion object {
        fun isHead(): Boolean = ((1..2).shuffled().first() == 1)

        fun roll(): Int = (1..6).random()
    }
}