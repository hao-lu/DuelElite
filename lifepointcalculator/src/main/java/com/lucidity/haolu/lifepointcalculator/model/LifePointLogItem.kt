package com.lucidity.haolu.lifepointcalculator.model

data class LifePointLogItem(
    private val player: String,
    private val actionLp: Int,
    private val totalLp: Int,
    private val time: String
)